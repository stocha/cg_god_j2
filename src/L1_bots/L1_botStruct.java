/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L1_bots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import L0_tools.L0_2dLib;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 *
 * @author Jahan
 */
public class L1_botStruct {

    public static interface BotBridge {

        /**
         *
         * @param P
         * @param Id
         * @param xyZ Z*2 int (x,y)
         * @param Z
         * @param D
         */
        public void setup(int P, int Id, int D, int Z, List<L0_2dLib.Point> xyZ);

        /**
         *
         * @param zline Z int (owner)
         * @param droneLinesPerPlayer P*D*2 p-> d->int (x,y)
         */
        public void turn(int[] zline, List<List<L0_2dLib.Point>> droneLinesPerPlayer);

        /**
         * D*2 int (x,y)
         *
         * @return
         */
        public List<L0_2dLib.Point> outorders();

        public String botName();
    }

    public static interface BotFactory {

        BotBase alloc(int P, int Id, int D, int Z);
    }

    public static class BotBase {

        public static class CompByPlayerRzd implements Comparator<RZoneDrone> {

            final int firstPlayer;

            public CompByPlayerRzd(int firstPlayer) {
                this.firstPlayer = firstPlayer;
            }

            @Override
            public int compare(RZoneDrone e1, RZoneDrone e2) {
                int o1 = e1.d.owner.id;
                int o2 = e2.d.owner.id;

                if (o1 == o2) {
                    return 0;
                }
                if (o1 == firstPlayer) {
                    return -1;
                }
                if (o2 == firstPlayer) {
                    return 1;
                }

                return (int) (o2 - o1);
            }
        }

        public class RZoneDrone {

            final Zone z;
            final Drone d;
            double dist;
            int level;

            public RZoneDrone(Zone z, Drone d) {
                this.z = z;
                this.d = d;
            }

            public Zone getZ() {
                return z;
            }

            public Drone getD() {
                return d;
            }

            public double getDist() {
                return dist;
            }

            @Override
            public String toString() {
                return "Rzd{" + "z=" + z + ", d=" + d + ", " + dist + ", L=" + level + '}';
            }

        }

        public class RZoneDroneSet {

            final List<RZoneDrone> s = new ArrayList<>(D * P * Z);

            final public RZoneDroneSet setDistanceCalc() {
                for (RZoneDrone r : s) {
                    r.dist = r.z.dist(r.d);
                    r.level = (int) (r.dist - 1) / 100;
                }
                return this;
            }

            public Stream<RZoneDrone> stream() {
                return s.stream();
            }

        }

        Comparator<RZoneDrone> comp_rzd_byDist = (e1, e2) -> {
            return (int) (e2.dist - e1.dist);
        };

        Comparator<RZoneDrone> comp_rzd_byLevel = (e1, e2) -> {
            return (int) (e2.level - e1.level);

        };

        final public RZoneDroneSet _buildRZoneDrone() {
            RZoneDroneSet res = new RZoneDroneSet();

            for (Zone z : _zone) {
                for (PlayerAI p : _player) {
                    for (Drone d : _drone.get(p)) {
                        RZoneDrone rzd = new RZoneDrone(z, d);
                        res.s.add(rzd);
                    }
                }
            }
            return res;
        }

        public class RZoneZone implements L0_2dLib.WithCoord {

            final Zone a;
            final Zone b;
            double distance;
            int level;
            PlayerAI owner;

            public RZoneZone(Zone a, Zone b) {
                this.a = a;
                this.b = b;
            }

            final L0_2dLib.Point coord = new L0_2dLib.Point();

            public double x() {
                return coord.x();
            }

            public double y() {
                return coord.y();
            }

            public void set(L0_2dLib.WithCoord c) {
                throw new RuntimeException("no");
            }

            public void set(double x, double y) {
                throw new RuntimeException("no");
            }

            @Override
            public String toString() {
                return "RZoneZone{" + "" + a + ", " + b + ", d=" + distance + "," + coord + '}';
            }
        }

        public class RZoneZoneSet {

            List<RZoneZone> s = new ArrayList<>(Z * Z);

            final public RZoneZoneSet setDistance() {
                for (RZoneZone r : s) {
                    r.distance = r.a.dist(r.b);
                    r.coord.setAsBarycentre(r.a, 1, r.b, 1);
                    r.level = (int) (r.distance - 1) / 100;
                    if (r.a.owner == r.b.owner) {
                        r.owner = r.a.owner;
                    } else {
                        r.owner = _nullPlayer;
                    }
                }
                return this;
            }

            public Stream<RZoneZone> stream() {
                return s.stream();
            }

        }

