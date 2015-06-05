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

        private void planA() {

            if (true && tsz(geom.a,3)) {
                put(geom.a,0,geom.c);
                put(geom.a,1,geom.b);
                put(geom.a,2,geom.b);
            }
                                    
                     

        }

        public void planIt() {
            planA();
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

    private void doSimpleOrder() {

        plan.planIt();

        for (Drone d : _drone.get(_me)) {
            _order.get(d).set(plan.got.get(d));
        }

    }

    @Override
    public List<L0_2dLib.Point> outorders() {
        plan.countDronesAt();

        HashSet<Drone> inuseDrones = new HashSet<>();
        HashSet<RZoneZone> marked = new HashSet<>();

        doSimpleOrder();

        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ);
        geom = calcTransition();
        outputText("" + geom);

        plan.init();
    }

    @Override
    public void inputTurnPlayerBot(int p, List<L0_2dLib.Point> xyZ) {
        super.inputTurnPlayerBot(p, xyZ); //To change body of generated methods, choose Tools | Templates.
    }

}
