/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L1_bots;

import L0_tools.L0_2dLib;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class TestL1_OffenseV2_3 extends L1_botStruct.BotBase {

    public TestL1_OffenseV2_3(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }

    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL1_OffenseV2_3(P1, Id1, D1, Z1);

    int DronePerPlanet = D / 3;
    List<Drone> attDrones = new ArrayList<>(D);

    public class ZoneDef {

        List<Drone> defDrone = new ArrayList<>(D);

        @Override
        public String toString() {
            return "ZoneDef{" + "" + defDrone + '}';
        }

    }

    HashMap<Zone, ZoneDef> zoneDefInfo = new HashMap<>(Z);
    HashMap<Drone, Zone> droneDefInfo = new HashMap<>(D);

    HashMap<Zone, Boolean> ownedPrev = new HashMap<>(Z);

    RZoneDroneSet rzdStruct = null;

    boolean init = false;

    Random rand = new Random(D * Z * _me.id * 8389);
    int fixRa = rand.nextInt() & 0xFFFF;

    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ); //To change body of generated methods, choose Tools | Templates.

        for (Zone z : _zone) {
            zoneDefInfo.put(z, new ZoneDef());
        }

        rzdStruct = _buildRZoneDrone();
    }
    
    private void releaseDefender(Zone z,Drone d){
        if(!zoneDefInfo.get(z).defDrone.contains(d)) throw new RuntimeException("Not in defenseur");
        droneDefInfo.remove(d);
        zoneDefInfo.get(z).defDrone.remove(d);
        attDrones.add(d);
    }

    public void conquestTest() {
        //System.err.println("owned prev "+ownedPrev);
        for (Zone z : _zone) {
            if (ownedPrev.get(z) != (z.owner == _me)) {
                if (z.owner == _me) {
                    //conquest
                    //System.err.println("Conquest " + z);

                    int defd = DronePerPlanet;
                    for (RZoneDrone rzd : rzdStruct.stream()
                            .filter(e -> e.d.owner == _me && attDrones.contains(e.d) && e.z == z)
                            .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList())) {

                        zoneDefInfo.get(z).defDrone.add(rzd.d);
                        droneDefInfo.put(rzd.d,z);
                        defd--;
                        if (defd == 0) {
                            break;
                        }
                    }
                    attDrones.removeAll(zoneDefInfo.get(z).defDrone);

                } else {
                    // lost
                    //System.err.println("Lost " + z);
                    attDrones.addAll(zoneDefInfo.get(z).defDrone);
                    for(Drone d : zoneDefInfo.get(z).defDrone){
                        droneDefInfo.remove(d);
                    }
                    zoneDefInfo.get(z).defDrone.clear();
                    //System.err.println("Att drones " + attDrones);

                }

            }
            ownedPrev.put(z, z.owner == _me);
        }
        
