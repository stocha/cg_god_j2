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
public class TestL2_Bi_P2_A_Explode3  extends L1_botStruct.BotBase {

    public TestL2_Bi_P2_A_Explode3(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }

    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL2_Bi_P2_A_Explode3(P1, Id1, D1, Z1);


    public void outputText(String t) {
        if(false) return;
        
        System.err.println("" + this.getClass().getSimpleName() + " " + t);
    }
    
    public class Planning{
        HashMap<Drone,L0_2dLib.WithCoord> got=new HashMap<>(D);
        
        
    }
    

    
    public class Geometry{
        final Zone a;
        final Zone b;
        final Zone c;
        final Zone d;
        
        final RZoneZone ab;
        final RZoneZone ac;
        final RZoneZone bc;
        final RZoneZone ad;

        public Geometry(Zone a, Zone b, Zone c, Zone d, RZoneZone ab, RZoneZone ac, RZoneZone bc, RZoneZone ad) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.ab = ab;
            this.ac = ac;
            this.bc = bc;
            this.ad = ad;
        }

        @Override
        public String toString() {
            return "Geometry{" + "a=" + a + ", b=" + b + ", c=" + c + ", d=" + d + "\n, ab=" + ab + "\n, ac=" + ac + "\n, bc=" + bc + "\n, ad=" + ad + '}';
        }
        
        
        
    }
    Geometry geom=null;
    

    
    private Zone otherZ(Zone it,RZoneZone rz){
    
        if(rz.a==it) return rz.b;
        return rz.a;
    }
    
    private Geometry calcTransition(){
        HashMap<Zone,List<RZoneZone>> stt=new HashMap<>(4);     
        List<RZoneZone> principaux=new ArrayList<>(3);        

        List<RZoneZone> lrzz=_buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());
        int maxArc=2;
        for(RZoneZone rz : lrzz){
            stt.putIfAbsent(rz.a, new ArrayList<>(8));
            stt.putIfAbsent(rz.b, new ArrayList<>(8));
            
            stt.get(rz.a).add(rz);
            stt.get(rz.b).add(rz);
            maxArc--;
            
            principaux.add(rz);
            if(maxArc==0) break;
        }
        maxArc=1;
        for(RZoneZone rz : lrzz){
            if(!stt.containsKey(rz.a)||!stt.containsKey(rz.b)) continue;
            if(principaux.contains(rz)) continue;
            principaux.add(rz);
            stt.get(rz.a).add(rz);
            stt.get(rz.b).add(rz);
            maxArc--;
             break;
        }       
        
        for(RZoneZone rz : lrzz){
            if(stt.containsKey(rz.a)&&stt.containsKey(rz.b)) continue;
            if(principaux.contains(rz)) continue;
            stt.putIfAbsent(rz.a, new ArrayList<>(8));
            stt.putIfAbsent(rz.b, new ArrayList<>(8));            
            
            stt.get(rz.a).add(rz);
            stt.get(rz.b).add(rz);
            maxArc--;
             break;
        }         
        
        Zone a=null;
        Zone d=null;
        
        
        for(Zone z : _zone){
            if(stt.get(z).size()==3){
                a=z;
            }
            if(stt.get(z).size()==1){
                d=z;
            }            
        }
        
        Zone b=otherZ(a, stt.get(a).get(0)) ;
        Zone c=otherZ(a, stt.get(a).get(1)) ;
        
        System.err.println("From b : "+stt.get(b));
        RZoneZone bc=null;
        for(RZoneZone rz : stt.get(b)){
            if(rz.a==a || rz.b==a) continue;
            bc=rz;
        }         
        
        Geometry geom=new Geometry(a, b, c, d, stt.get(a).get(0), stt.get(a).get(1),bc, stt.get(a).get(2));
        

        return geom;
        
    }
    
    Planning plan=new Planning();
    
    private void doSimpleOrder(){
        
        HashMap<L0_2dLib.WithCoord,List<Drone>> at=new HashMap<>(8);
        
        at.putIfAbsent(geom.a,new ArrayList<>(D));
        at.putIfAbsent(geom.b,new ArrayList<>(D));
        at.putIfAbsent(geom.c,new ArrayList<>(D));
        at.putIfAbsent(geom.d,new ArrayList<>(D));
        
        for(Drone d : _drone.get(_me)){
            if(!plan.got.containsKey(d)){
                plan.got.put(d, geom.d);                
            }        
            L0_2dLib.WithCoord dst=plan.got.get(d);
            if(d.dist(dst)<=100){
                at.putIfAbsent(dst,new ArrayList<>(D));
                at.get(dst).add(d);
            }
        }
        
        if( at.get(geom.d).size()==3){
            for(Drone d : at.get(geom.d)){
                 plan.got.put(d, geom.a);
            }
        
        }
        
        if( at.get(geom.a).size()==3){
            List<Drone> them=at.get(geom.a);
            plan.got.put(them.get(0), geom.d);
            plan.got.put(them.get(1), geom.b);
            plan.got.put(them.get(2), geom.c);                    
        }
        
        if( at.get(geom.d).size()==1 && at.get(geom.b).size()==1 && at.get(geom.c).size()==1 ){
            for(Drone d : _drone.get(_me)){
                 plan.got.put(d, geom.a);
            }            
            
        }
        
        
        
        
        
        
        for(Drone d : _drone.get(_me)){
             _order.get(d).set(plan.got.get(d));
        }

    }

    @Override
    public List<L0_2dLib.Point> outorders() {
        
        

        HashSet<Drone> inuseDrones = new HashSet<>();
        HashSet<RZoneZone> marked=new HashSet<>();

        doSimpleOrder();
        
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ); 
        geom=calcTransition();       
        outputText(""+geom);
    }

}
