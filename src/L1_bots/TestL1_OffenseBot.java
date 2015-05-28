/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L1_bots;

import L0_tools.L0_2dLib;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class TestL1_OffenseBot extends L1_botStruct.BotBase {

    public class IA {

        List<L1_botStruct.BotBase.RZoneDrone> sortedRzd = null;
        List<RZoneZone> sortedRzz = null;
        List<RZoneB> sortedRzb = null;
        List<L0_2dLib.WithCoord> spaceGeom = null;

        public void inputZonePostTrait() {
            List<RZoneZone> rzz = buildRZoneZone();
            rZoneZone_setDistance(rzz);
            sortedRzz = rzz.stream().sorted(byDistrzz.reversed()).collect(Collectors.toList());

            List<RZoneB> rzb = buildRZoneB(sortedRzz);
            rZoneB_setDistance(rzb);
            sortedRzb = rzb.stream().sorted(byDistrzb.reversed()).collect(Collectors.toList());
        }

        public class RZoneB implements L0_2dLib.WithCoord {

            final Zone a;
            final RZoneZone b;

            public RZoneB(Zone a, RZoneZone b) {
                this.a = a;
                this.b = b;
            }

            double distance;
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
                return "RZoneB{" + "" + a + ", " + b + ", d=" + distance + "," + coord + '}';
            }

        }

        Comparator<RZoneB> byDistrzb = (e1, e2) -> {
            return (int) (e2.distance - e1.distance);
        };

        public class RZoneZone implements L0_2dLib.WithCoord {

            final Zone a;
            final Zone b;

            public RZoneZone(Zone a, Zone b) {
                this.a = a;
                this.b = b;
            }

            double distance;
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

        Comparator<RZoneZone> byDistrzz = (e1, e2) -> {
            return (int) (e2.distance - e1.distance);
        };

        final public List<RZoneZone> buildRZoneZone() {
            List<RZoneZone> res = new ArrayList<>(Z * Z);
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
                    res.add(new RZoneZone(a, b));
                }
            }
            return res;

        }

        final public List<RZoneB> buildRZoneB(List<RZoneZone> bar) {
            List<RZoneB> res = new ArrayList<>(Z * Z);

            for (Zone a : _zone) {
                for (RZoneZone b : bar) {
                    res.add(new RZoneB(a, b));
                }
            }
            return res;
        }

        final public void rZoneZone_setDistance(List<RZoneZone> in) {
            for (RZoneZone r : in) {
                r.distance = r.a.dist(r.b);
                r.coord.setAsBarycentre(r.a, 1, r.b, 1);
            }
        }

        final public void rZoneB_setDistance(List<RZoneB> in) {
            for (RZoneB r : in) {
                r.distance = r.a.dist(r.b);
                r.coord.setAsBarycentre(r.a, 1, r.b, 1);
            }
        }

        public class DroneInfo {

            HashMap<L1_botStruct.BotBase.Zone, Integer> meLevel = new HashMap<>(Z);

            @Override
            public String toString() {
                return "DI{" + "meLevel=" + meLevel + '}';
            }

        }
        HashMap<L1_botStruct.BotBase.Drone, DroneInfo> droneInfo = new HashMap<>(D);

        public class ZoneInfo {
            List<L1_botStruct.BotBase.Drone> defender = new ArrayList<>(D);
            List<L1_botStruct.BotBase.Drone> defCandidat = new ArrayList<>(D);
            List<L1_botStruct.BotBase.Drone> attackCandidate = new ArrayList<>(D);

            @Override
            public String toString() {
                return "ZI{" + " def" + defCandidat + " att " + attackCandidate;

                //+ "clo=" + closest + "" + '}';
            }

        }
        HashMap<L1_botStruct.BotBase.Zone, ZoneInfo> zoneInfo = new HashMap<>(D);

        final int nbDroneDef;

        public IA(int nbDroneDef) {
            this.nbDroneDef = nbDroneDef;

        }

        private void buildDroneZoneInfo() {       

            for (L1_botStruct.BotBase.PlayerAI p : _player) {
                for (L1_botStruct.BotBase.Drone d : _drone.get(p)) {
                    droneInfo.put(d, new DroneInfo());
                }
            }
            for (L1_botStruct.BotBase.Zone z : _zone) {
                zoneInfo.put(z, new ZoneInfo());
            }

            for (L1_botStruct.BotBase.RZoneDrone rdz : sortedRzd) {
                droneInfo.get(rdz.d).meLevel.put(rdz.z, rdz.level);

                // System.err.println(""+rdz);
            }

        }

        public void opportunistAttack(boolean[] doneBot) {
            sortedRzd= _rzonedrone.stream().sorted(byLevel.reversed().thenComparing(new ByPlayerRzd(_me.id))).collect(Collectors.toList())            ;            
            
            buildDroneZoneInfo();
            List<L1_botStruct.BotBase.Drone> done = new ArrayList<>(D);
            List<Zone> attacked = new ArrayList<>();

            for (PlayerAI p : _player) {
                if (p == _me) {
                    continue;
                }

                int lvl = 0;
                //System.err.println("Offense lvl 0");
                for (L1_botStruct.BotBase.RZoneDrone rzd : sortedRzd) {
                    if (rzd.z.owner != p) {
                        continue;
                    }

                    int nlv = rzd.level;
                    if (lvl != nlv) {
                        //System.err.println("Offense lvl "+nlv+" fermeture "+lvl);
                        lvl = nlv;

                        for (Zone z : _zone) {
                            if (z.owner != p) {
                                continue;
                            }
                            if (attacked.contains(z)) {
                                continue;
                            }

                            if (zoneInfo.get(z).attackCandidate.size() >= zoneInfo.get(z).defCandidat.size() && zoneInfo.get(z).defCandidat.size() <= D / 2) {
                                //System.err.println("Assault on "+z+" "+zoneInfo.get(z));
                                for (Drone att : zoneInfo.get(z).attackCandidate) {
                                    _order.get(att).set(z);
                                    doneBot[att.id] = true;
                                }
                                attacked.add(z);

                            }

                        }

                    }
                    L1_botStruct.BotBase.Drone d = rzd.d;
                    L1_botStruct.BotBase.Zone z = rzd.z;

                    if (done.contains(d)) {
                        continue;
                    }
                    if (d.owner == _me && doneBot[d.id]) {
                        continue;
                    }

                    if (d.owner == p) {
                        zoneInfo.get(z).defCandidat.add(d);
                        done.add(d);
                    } else if (d.owner == _me) {
                        zoneInfo.get(z).attackCandidate.add(d);

                    }

                } // fin Zone / drone

                //System.err.println("Offense lvl "+lvl+" fermeture "+lvl);
                for (Zone z : _zone) { // level final
                    if (z.owner != p) {
                        continue;
                    }
                    if (zoneInfo.get(z).attackCandidate.size() > zoneInfo.get(z).defCandidat.size()) {
                        for (Drone att : zoneInfo.get(z).attackCandidate) {
                            _order.get(att).set(z);
                            doneBot[att.id] = true;
                        }

                    }

                }
            }

        }

        public void offenseSplit(boolean[] doneBot) {
            L0_2dLib.WithCoord cible = null;

            List<L0_2dLib.WithCoord> geoPoint = new ArrayList<>(Z * Z * Z);
            geoPoint.addAll(sortedRzz);
            //geoPoint.addAll(sortedRzb);

            HashMap<L1_botStruct.BotBase.Drone, Boolean> droneDone = new HashMap(D);
            HashMap<L0_2dLib.WithCoord, Boolean> geoDone = new HashMap(Z * Z * Z);

            for (L0_2dLib.WithCoord geo : geoPoint) {
                //System.err.println(""+geo);
                Drone dcand = null;
                double dMin = Double.MAX_VALUE;
                for (Drone d : _drone.get(_me)) {
                    if (doneBot[d.id]) {
                        continue;
                    }

                    if (droneDone.containsKey(d) || geoDone.containsKey(geo)) {
                        continue;
                    }
                    double dist = d.dist(geo);
                    if (dist < dMin) {
                        dist = dMin;
                        dcand = d;
                    }
                }
                if (dcand != null) {
                    droneDone.put(dcand, true);
                    geoDone.put(geo, true);
                    _order.get(dcand).set(geo);
                    doneBot[dcand.id] = true;
                }
            }
        }

        public void defenseDoing(boolean[] doneBot) {
            sortedRzd= _rzonedrone.stream().sorted(byLevel.reversed().thenComparing(new ByPlayerRzd(_me.id).reversed())).collect(Collectors.toList())            ;            
            buildDroneZoneInfo();
                        

            
            List<Drone> done=new ArrayList<>(D);     
            List<Zone> fail=new ArrayList<>(Z);
            
            for(Zone z : _zone){
                 zoneInfo.get(z).defCandidat.clear();
                 zoneInfo.get(z).defender.clear();
            }
            
            for(RZoneDrone rzd : sortedRzd){
                //System.err.println(""+rzd);

                
                Drone d = rzd.d;
                Zone z = rzd.z;
                                if(fail.contains(z)) continue;
                
                    if(done.contains(d)) continue;
                    
                    if(d.owner==_me){
                        zoneInfo.get(z).defCandidat.add(d);
                    }else
                    if(d.owner!=_me && zoneInfo.get(z).defCandidat.isEmpty()){
                        //heeem ... failure.
                        fail.add(z);
                    }else if(d.owner!=_me && !zoneInfo.get(z).defCandidat.isEmpty() && zoneInfo.get(z).defender.size()<nbDroneDef){
                        Drone dd = zoneInfo.get(z).defCandidat.get(0);
                        zoneInfo.get(z).defCandidat.remove(dd);
                        done.add(dd);
                        zoneInfo.get(z).defender.add(dd);
                        //System.err.println("Defending "+z+" with "+dd+" against "+d);
                        //System.err.println("Already defending "+done);
                        if(droneInfo.get(dd).meLevel.get(z) +2 <= droneInfo.get(d).meLevel.get(z)){
                            _order.put(dd, d.cor);
                            doneBot[dd.id]=true;
                        }else{
                            _order.put(dd, z.cor);
                            doneBot[dd.id]=true;
                        }
                    }      else{
                        fail.add(z);
                    }
            
            }                                 
        }

        public void reflechirTour() {
            boolean[] doneBot = new boolean[D];
            for (int i = 0; i < doneBot.length; i++) {
                doneBot[i] = false;
            }

            if (_controled.get(_me).size() < 1) {
                opportunistAttack(doneBot);
                offenseSplit(doneBot);
            } else {
                defenseDoing(doneBot);
                opportunistAttack(doneBot);
                offenseSplit(doneBot);
            }

        }

    }

    private final IA ia;

    public TestL1_OffenseBot(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
        ia = new IA(D / 3);
    }

    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL1_OffenseBot(P1, Id1, D1, Z1);

    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ); //To change body of generated methods, choose Tools | Templates.
        ia.inputZonePostTrait();
    }

    @Override
    public List<L0_2dLib.Point> outorders() {
        ia.reflechirTour();
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

}