        Comparator<RZoneZone> comp_zz_bydist = (e1, e2) -> {
            return (int) (e2.distance - e1.distance);
        };

        final public RZoneZoneSet _buildRZoneZone() {
            RZoneZoneSet res = new RZoneZoneSet();
            boolean[] zdone = new boolean[Z];
            for (int i = 0; i < Z; i++) {
                zdone[i] = false;
            }

            for (Zone a : _zone) {
                zdone[a.id] = true;
                for (Zone b : _zone) {
                    if (zdone[b.id]) {
                        continue;
                    }
                    res.s.add(new RZoneZone(a, b));
                }
            }
            return res;

        }

        public class Zone implements L0_2dLib.WithCoord {

            final int id;
            final L0_2dLib.Point cor;
            PlayerAI owner;

            public Zone(int id, PlayerAI owner) {
                this.id = id;
                this.cor = new L0_2dLib.Point();
                this.owner = owner;
            }

            public String toString() {
                return "{Z" + id + cor.toString() + "}";
            }

            public double x() {
                return cor.x();
            }

            public double y() {
                return cor.y();
            }

            public double sqDist(L0_2dLib.WithCoord c) {
                return cor.sqDist(c);
            }

            public double dist(L0_2dLib.WithCoord c) {
                return cor.dist(c);
            }

            @Override
            public void set(L0_2dLib.WithCoord c) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void set(double x, double y) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        }

        public class Drone implements L0_2dLib.WithCoord {

            final int id;
            final PlayerAI owner;

            final L0_2dLib.Point cor;

            public Drone(PlayerAI owner, int id) {
                this.owner = owner;
                this.id = id;
                this.cor = new L0_2dLib.Point();
            }

            public String toString() {
                return "{D" + id + cor.toString() + "" + owner + "" + "}";
            }

            public double x() {
                return cor.x();
            }

            public double y() {
                return cor.y();
            }

            public double sqDist(L0_2dLib.WithCoord c) {
                return cor.sqDist(c);
            }

            public double dist(L0_2dLib.WithCoord c) {
                return cor.dist(c);
            }

            @Override
            public void set(L0_2dLib.WithCoord c) {
                this.cor.set(c);
            }

            @Override
            public void set(double x, double y) {
                this.cor.set(x, y);
            }
        }

        public class PlayerAI {

            final int id;
            int score = 0;
            List<Zone> owned=new ArrayList<>(Z);

            public PlayerAI(int id) {
                this.id = id;
            }

            @Override
            public String toString() {
                return "P{" + "id=" + id + "|sc" + score + '}';
            }

        }

        final int P;
        final int Id;
        final int D;
        final int Z;

        final PlayerAI _nullPlayer = new PlayerAI(-1);
        final PlayerAI _me;
        final List<PlayerAI> _player;
        final List<Zone> _zone;
        final HashMap<PlayerAI, List<Drone>> _drone;
        final HashMap<Drone, L0_2dLib.Point> _order;
        int _turnNumber = 0;
        private final List<L0_2dLib.Point> res;

        public BotBase(int P, int Id, int D, int Z) {
            this.P = P;
            this.Id = Id;
            this.D = D;
            this.Z = Z;

            _player = new ArrayList<>(P);
            for (int p = 0; p < P; p++) {
                _player.add(new PlayerAI(p));
            }
            _me = _player.get(Id);

            _zone = new ArrayList<>(Z);
            for (int z = 0; z < Z; z++) {
                _zone.add(new Zone(z, _nullPlayer));
            }

            _drone = new HashMap<>(P);

            for (PlayerAI p : _player) {                
                List<Drone> bbl = new ArrayList<>(D);
                for (int d = 0; d < D; d++) {
                    Drone bb = new Drone(p, d);
                    bbl.add(bb);
                }
                _drone.put(p, bbl);

            }

            _order = new HashMap<>(D);
            for (Drone d : _drone.get(_me)) {
                _order.put(d, new L0_2dLib.Point());
            }

            res = new ArrayList<>(D);
            for (int d = 0; d < D; d++) {
                res.add(new L0_2dLib.Point());
            }          
        }

        public void inputZones(List<L0_2dLib.Point> xyZ) {
            _turnNumber = 0;

            for (Zone z : _zone) {
                z.cor.set(xyZ.get(z.id));
                //System.err.println("Inputing zone "+z+" from"+xyZ);
            }

        }

        public void inputTurnZonesOwner(int[] owners) {

            for (PlayerAI p : _player) {
                p.owned.clear();
            }
            _nullPlayer.owned.clear();
            for (Zone z : _zone) {
                if (owners[z.id] == -1) {
                    z.owner = _nullPlayer;
                } else {
                    z.owner = _player.get(owners[z.id]);
                }

                z.owner.owned.add(z);
            }
        }

