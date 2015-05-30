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
public class TestL1_OffenseV2_2 extends L1_botStruct.BotBase {

    public TestL1_OffenseV2_2(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }

    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL1_OffenseV2_2(P1, Id1, D1, Z1);

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

    public void conquestTest() {
        //System.err.println("owned prev "+ownedPrev);
        for (Zone z : _zone) {

            if (ownedPrev.get(z) != (z.owner == _me)) {
                if (z.owner == _me) {
                    //conquest
                     System.err.println("Conquest "+z);

                    int defd = DronePerPlanet;
                    for (RZoneDrone rzd : rzdStruct.stream()
                            .filter(e -> e.d.owner == _me && attDrones.contains(e.d) && e.z == z)
                            .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList())) {

                        zoneDefInfo.get(z).defDrone.add(rzd.d);
                        defd--;
                        if (defd == 0) {
                            break;
                        }
                    }
                    attDrones.removeAll(zoneDefInfo.get(z).defDrone);

                } else {
                    // lost
                    System.err.println("Lost "+z);
                    attDrones.addAll(zoneDefInfo.get(z).defDrone);
                    zoneDefInfo.get(z).defDrone.clear();
                    System.err.println("Att drones "+attDrones);

                }

            }
            ownedPrev.put(z, z.owner == _me);
        }

    }

    public void debug_attackplan() {
        for (Zone z : _zone) {
            if (z.owner == _me) {
                System.err.println("" + z + " has " + zoneDefInfo.get(z));
            }
        }
        System.err.println("Attack group has " + attDrones);

    }

    public class ThreatLevel {

        public static final int maxLevel = 50;

        private int[][] t = new int[P][maxLevel + 1];
        int sp = 1;

        HashMap<PlayerAI, HashMap<Integer, List<Drone>>> droneAtLevel = new HashMap<>(P);

        {
            for (PlayerAI p : _player) {
                droneAtLevel.put(p, new HashMap<>(maxLevel + 1));
            }

        }

        public ThreatLevel reset() {
            sp = 1;
            for (int p = 0; p < P; p++) {
                t[p][0] = 0;
            }
            return this;
        }

        private void increastLevelTo(int level) {
            if (level >= maxLevel - 1) {
                return;
            }
            while (sp < level + 1) {
                for (int p = 0; p < P; p++) {
                    t[p][sp] = t[p][sp - 1];
                }
                sp++;
            }
        }

        public void addThreat(Drone d, int level) {
            if (level >= maxLevel - 1) {
                return;
            }
            //System.err.println("Add threat " + d + " level " + level);

            if (droneAtLevel.get(d.owner).get(level) == null) {
                droneAtLevel.get(d.owner).put(level, new ArrayList<>(D));
            }
            droneAtLevel.get(d.owner).get(level).add(d);

            increastLevelTo(level + 1);
            t[d.owner.id][level + 1]++;

        }

        public int getThreatAt(PlayerAI p, int level) {
            if (level >= maxLevel - 1) {
                return t[p.id][maxLevel - 1];
            }
            return t[p.id][level + 1];
        }

        public int getMaxThreatAt(int level, PlayerAI exclude) {
            if (level >= maxLevel - 1) {
                return getMaxThreatAt(maxLevel - 1, exclude);
            }

            int max = -1;
            for (int i = 0; i < P; i++) {
                if (i == exclude.id) {
                    continue;
                }
                if (t[i][level + 1] > max) {
                    max = t[i][level + 1];
                }
            }
            return max;
        }

        public int[] getMaxThreat(PlayerAI exclude) {
            int[] res = new int[sp - 1];
            for (int i = 0; i < sp - 1; i++) {
                res[i] = getMaxThreatAt(i, exclude);
            }

            return res;
        }

        @Override
        public String toString() {
            String res = "";

            for (int i = 0; i < P; i++) {
                for (int level = 0; level < sp; level++) {
                    res += "|" + t[i][level];

                }
                res += "\n";
            }
            for (PlayerAI p : _player) {
                res += "max for " + p + "\n";
                for (int level = 0; level < sp - 1; level++) {
                    res += "|" + (getMaxThreatAt(level, p)) + "";
                }
                res += "\n";
            }
            res += "" + droneAtLevel;

            return res;
        }

    }

    private String arrayToString(int[] it) {
        String r = "";
        for (int i = 0; i < it.length; i++) {
            r += "|" + it[i];
        }
        return r;
    }

    public void defendMonoworld() {

        List<RZoneDrone> rzb = rzdStruct.stream().sorted(comp_rzd_byLevel.reversed().thenComparing(new CompByPlayerRzd(_me.id).reversed())).collect(Collectors.toList());

        HashMap<Zone, ThreatLevel> threat = new HashMap<>(Z);
        for (Zone z : _zone) {
            threat.put(z, new ThreatLevel().reset());
        }

        int cpThreat = 0;

        for (RZoneDrone r : rzb) {
            threat.get(r.z).addThreat(r.d, r.level);
        }

        ///-------------- Classer les defenseurs en free / stuck / retreat
        HashSet<Drone> freeDrone = new HashSet<>();
        HashSet<Drone> stuckDrone = new HashSet<>();
        HashSet<Drone> retreatDrone = new HashSet<>();

        for (Zone z : _zone) {
            if (z.owner != _me) {
                continue;
            }

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
                if(level >= maxThreat.length-3) break;
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
                    _player.add(_nullPlayer);
                    for (PlayerAI p : _player) {
                        if (p == _me) {
                            continue;
                        }
                        if (p.owned.size() > 0) {
                            _order.get(d).set( p.owned.get(fixRa % p.owned.size()).cor);
                            //System.err.println("free Attacking " + p.owned.get(fixRa % p.owned.size()) + " fixed " + fixRa + "  size " + p.owned.size());
                        }
                    }
                    _player.remove(_nullPlayer);
                } else if (retreatDrone.contains(d)) {
                    _order.get(d).set( z.cor);
                } else {
                    _order.get(d).set( d.cor);
                }

            }

        }// fin zone

    }
    
    public void placementAttack(HashSet<Drone> inuseDrones){
        for(Drone d : attDrones){
            if(inuseDrones.contains(d)) continue;
            _order.get(d).set(new L0_2dLib.Point(2000,400));
        }
    }
    
    public void greedyThem(HashSet<Drone> inuseDrones){
       
        HashSet<Drone> droneDone=new HashSet(D);
        
        for(RZoneDrone rzd : _buildRZoneDrone().setDistanceCalc().stream().filter((e)->e.z.owner==_nullPlayer ).sorted(comp_rzd_byDist.reversed()).collect(Collectors.toList())){
           // System.err.println(""+rzd);
            
            if(rzd.d.owner==_me && !droneDone.contains(rzd.d)){//&& rzd.z.owner!=_me
                _order.get(rzd.d).set(rzd.z);
                System.err.println(""+rzd.d+" is heading to "+rzd.z);
                droneDone.add(rzd.d);
                inuseDrones.add(rzd.d);
            }        
        }
            
    
    }

    public void attackOpportunist(HashSet<Drone> inuseDrones) {

        for (PlayerAI p : _player) {
            if(p==_me) continue;
            
            List<RZoneDrone> rzb = rzdStruct.stream().sorted(comp_rzd_byLevel.reversed().thenComparing(new CompByPlayerRzd(p.id).reversed())).collect(Collectors.toList());

            HashMap<Zone, ThreatLevel> threat = new HashMap<>(Z);
            for (Zone z : _zone) {
                threat.put(z, new ThreatLevel().reset());
            }

            int cpThreat = 0;

            for (RZoneDrone r : rzb) {
                threat.get(r.z).addThreat(r.d, r.level);
            }

            ///-------------- Classer les defenseurs en free / stuck / retreat
            HashSet<Drone> freeDrone = new HashSet<>();
            HashSet<Drone> stuckDrone = new HashSet<>();
            HashSet<Drone> retreatDrone = new HashSet<>();
            
            List<Drone> currAttaque=new ArrayList<>(D);

            for (Zone z : _zone) {
                
                currAttaque.clear();
                if (!(z.owner == p || z.owner== _nullPlayer)) {
                    continue;
                }

                int[] maxThreat = threat.get(z).getMaxThreat(_me);

                //System.err.println("" + z + "\n" + threat.get(z));
                //System.err.println("maxthreat[] " + arrayToString(maxThreat));

                List<RZoneDrone> defDrones = rzdStruct.stream().filter(e -> e.d.owner == _me && e.z == z && attDrones.contains(e.d) && !inuseDrones.contains(e.d))
                        .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList());
                int nbDrone = attDrones.stream().filter((e)->!inuseDrones.contains(e)).collect(Collectors.toList()).size();
                int nbDroneLeft = nbDrone;
                for (RZoneDrone rzd : defDrones) {
                    int nbUsed = nbDrone - nbDroneLeft;
                    Drone d = rzd.d;
                    int level = rzd.level;
                    if(level >= maxThreat.length-1) break;
                    int currLevelCp = maxThreat[level];
                    currAttaque.add(d);

                    //System.err.println("Considering " + rzd.d + " at " + z + " level " + rzd.level + " knowing " + currLevelCp + "/ Left " + nbDroneLeft + " used " + nbUsed);
                    if (currLevelCp < currAttaque.size()) {
                        //System.err.println("Defense is breaking : attack drones "+currAttaque);
                        
                        //System.err.println("Attack "+z+" with "+currAttaque+" at "+z.cor);
                        for(Drone atd : currAttaque){
                            _order.get(atd).set(z.cor);
                            inuseDrones.add(atd);
                            
                        }
                        
                        break;
                    }
                }

//                for (Drone d : zoneDefInfo.get(z).defDrone) {
//                    if (freeDrone.contains(d)) {
//                        _player.add(_nullPlayer);
//                        for (PlayerAI p : _player) {
//                            if (p == _me) {
//                                continue;
//                            }
//                            if (p.owned.size() > 0) {
//                                _order.put(d, p.owned.get(fixRa % p.owned.size()).cor);
//                                System.err.println("free Attacking " + p.owned.get(fixRa % p.owned.size()) + " fixed " + fixRa + "  size " + p.owned.size());
//                            }
//                        }
//                        _player.remove(_nullPlayer);
//                    } else if (retreatDrone.contains(d)) {
//                        _order.put(d, z.cor);
//                    } else {
//                        _order.put(d, d.cor);
//                    }
//
//                }

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
        //System.err.println("generating orders "+_turnNumber);
        conquestTest();
        defendMonoworld();
        HashSet<Drone> inuseDrones = new HashSet<>();            
                  
        greedyThem(inuseDrones);        
        attackOpportunist(inuseDrones);        
        placementAttack(inuseDrones);

        //debug_attackplan();
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

}
