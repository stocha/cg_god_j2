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

        public class RDroneDrone {

            final Drone friend;
            final Drone foe;

            public RDroneDrone(Drone friend, Drone foe) {
                this.friend = friend;
                this.foe = foe;
            }

            public double getDist() {
                return dist;
            }

            double dist;

            @Override
            public String toString() {
                return "RZoneDrone{" + "f=" + friend + ", e=" + foe + ", dist=" + dist + '}';
            }

        }

        public class RZoneDrone {

            final Zone z;
            final Drone d;

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

            double dist;

            @Override
            public String toString() {
                return "RZoneDrone{" + "z=" + z + ", d=" + d + ", dist=" + dist + '}';
            }

        }

        Comparator<RZoneDrone> byZoneDist = (e1, e2) -> {
            if (e2.z != e1.z) {
                return e2.z.id - e1.z.id;
            } else {
                return (int) (e2.dist - e1.dist);
            }
        };
        Comparator<RZoneDrone> byDroneDist = (e1, e2) -> {
            if (e2.d != e1.d) {
                return e2.d.id - e1.d.id;
            } else {
                return (int) (e2.dist - e1.dist);
            }
        };
        Comparator<RZoneDrone> byDist = (e1, e2) -> {
            return (int) (e2.dist - e1.dist);
        };
        
        Comparator<RDroneDrone> rDroneDronebyDist = (e1, e2) -> {
            return (int) (e2.dist - e1.dist);
        };        

        final public List<RZoneDrone> buildRZoneDrone() {
            List<RZoneDrone> res = new ArrayList<>(D * P * Z);

            for (Zone z : _zone) {
                for (PlayerAI p : _player) {
                    for (Drone d : _drone.get(p)) {
                        RZoneDrone rzd = new RZoneDrone(z, d);
                        res.add(rzd);
                    }
                }
            }
            
    
            return res;
        }
        
        final private void rZoneDrone_setDistanceCalc(){
            for (RZoneDrone r : _rzonedrone) {
                r.dist = r.z.dist(r.d);
            }                    
        }

        final public List<RDroneDrone> buildRDroneDrone() {
            List<RDroneDrone> res = new ArrayList<>(D * D *P);
            
            for (Drone m : _drone.get(_me)) {
                for (PlayerAI p : _player) {
                    if(p==_me) continue;
                    for (Drone d : _drone.get(p)) {
                        RDroneDrone rzd = new RDroneDrone(m, d);
                        res.add(rzd);
                    }
                }
            }            

            return res;
        }
          final public void rDroneDrone_setDistanceCalc(List<RDroneDrone> it){
            for (RDroneDrone r : it) {
                r.dist = r.friend.dist(r.foe);
            }                    
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
        final HashMap<PlayerAI, List<Zone>> _controled;
        final HashMap<Drone, L0_2dLib.Point> _order;
        int _turnNumber = 0;
        private final List<L0_2dLib.Point> res;

        final List<RZoneDrone> _rzonedrone;

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

            _controled = new HashMap<>(P);
            for (PlayerAI p : _player) {
                _controled.put(p, new ArrayList<>());
            }
            _controled.put(_nullPlayer, new ArrayList<>());

            _order = new HashMap<>(D);
            for (Drone d : _drone.get(_me)) {
                _order.put(d, new L0_2dLib.Point());
            }

            res = new ArrayList<>(D);
            for (int d = 0; d < D; d++) {
                res.add(new L0_2dLib.Point());
            }

            _rzonedrone = buildRZoneDrone();
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
                _controled.get(p).clear();
            }
            for (Zone z : _zone) {
                if (owners[z.id] == -1) {
                    z.owner = _nullPlayer;
                } else {
                    z.owner = _player.get(owners[z.id]);
                }

                _controled.get(z.owner).add(z);
            }
        }

        public void inputTurnPlayerBot(int p, List<L0_2dLib.Point> xyZ) {
            PlayerAI pp = _player.get(p);
            for (Drone d : _drone.get(pp)) {
                d.set(xyZ.get(d.id));
            }
            
            rZoneDrone_setDistanceCalc();       
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