        public void inputTurnPlayerBot(int p, List<L0_2dLib.Point> xyZ) {
            PlayerAI pp = _player.get(p);
            for (Drone d : _drone.get(pp)) {
                d.set(xyZ.get(d.id));
            }
        }

        public List<L0_2dLib.Point> outorders() {

            for (Drone d : _drone.get(_me)) {
                res.set(d.id, _order.get(d));
            }

            _turnNumber++;
            return res;
        }

    }

    public static class BotBridgeImpl implements BotBridge {

        final BotFactory fact;
        BotBase bot = null;

        public BotBridgeImpl(BotFactory fact) {
            this.fact = fact;
        }

        @Override
        public void setup(int P, int Id, int D, int Z, List<L0_2dLib.Point> xyZ) {

            bot = fact.alloc(P, Id, D, Z);
            bot.inputZones(xyZ);
            System.err.println("Seting up of " + bot.getClass().getSimpleName());
        }

        @Override
        public void turn(int[] zline, List<List<L0_2dLib.Point>> droneLinesPerPlayer) {
            long t0 = System.currentTimeMillis();

            bot.inputTurnZonesOwner(zline);

            for (int i = 0; i < droneLinesPerPlayer.size(); i++) {
                bot.inputTurnPlayerBot(i, droneLinesPerPlayer.get(i));
            }

            long t1 = System.currentTimeMillis();

            System.gc();
            long t = t1 - t0;
            System.err.println("" + t + " ms");
        }

        @Override
        public List<L0_2dLib.Point> outorders() {
            return bot.outorders();
        }

        @Override
        public String botName() {
//            if(true) return "mockedName";
            return bot.getClass().getSimpleName();
        }
    }

    public static class BotBridgeGodIn {

        final BotBase theBot;

        final Scanner in;

        final int ID;
        final int D;
        final int P;
        final int Z;

        private final L0_2dLib.Point[] _worldZoneCoord;

        int _turn_Number;
        final int[] _turn_scoreControl;

        final int[] _owner;
        private final L0_2dLib.Point[][] _playerDronesCords;
        final L0_2dLib.Point[] _orders;

        public BotBridgeGodIn(InputStream inst, BotFactory fact) {

            this.in = new Scanner(inst);
            P = in.nextInt(); // number of players in the game (2 to 4 players)
            ID = in.nextInt(); // ID of your player (0, 1, 2, or 3)
            D = in.nextInt(); // number of drones in each team (3 to 11)
            Z = in.nextInt(); // number of zones on the map (4 to 8)      

            theBot = fact.alloc(P, ID, D, Z);

            _worldZoneCoord = new L0_2dLib.Point[Z];
            for (int i = 0; i < Z; i++) {
                int X = in.nextInt(); // corresponds to the position of the center of a zone. A zone is a circle with a radius of 100 units.
                int Y = in.nextInt();
                _worldZoneCoord[i] = new L0_2dLib.Point(X, Y);
            }

            _owner = new int[Z];
            _playerDronesCords = new L0_2dLib.Point[P][];
            for (int p = 0; p < P; p++) {
                _playerDronesCords[p] = new L0_2dLib.Point[D];
                for (int d = 0; d < D; d++) {
                    _playerDronesCords[p][d] = new L0_2dLib.Point(0, 0);
                }
            }

            _orders = new L0_2dLib.Point[D];
            for (int d = 0; d < D; d++) {
                _orders[d] = new L0_2dLib.Point(20, 20);
            }

            _turn_scoreControl = new int[P];

            theBot.inputZones(Arrays.asList(_worldZoneCoord));
        }

        public void readTurn() {

            for (int i = 0; i < Z; i++) {
                int own = in.nextInt();
                _owner[i] = own;
            }

            theBot.inputTurnZonesOwner(_owner);

            for (int i = 0; i < P; i++) {

                for (int j = 0; j < D; j++) {
                    _playerDronesCords[i][j].set(in.nextInt(), in.nextInt());
                }

                theBot.inputTurnPlayerBot(i, Arrays.asList(_playerDronesCords[i]));
            }
        }

        public void writeOrders(PrintStream out) {

            List<L0_2dLib.Point> orders = theBot.outorders();
            for (int d = 0; d < D; d++) {
                _orders[d].set(orders.get(d));
            }

            for (int d = 0; d < D; d++) {
                out.println("" + (int) _orders[d].x + " " + (int) _orders[d].y);
            }

            _turn_Number++;
        }

    }

}
