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
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class TestL2_Bibot_P2_V1_1 extends L1_botStruct.BotBase {

    public TestL2_Bibot_P2_V1_1(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }

    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL2_Bibot_P2_V1_1(P1, Id1, D1, Z1);

    public void outputText(String t) {
        if (false) {
            return;
        }

        System.err.println("" + this.getClass().getSimpleName() + " " + t);
    }

    public class Planning {

        HashMap<Drone, L0_2dLib.WithCoord> got = new HashMap<>(D);

        HashMap<L0_2dLib.WithCoord, List<Drone>> at = new HashMap<>(8);
        HashMap<L0_2dLib.WithCoord, List<Drone>> atenemy = new HashMap<>(8);

        public void init() {
            plan.at.putIfAbsent(geom.a, new ArrayList<>(D));
            plan.at.putIfAbsent(geom.b, new ArrayList<>(D));
            plan.at.putIfAbsent(geom.c, new ArrayList<>(D));
            plan.at.putIfAbsent(geom.d, new ArrayList<>(D));
            plan.at.putIfAbsent(geom.ab, new ArrayList<>(D));
            plan.at.putIfAbsent(geom.ac, new ArrayList<>(D));
            plan.at.putIfAbsent(geom.bc, new ArrayList<>(D));
            plan.at.putIfAbsent(geom.ad, new ArrayList<>(D));

            plan.atenemy.putIfAbsent(geom.a, new ArrayList<>(D));
            plan.atenemy.putIfAbsent(geom.b, new ArrayList<>(D));
            plan.atenemy.putIfAbsent(geom.c, new ArrayList<>(D));
            plan.atenemy.putIfAbsent(geom.d, new ArrayList<>(D));
            plan.atenemy.putIfAbsent(geom.ab, new ArrayList<>(D));
            plan.atenemy.putIfAbsent(geom.ac, new ArrayList<>(D));
            plan.atenemy.putIfAbsent(geom.bc, new ArrayList<>(D));
            plan.atenemy.putIfAbsent(geom.ad, new ArrayList<>(D));
        }
        
        boolean tsz(L0_2dLib.WithCoord cc,int val){
            return plan.at.get(cc).size() == val;
        }
        
        void put(L0_2dLib.WithCoord curr,int ind, L0_2dLib.WithCoord dst){
            plan.got.put(plan.at.get(curr).get(ind), dst);         
        }

        private void planA(HashSet<Drone> inuse) {
            
            if(geom.c.owner==_nullPlayer){
                for(RZoneDrone rzd : _buildRZoneDrone().stream().filter(e -> e.d.owner == _me && !inuse.contains(e.d) && e.z==geom.c)
                                        .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList())){
                    inuse.add(rzd.d);
                    plan.got.put(rzd.d,geom.c);
                    break;
                }
            }
            
            if(geom.d.owner==_nullPlayer){
                for(RZoneDrone rzd : _buildRZoneDrone().stream().filter(e -> e.d.owner == _me && !inuse.contains(e.d) && e.z==geom.d)
                                        .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList())){
                    inuse.add(rzd.d);
                    plan.got.put(rzd.d,geom.d);
                    break;
                }
            }     
            
            if(geom.b.owner==_nullPlayer){
                for(RZoneDrone rzd : _buildRZoneDrone().stream().filter(e -> e.d.owner == _me && !inuse.contains(e.d) && e.z==geom.b)
                                        .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList())){
                    inuse.add(rzd.d);
                    plan.got.put(rzd.d,geom.b);
                    break;
                }
            }               
                                    
                     

        }

        public void planIt(HashSet<Drone> inuse) {
            planA(inuse);
        }

        public void countDronesAt() {
            PlayerAI enemyp = _player.get(_me.id ^ 1);
            for (L0_2dLib.WithCoord cc : geom.all) {
                at.get(cc).clear();
            }

            //-------------- Friendly drone count
            for (Drone d : _drone.get(_me)) {
                if (!plan.got.containsKey(d)) {
                    plan.got.put(d, geom.a);
                }

                L0_2dLib.WithCoord dst = plan.got.get(d);
                if (d.dist(dst) <= 100) {
                    at.get(dst).add(d);
                }
            }
            //-------------- E drone count
            for (Drone d : _drone.get(enemyp)) {
                for (L0_2dLib.WithCoord cc : geom.all) {
                    L0_2dLib.WithCoord dst = cc;
                    if (d.dist(dst) <= 100) {
                        atenemy.get(dst).add(d);
                    }

                }

            }
            //----- End drone count        

        }
    }

    public class Geometry {

        final Zone a;
        final Zone b;
        final Zone c;
        final Zone d;

        final RZoneZone ab;
        final RZoneZone ac;
        final RZoneZone bc;
        final RZoneZone ad;

        final List<L0_2dLib.WithCoord> all = new ArrayList<>(8);

        public Geometry(Zone a, Zone b, Zone c, Zone d, RZoneZone ab, RZoneZone ac, RZoneZone bc, RZoneZone ad) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.ab = ab;
            this.ac = ac;
            this.bc = bc;
            this.ad = ad;

            all.add(a);
            all.add(b);
            all.add(c);
            all.add(d);

            all.add(ab);
            all.add(ac);
            all.add(ad);
            all.add(bc);
        }

        @Override
        public String toString() {
            return "Geometry{" + "a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + "\n, ab=" + ab + "\n, ac=" + ac + "\n, bc=" + bc + "\n, ad=" + ad + '}';
        }

    }
    Geometry geom = null;

    private Zone otherZ(Zone it, RZoneZone rz) {

        if (rz.a == it) {
            return rz.b;
        }
        return rz.a;
    }

    private Geometry calcTransition() {
        HashMap<Zone, List<RZoneZone>> stt = new HashMap<>(4);
        List<RZoneZone> principaux = new ArrayList<>(3);

        List<RZoneZone> lrzz = _buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());
        int maxArc = 2;
        for (RZoneZone rz : lrzz) {
            stt.putIfAbsent(rz.a, new ArrayList<>(8));
            stt.putIfAbsent(rz.b, new ArrayList<>(8));

            stt.get(rz.a).add(rz);
            stt.get(rz.b).add(rz);
            maxArc--;

            principaux.add(rz);
            if (maxArc == 0) {
                break;
            }
        }
        maxArc = 1;
        for (RZoneZone rz : lrzz) {
            if (!stt.containsKey(rz.a) || !stt.containsKey(rz.b)) {
                continue;
            }
            if (principaux.contains(rz)) {
                continue;
            }
            principaux.add(rz);
            stt.get(rz.a).add(rz);
            stt.get(rz.b).add(rz);
            maxArc--;
            break;
        }

        for (RZoneZone rz : lrzz) {
            if (stt.containsKey(rz.a) && stt.containsKey(rz.b)) {
                continue;
            }
            if (principaux.contains(rz)) {
                continue;
            }
            stt.putIfAbsent(rz.a, new ArrayList<>(8));
            stt.putIfAbsent(rz.b, new ArrayList<>(8));

            stt.get(rz.a).add(rz);
            stt.get(rz.b).add(rz);
            maxArc--;
            break;
        }

        Zone a = null;
        Zone d = null;

        for (Zone z : _zone) {
            if (stt.get(z).size() == 3) {
                a = z;
            }
            if (stt.get(z).size() == 1) {
                d = z;
            }
        }
        
        Zone b=null;
        for (RZoneZone rz : lrzz) {
            if ((rz.a!=d) & rz.b!=d) {
                continue;
            }
            if ((rz.a==a) || rz.b==a) {
                continue;
            }            
            if (principaux.contains(rz)) {
                continue;
            }
            
            // on a trouve d <--> b
            b=otherZ(d, rz);
            break;
        }        
        

        Zone c1 = otherZ(a, stt.get(a).get(0));
        Zone c2 = otherZ(a, stt.get(a).get(1));
        
        Zone c;
        
        if(c1==b) c=c2; else c=c1;

        System.err.println("From b : " + stt.get(b));
        RZoneZone bc = null;
        for (RZoneZone rz : stt.get(b)) {
            if (rz.a == a || rz.b == a) {
                continue;
            }
            bc = rz;
        }

        Geometry geom = new Geometry(a, b, c, d, stt.get(a).get(0), stt.get(a).get(1), bc, stt.get(a).get(2));

        return geom;

    }

    Planning plan = new Planning();

    private void doSimpleOrder(HashSet<Drone> inuse) {

        plan.planIt(inuse);

        for (Drone d : _drone.get(_me)) {
            _order.get(d).set(plan.got.get(d));
        }

    }
    
    public void forceAnalysis(){
        HashMap<Zone, ThreatLevel> threat = _buildRZoneDrone().setDistanceCalc().getThreat(e -> true);                
    }
    
    public class ZoneDef {

        List<Drone> defDrone = new ArrayList<>(D);

        @Override
        public String toString() {
            return "ZoneDef{" + "" + defDrone + '}';
        }

    }    
    
    HashMap<Drone, Zone> droneDefInfo = new HashMap<>(D);

    HashMap<Zone, Boolean> ownedPrev = new HashMap<>(Z);    
    int DronePerPlanet = D / 3;
    List<Drone> attDrones = new ArrayList<>(D);
    
    RZoneDroneSet rzdStruct = null;    
    HashMap<Zone, Integer> immediateProx    =null;  // Proximity immediate
    
    /**
     * Attribue a chaque world des drones
     */
    private void calc_immediateProx() {
        if(immediateProx!=null) throw new RuntimeException("Called only once invariant broken");
        
         immediateProx= new HashMap<>(Z);   // Puit immediat
        
        for (RZoneZone rzz : _buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList())) {
            if(!immediateProx.containsKey(rzz.a)){
                immediateProx.put(rzz.a,rzz.level/2 +1);
            }
            if(!immediateProx.containsKey(rzz.b)){
                immediateProx.put(rzz.b,rzz.level/2+1);
            }            
        }        
    }    
    
    List<Zone> firstZone=new ArrayList<>();
    public boolean openingStratDone(){
        if(firstZone.size()==0 ){
           firstZone.add( rzdStruct.stream().sorted(comp_rzd_byLevel.reversed()).findFirst().get().z);             
        }
        
        HashMap<Zone, ThreatLevel> threat = rzdStruct.setDistanceCalc().getThreat(e -> true);        
        
        if(! (firstZone.get(0).owner==_nullPlayer)) return true;       

        HashSet<Drone> out_freeDrone=new HashSet<>(D);
        HashSet<Drone> out_stuckDrone=new HashSet<>(D);
        HashSet<Drone> out_retreatDrone=new HashSet<>(D);
        
        int upTo=defendMonoworldUpTo(_drone.get(_me), threat, firstZone.get(0),_me,0, out_freeDrone, out_stuckDrone, out_retreatDrone);
        
        for(Drone d :_drone.get(_me)){
            if(out_retreatDrone.contains(d) || out_stuckDrone.contains(d) ){
                _order.get(d).set(firstZone.get(0));
            
            }
        }
        
        return false;
    }
    
    public int defendMonoworldUpTo(List<Drone> dronesToUseForDef,HashMap<Zone, ThreatLevel> threat,Zone zoneToDefend, PlayerAI defender, int attackBonus
            ,HashSet<Drone> out_freeDrone,HashSet<Drone> out_stuckDrone,HashSet<Drone> out_retreatDrone) {
        ///-------------- Classer les defenseurs en free / stuck / retreat
        List<Drone> freeDefender = new ArrayList<>();
        
            int holdResult=-1;

            //System.err.println("Defenders "+z+" : "+zoneDefInfo.get(z).defDrone);

            int[] maxThreat = threat.get(zoneToDefend).getMaxThreat(defender);

            //System.err.println("" + z + "\n" + threat.get(z));
            //System.err.println("maxthreat[] " + arrayToString(maxThreat));
            List<RZoneDrone> defDrones = rzdStruct.stream().filter(e -> e.d.owner == defender && e.z == zoneToDefend && dronesToUseForDef.contains(e.d))
                    .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList());
            int nbDrone = dronesToUseForDef.size();
            int nbDroneLeft = nbDrone;
            
            int lastholding=-1;
            for (RZoneDrone rzd : defDrones) {
                int nbUsed = nbDrone - nbDroneLeft;
                Drone d = rzd.d;
                int level = rzd.level;
                if (level >= maxThreat.length - 3) {
                    break;
                }
                int currLevelCp = maxThreat[level]+attackBonus;
                int nextLevelCp = maxThreat[level + 1]+attackBonus;
                int futurLevelCp = maxThreat[level + 2]+attackBonus;

                //System.err.println("Considering " + rzd.d + " at " + z + " level " + rzd.level + " knowing " + currLevelCp + "/ Left " + nbDroneLeft + " used " + nbUsed);
                if (currLevelCp > nbDrone) {
                    if(holdResult==-1){
                        holdResult=lastholding;
                    }
                    out_freeDrone.add(d);
                }

                if (currLevelCp <= nbUsed && nextLevelCp <= nbUsed && futurLevelCp <= nbUsed) {
                    // System.err.println("" + d + " is considred free");
                    out_freeDrone.add(d);
                    nbDroneLeft--;
                } else if (currLevelCp <= nbUsed && nextLevelCp <= nbUsed) {
                    //  System.err.println("" + d + " is considred stuck");
                    out_stuckDrone.add(d);
                    nbDroneLeft--;
                } else {
                    //  System.err.println("" + d + " is considred retreat");
                    out_retreatDrone.add(d);
                    nbDroneLeft--;
                }
                
                lastholding=level;
            }
            
            return holdResult;
    }    


    @Override
    public List<L0_2dLib.Point> outorders() {
        rzdStruct.setDistanceCalc();        
        plan.countDronesAt();

        HashSet<Drone> inuseDrones = new HashSet<>();
        HashSet<RZoneZone> marked = new HashSet<>();

        if(openingStratDone()){
                    doSimpleOrder(inuseDrones);
        }


        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ);
        geom = calcTransition();
        outputText("" + geom);

        plan.init();
        rzdStruct = _buildRZoneDrone();
        
        calc_immediateProx();
    }

    @Override
    public void inputTurnPlayerBot(int p, List<L0_2dLib.Point> xyZ) {
        super.inputTurnPlayerBot(p, xyZ); //To change body of generated methods, choose Tools | Templates.
        
    }

}