//        List<RZoneDrone> defDrones = rzdStruct.stream().filter(e -> zoneDefInfo.get( e.z).defDrone.contains(e.d))
//                    .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList());        
//        

    }

    private String arrayToString(int[] it) {
        String r = "";
        for (int i = 0; i < it.length; i++) {
            r += "|" + it[i];
        }
        return r;
    }

    public void defendMonoworld(HashSet<Drone> lockedDrones) {

        HashMap<Zone, ThreatLevel> threat = rzdStruct.setDistanceCalc().getThreat(e -> !lockedDrones.contains(e.d));

        ///-------------- Classer les defenseurs en free / stuck / retreat
        HashSet<Drone> freeDrone = new HashSet<>();
        HashSet<Drone> stuckDrone = new HashSet<>();
        HashSet<Drone> retreatDrone = new HashSet<>();

        List<Drone> freeDefender = new ArrayList<>();

        for (Zone z : _zone) {
            if (z.owner != _me) {
                continue;
            }
            //System.err.println("Defenders "+z+" : "+zoneDefInfo.get(z).defDrone);

            int[] maxThreat = threat.get(z).getMaxThreat(_me);

            //System.err.println("" + z + "\n" + threat.get(z));
            //System.err.println("maxthreat[] " + arrayToString(maxThreat));
            List<RZoneDrone> defDrones = rzdStruct.stream().filter(e -> e.d.owner == _me && e.z == z && zoneDefInfo.get(z).defDrone.contains(e.d))
                    .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList());
            int nbDrone = zoneDefInfo.get(z).defDrone.size();
            int nbDroneLeft = nbDrone;
            for (RZoneDrone rzd : defDrones) {
                int nbUsed = nbDrone - nbDroneLeft;
                Drone d = rzd.d;
                int level = rzd.level;
                if (level >= maxThreat.length - 3) {
                    break;
                }
                int currLevelCp = maxThreat[level];
                int nextLevelCp = maxThreat[level + 1];
                int futurLevelCp = maxThreat[level + 2];

                //System.err.println("Considering " + rzd.d + " at " + z + " level " + rzd.level + " knowing " + currLevelCp + "/ Left " + nbDroneLeft + " used " + nbUsed);
                if (currLevelCp > nbDrone) {
                    //System.err.println("Defense is breaking : release drones");
                }

                if (currLevelCp <= nbUsed && nextLevelCp <= nbUsed && futurLevelCp <= nbUsed) {
                    //System.err.println("" + d + " is considred free");
                    freeDrone.add(d);
                    nbDroneLeft--;
                } else if (currLevelCp <= nbUsed && nextLevelCp <= nbUsed) {
                    //System.err.println("" + d + " is considred stuck");
                    stuckDrone.add(d);
                    nbDroneLeft--;
                } else {
                    //System.err.println("" + d + " is considred retreat");
                    retreatDrone.add(d);
                    nbDroneLeft--;
                }
            }

            for (Drone d : zoneDefInfo.get(z).defDrone) {

                if (freeDrone.contains(d)) {
                    freeDefender.add(d);

                } else if (retreatDrone.contains(d)) {
                    _order.get(d).set(z.cor);
                } else {
                    _order.get(d).set(d.cor);
                }

            }

        }// fin zone

        attackWithDef(freeDefender, lockedDrones);

    }

    public void attackWithDef(List<Drone> them, HashSet<Drone> lockDrone) {

        List<RZoneZone> zz = _buildRZoneZone().setDistance().stream().filter(
                e -> ((e.a.owner == _me) || (e.b.owner == _me)) && ((e.a.owner != _me) || (e.b.owner != _me))
        ).sorted(comp_zz_bydist).collect(Collectors.toList());

        if (zz.size() > 0) {
            Zone targ = zz.get(0).a;
            if (targ.owner == _me) {
                targ = zz.get(0).b;
            }
            for (Drone d : them) {
                _player.add(_nullPlayer);
                for (PlayerAI p : _player) {
                    if (p == _me) {
                        continue;
                    }
                    if (p.owned.size() > 0) {
                        
                        if( droneDefInfo.containsKey(d) && d.dist(droneDefInfo.get(d))>0.5*droneDefInfo.get(d).dist(targ.cor)){
                            releaseDefender(droneDefInfo.get(d), d);
                        }else
                            _order.get(d).set(targ.cor);                        
                        //System.err.println("def Attacking " + targ+"  "+them);
                    }
                }
                _player.remove(_nullPlayer);
            }
        }

    }

    public void placementAttack(HashSet<Drone> inuseDrones) {

        //System.err.println("Placement "+attDrones+"  inuse :  "+inuseDrones);
        List<RZoneZone> zz = _buildRZoneZone().setDistance().stream().filter(
                e -> ((e.a.owner == _me) || (e.b.owner == _me)) && ((e.a.owner != _me) || (e.b.owner != _me))
        ).sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());
        final Zone targ;
        if (zz.size() > 0) {
            Zone targProv;
            targProv = zz.get(0).a;
            if (targProv.owner == _me) {
                targProv = zz.get(0).b;
            }
            targ = targProv;

            //System.err.println("Selected "+zz.get(0)+" targ (provi) is "+targ);
        } else {
            zz = _buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());

            if (zz.size() > 0) {
                Zone targProv;
                targProv = zz.get(0).a;
                if (targProv.owner == _me) {
                    targProv = zz.get(0).b;
                }
                targ = targProv;

                //System.err.println("Selected "+zz.get(0)+" targ (provi) is "+targ);
            } else {
                return;

            };

        };

        zz = _buildRZoneZone().setDistance().stream().filter(
                e -> (((e.a == targ) || (e.b == targ)) && ((e.a.owner != _me) && (e.b.owner != _me)))
        ).sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());

        if (zz.size() > 0) {
        } else {
            return;
        }

        //System.err.println("Sending to "+zz.get(0));
        for (Drone d : attDrones) {
            if (inuseDrones.contains(d)) {
                continue;
            }

            if (d.dist(zz.get(0)) < 50) {
                int toss = rand.nextInt() & 0x01;

                if (toss == 0) {
                    _order.get(d).set(zz.get(0).a);
                } else {
                    _order.get(d).set(zz.get(0).b);
                }
            } else {
                _order.get(d).set(zz.get(0).coord);
            }
        }
    }

    public void detectLocked(HashSet lockedWorld, HashSet<Drone> lockedDrones) {
        HashMap<Zone, ThreatLevel> threat = rzdStruct.setDistanceCalc().getThreat(e -> true);

        for (Zone z : _zone) {
            int countMax0 = 0;
            int max = threat.get(z).getMaxThreat(_nullPlayer)[0];
            ThreatLevel tl = threat.get(z);
            for (PlayerAI p : _player) {
                if (p == _me) {
                    continue;
                }
                if (max == tl.getThreatAt(p, 0)) {
                    countMax0++;
                }
            }
            if (countMax0 > 1 && max > 0) {
                lockedWorld.add(z);
            }
        }

        List<RZoneDrone> rzb = rzdStruct.stream().filter(e -> lockedWorld.contains(e.z)).sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList());

        for (RZoneDrone r : rzb) {
            if (r.d.owner == _me) {
                continue;
            }
            int level = r.level;
            if (level == 0) {
                //System.err.println("Adding "+r.d+" to ignored locks");
                lockedDrones.add(r.d);
            }
        }

    }

    public void greedyThem(HashSet<Drone> inuseDrones, HashSet lockedWorld) {

        HashSet<Drone> droneDone = new HashSet(D);
        
        if(_me.owned.size()>0) return;

        L0_2dLib.Point bary = L0_2dLib.baryCentre(_drone.get(_me));
        for (Drone d : _drone.get(_me)) {

            if (d.owner == _me && !droneDone.contains(d) && !inuseDrones.contains(d)) {//&& rzd.z.owner!=_me
                _order.get(d).set(bary);
                //System.err.println(""+rzd.d+" is heading to "+rzd.z);
                droneDone.add(d);
                inuseDrones.add(d);
            }
        }

//        for(RZoneDrone rzd : _buildRZoneDrone().setDistanceCalc().stream().filter((e)->e.z.owner==_nullPlayer && !lockedWorld.contains(e.z) ).sorted(comp_rzd_byDist.reversed()).collect(Collectors.toList())){
//           // System.err.println(""+rzd);
//            
//            if(rzd.d.owner==_me && !droneDone.contains(rzd.d)){//&& rzd.z.owner!=_me
//                _order.get(rzd.d).set(rzd.z);
//                //System.err.println(""+rzd.d+" is heading to "+rzd.z);
//                droneDone.add(rzd.d);
//                inuseDrones.add(rzd.d);
//            }        
//        }
    }

    public void attackOpportunist(HashSet<Drone> inuseDrones, HashSet<Drone> lockedDrones) {

        HashMap<Zone, ThreatLevel> threat = rzdStruct.setDistanceCalc().getThreat(e -> true);

        for (PlayerAI p : _player) {
            if (p == _me) {
                continue;
            }
            ///-------------- Classer les defenseurs en free / stuck / retreat
            HashSet<Drone> freeDrone = new HashSet<>();
            HashSet<Drone> stuckDrone = new HashSet<>();
            HashSet<Drone> retreatDrone = new HashSet<>();

            List<Drone> currAttaque = new ArrayList<>(D);

            for (Zone z : _zone) {

                currAttaque.clear();
                if (!(z.owner == p || z.owner == _nullPlayer)) {
                    continue;
                }

                int[] maxThreat = threat.get(z).getMaxThreat(_me);

                //System.err.println("" + z + "\n" + threat.get(z));
                //System.err.println("maxthreat[] " + arrayToString(maxThreat));
                List<RZoneDrone> defDrones = rzdStruct.stream().filter(e -> e.d.owner == _me && e.z == z && attDrones.contains(e.d) && !inuseDrones.contains(e.d))
                        .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList());
                int nbDrone = attDrones.stream().filter((e) -> !inuseDrones.contains(e)).collect(Collectors.toList()).size();
                int nbDroneLeft = nbDrone;
                for (RZoneDrone rzd : defDrones) {
                    int nbUsed = nbDrone - nbDroneLeft;
                    Drone d = rzd.d;
                    int level = rzd.level;
                    if (level >= maxThreat.length - 1) {
                        break;
                    }
                    int currLevelCp = maxThreat[level];
                    currAttaque.add(d);

                    //System.err.println("Considering " + rzd.d + " at " + z + " level " + rzd.level + " knowing " + currLevelCp + "/ Left " + nbDroneLeft + " used " + nbUsed);
                    if (currLevelCp < currAttaque.size() && currLevelCp <= DronePerPlanet) {
                        //System.err.println("Defense is breaking : attack drones "+currAttaque);

                        //System.err.println("Attack "+z+" with "+currAttaque+" at "+z.cor);
                        for (Drone atd : currAttaque) {
                            _order.get(atd).set(z.cor);
                            inuseDrones.add(atd);

                        }

                        break;
                    }
                }
            }// fin zone            

        }

    }

    @Override
    public List<L0_2dLib.Point> outorders() {

        if (!init) {
            attDrones.addAll(_drone.get(_me));
            init = true;

            for (Zone z : _zone) {
                ownedPrev.put(z, false);
            }
        }
        rzdStruct.setDistanceCalc();

        HashSet<Zone> lockedWorld = new HashSet<>();
        HashSet<Drone> lockedDrones = new HashSet<>();
        //System.err.println("generating orders "+_turnNumber);
        conquestTest();
        detectLocked(lockedWorld, lockedDrones);

        defendMonoworld(lockedDrones);
        HashSet<Drone> inuseDrones = new HashSet<>();

        attackOpportunist(inuseDrones, lockedDrones);        
        greedyThem(inuseDrones, lockedWorld);
        placementAttack(inuseDrones);

        //debug_attackplan();
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

}
