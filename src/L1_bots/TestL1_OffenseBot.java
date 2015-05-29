/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L1_bots;

import L0_tools.L0_2dLib;
import java.util.ArrayList;
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
        List<L0_2dLib.WithCoord> spaceGeom = null;

        public void inputZonePostTrait() {
            sortedRzz = buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());
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
            List<AssistTag> otherAssistNeeded=new ArrayList<>(D);

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
        
        public class AssistTag{
            final Drone assitant;
            final Zone cible;

            public AssistTag(Drone assitant, Zone cible) {
                this.assitant = assitant;
                this.cible = cible;
            }

            @Override
            public String toString() {
                return "AssistTag{" + "a=" + assitant + ", c=" + cible + '}';
            }
            
            
        }

        public void opportunistAttack(boolean[] doneBot) {
            sortedRzd = _buildRZoneDrone().setDistanceCalc().stream().sorted(comp_rzd_byLevel.reversed().thenComparing(new CompByPlayerRzd(_me.id).reversed())).collect(Collectors.toList());
            buildDroneZoneInfo();

            List<Drone> done = new ArrayList<>(D);
            List<Zone> executed = new ArrayList<>(Z);
            
            HashMap<Drone,AssistTag> tagAgainst=new HashMap<>(D);

            for (Zone z : _zone) {
                zoneInfo.get(z).defCandidat.clear();
                zoneInfo.get(z).defender.clear();
                zoneInfo.get(z).attackCandidate.clear();
                zoneInfo.get(z).otherAssistNeeded.clear();
                //System.err.println("clearing stuff of zone "+z);
            }

            for (PlayerAI p : _player) {
                System.err.println("considering player "+p);
                if(p==_me) continue;
                for (RZoneDrone rzd : sortedRzd) {
                    System.err.println(""+rzd);               
                    Drone d = rzd.d;
                    Zone z = rzd.z;
                    if (executed.contains(z)) {
                        continue;
                    }

                    if (tagAgainst.containsKey(d)) {
                        System.err.println(""+d+" on "+z+" is already ++++++++++ tagged for use agaisnt "+tagAgainst.get(d));
                        zoneInfo.get(z).otherAssistNeeded.add(tagAgainst.get(d));
                        continue;
                    }
                    
                    if(z.owner!=p && z.owner!=_nullPlayer) continue; // on ne considere que ses zones

                    if (d.owner == p) {
                        zoneInfo.get(z).defCandidat.add(d);
                        done.add(d);
                    }
                    else if (d.owner == _me && doneBot[d.id]){
                        System.err.println("ignoring "+d+" as already in use for another mission");
                        continue;
                    }
                    else if (d.owner == _me && zoneInfo.get(z).defCandidat.isEmpty()) {
                        //Ennemy overruned
                        executed.add(z);
                        System.err.println("---TRYING Overunning ennemy at "+z+" "+zoneInfo.get(z)+" by "+d+" "+zoneInfo.get(z));
                        zoneInfo.get(z).attackCandidate.add(d);
                        
                        int doublons=0;
                        for(AssistTag at : zoneInfo.get(z).otherAssistNeeded){   
                            if(zoneInfo.get(z).attackCandidate.contains(at.assitant)){
                                doublons++;
                                System.err.println(" "+at.assitant+" needed as distraction !");
                            }
                        }
                        
                        System.err.println("att size "+zoneInfo.get(z).attackCandidate.size()+" doubling -"+doublons+" for overwhelm"+zoneInfo.get(z).defender.size());
                        if(zoneInfo.get(z).attackCandidate.size() - doublons > zoneInfo.get(z).defender.size()){
                            
                            System.err.println("++++++++ LAUNCHING ON "+z+"+++++++++++");
                            for(Drone att : zoneInfo.get(z).attackCandidate){
                                _order.get(d).set(z);
                                doneBot[d.id] = true;                    
                            }

                            for(AssistTag at : zoneInfo.get(z).otherAssistNeeded){
                                _order.get(at.assitant).set(at.cible);
                                doneBot[at.assitant.id] = true;    
                            }                              
                        }
                        
                        
                        


                        
                    } else if (d.owner == _me && !zoneInfo.get(z).defCandidat.isEmpty()) {
                        Drone dd = zoneInfo.get(z).defCandidat.get(0);
                        zoneInfo.get(z).defCandidat.remove(dd);
                        zoneInfo.get(z).defender.add(dd);
                        System.err.println("Hypothesing defending"+z+" with "+dd+" against "+d);
                        tagAgainst.put(dd, new AssistTag(d, z));
                    } else {
                        throw new RuntimeException("not possible "+d.owner+" "+zoneInfo.get(z).defCandidat.isEmpty()+" me is "+_me+"  currPlayer "+p);
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
            sortedRzd = _buildRZoneDrone().setDistanceCalc().stream().sorted(comp_rzd_byLevel.reversed().thenComparing(new CompByPlayerRzd(_me.id).reversed())).collect(Collectors.toList());
            buildDroneZoneInfo();

            List<Drone> done = new ArrayList<>(D);
            List<Zone> fail = new ArrayList<>(Z);

            for (Zone z : _zone) {
                zoneInfo.get(z).defCandidat.clear();
                zoneInfo.get(z).defender.clear();
            }

            for (RZoneDrone rzd : sortedRzd) {
                //System.err.println(""+rzd);

                Drone d = rzd.d;
                Zone z = rzd.z;
                if (fail.contains(z)) {
                    continue;
                }

                if (done.contains(d)) {
                    continue;
                }

                if (d.owner == _me) {
                    zoneInfo.get(z).defCandidat.add(d);
                } else if (d.owner != _me && zoneInfo.get(z).defCandidat.isEmpty()) {
                    //heeem ... failure.
                    fail.add(z);
                } else if (d.owner != _me && !zoneInfo.get(z).defCandidat.isEmpty() && zoneInfo.get(z).defender.size() < nbDroneDef) {
                    Drone dd = zoneInfo.get(z).defCandidat.get(0);
                    zoneInfo.get(z).defCandidat.remove(dd);
                    done.add(dd);
                    zoneInfo.get(z).defender.add(dd);
                        //System.err.println("Defending "+z+" with "+dd+" against "+d);
                    //System.err.println("Already defending "+done);
                    if (droneInfo.get(dd).meLevel.get(z) + 2 <= droneInfo.get(d).meLevel.get(z)) {
                        _order.put(dd, d.cor);
                        doneBot[dd.id] = true;
                    } else {
                        _order.put(dd, z.cor);
                        doneBot[dd.id] = true;
                    }
                } else {
                    fail.add(z);
                }

            }
        }

        public void reflechirTour() {
            boolean[] doneBot = new boolean[D];
            for (int i = 0; i < doneBot.length; i++) {
                doneBot[i] = false;
            }

            if (_me.owned.size() < 1) {
                opportunistAttack(doneBot);
                offenseSplit(doneBot);
            } else {
                //defenseDoing(doneBot);
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
