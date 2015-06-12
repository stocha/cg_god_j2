/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package L1_bots;

import L0_tools.L0_2dLib;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class TestL2_Bibot_P2_V1_2 extends L1_botStruct.BotBase{

    public TestL2_Bibot_P2_V1_2(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }
    
    public static class HypCalc{
        public static class DroneLevel implements Comparable<DroneLevel>{
            final Drone d;
            final int l;

            public DroneLevel(Drone d, int l) {
                this.d = d;
                this.l = l;
            }                        

            @Override
            public int compareTo(DroneLevel t) {
                return -t.l+this.l;
            }

            @Override
            public String toString() {
                return "DroneLevel{" + "d=" + d + ", l=" + l + '}';
            }
            
            
        }
        
        public static class Binding{
            DroneLevel under;
            DroneLevel top;
            int leveldif;
            boolean underCapture;
            boolean captureConflict;

            @Override
            public String toString() {
                return "Binding{" + "under=" + under + ", top=" + top + ", leveldif=" + leveldif + ", underCapture=" + underCapture + ", captureConflict=" + captureConflict + '}';
            }
        
            
            
        }
        
        
    
        /**
         * 
         * @param target
         * @param p1 non empty
         * @param p2 non empty (define player)
         * @return 
         */
        public final static List<Binding> calcBinding(Zone target,List<DroneLevel> p1,List<DroneLevel> p2){
            List<Binding> res=new ArrayList<>(20);
            
            Collections.sort(p1);
            Collections.sort(p2);            
            
            PlayerAI own=target.owner;
            PlayerAI pp1=p1.get(0).d.owner;
            PlayerAI pp2=p2.get(0).d.owner;
            
            boolean end=false;
            
            Iterator<DroneLevel> a=p1.iterator();
            Iterator<DroneLevel> b=p2.iterator();
            
            DroneLevel ca=a.next();
            DroneLevel cb=b.next();
            
            while(!end){
                Binding bind=new Binding();                        
                
                int comp=ca.compareTo(cb);
                if(comp==0){
                    if(ca.d.owner!=own && cb.d.owner!=own){
                        bind.top=cb;
                        bind.under=ca;
                        bind.captureConflict=true;
                        bind.leveldif=comp;
                        bind.underCapture=false;
                    }else if(ca.d.owner==own){
                        bind.top=cb;
                        bind.under=ca;
                        bind.captureConflict=false;
                        bind.leveldif=comp;
                        bind.underCapture=false;                        
                    
                    }else{
                        bind.top=ca;
                        bind.under=cb;
                        bind.captureConflict=false;
                        bind.leveldif=comp;
                        bind.underCapture=false;                              
                    }
                    
                    
                }
                else if(comp<0){ // ca plus bas que cb
                    if(ca.d.owner!=own && cb.d.owner!=own){
                        bind.top=cb;
                        bind.under=ca;
                        bind.captureConflict=false;
                        bind.leveldif=-comp;
                        bind.underCapture=true;
                        own=ca.d.owner;
                    }else if(ca.d.owner==own){
                        bind.top=cb;
                        bind.under=ca;
                        bind.captureConflict=false;
                        bind.leveldif=-comp;
                        bind.underCapture=false;                        
                    
                    }else{
                        bind.top=cb;
                        bind.under=ca;
                        bind.captureConflict=false;
                        bind.leveldif=-comp;
                        bind.underCapture=true;     
                        own=ca.d.owner;
                    }                    
                    
                }else if(comp>0){
                    if(ca.d.owner!=own && cb.d.owner!=own){
                        bind.top=ca;
                        bind.under=cb;
                        bind.captureConflict=false;
                        bind.leveldif=comp;
                        bind.underCapture=true;
                        own=cb.d.owner;
                    }else if(cb.d.owner==own){
                        bind.top=ca;
                        bind.under=cb;
                        bind.captureConflict=false;
                        bind.leveldif=comp;
                        bind.underCapture=false;                        
                    
                    }else{
                        bind.top=ca;
                        bind.under=cb;
                        bind.captureConflict=false;
                        bind.leveldif=comp;
                        bind.underCapture=true;  
                        own=cb.d.owner;
                    }                       
                    
                }
                if(bind.top==null || bind.under ==null) throw new RuntimeException("invariation invalide");
                res.add(bind);
                bind=null;
                
                if(!a.hasNext()||!b.hasNext()) {
                    if(a.hasNext()==b.hasNext()) return res;
                    if(a.hasNext()){
                        ca=a.next();
                        
                        Binding bindfin=new Binding();
                        bindfin.top=null;
                        bindfin.under=ca;
                        bindfin.captureConflict=false;
                        bindfin.leveldif=ca.compareTo(cb);
                        
                        if(own!=ca.d.owner)
                            bindfin.underCapture=true;           
                        else
                            bindfin.underCapture=false;           
                        
                        res.add(bindfin);
                    }else{
                        cb=b.next();
                        
                        Binding bindfin=new Binding();
                        bindfin.top=null;
                        bindfin.under=cb;
                        bindfin.captureConflict=false;
                        bindfin.leveldif=cb.compareTo(ca);
                        
                        if(own!=cb.d.owner)
                            bindfin.underCapture=true;           
                        else
                            bindfin.underCapture=false;           
                        
                        res.add(bindfin);                        
                    }
                    //System.out.println("returning asymetric "+res);
                    return res;
                }
                ca=a.next();
                cb=b.next();
            }
            
            return res;
        }
    }

    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL2_Bibot_P2_V1_2(P1, Id1, D1, Z1);

    public void outputText(String t) {
        if (false) {
            return;
        }
        System.err.println("" + this.getClass().getSimpleName() + " " + t);
    }

    HashMap<Zone,Integer> immediateProx=null;
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
    
    public List<HypCalc.Binding> calcBinding(List<Drone> a, List<Drone> b, Zone it){
        List<HypCalc.DroneLevel> la=new ArrayList<>(a.size());
        List<HypCalc.DroneLevel> lb=new ArrayList<>(b.size());
        
        for(Drone d : a){
            la.add(new HypCalc.DroneLevel(d,(int) (d.dist(it)-1) /100));
        }
        for(Drone d : b){
            lb.add(new HypCalc.DroneLevel(d,(int) (d.dist(it)-1) /100));
        }        
        
        return HypCalc.calcBinding(it, la, lb);
        
    }
    
    public int levelCapture(List<HypCalc.Binding> bind){
        for(HypCalc.Binding b : bind){
            if(b.under.d.owner==_me && b.underCapture){
                return b.under.l;
            
            }
        
        }
        return -1;
    }
    
    public HypCalc.DroneLevel findMine(HypCalc.Binding b){
        if(b.under.d.owner==_me) return b.under;
        
        if(b.top==null) return null;
        return b.top;
    }
    
    List<Zone> firstZone=new ArrayList<>();
    public boolean openingStratDone(){
        if(firstZone.size()==0 ){
            List<RZoneDrone> lz=_buildRZoneDrone().setDistanceCalc().stream().filter(e->e.d.owner==_me).sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList());            
           firstZone.add(lz.get(0).z );             
           for(RZoneZone rzd : _buildRZoneZone().setDistance().stream().filter(e->e.a==firstZone.get(0) || e.b==firstZone.get(0)).sorted(comp_zz_bydist.reversed()).collect(Collectors.toList())){
               if(rzd.a==firstZone.get(0)){
                   firstZone.add(rzd.b);
                   break;
               }else{
                   firstZone.add(rzd.a);
                   break;                   
               }
               
           }
        }
        
        List<HypCalc.Binding> bind=calcBinding(_drone.get(_me), _drone.get(_player.get(_me.id^1)), firstZone.get(0));
        
        //HashSet<Drone> done
        if(firstZone.get(0).owner!=_me){
            int lvlcapt=levelCapture(bind);
            if(lvlcapt==-1){
                for(HypCalc.Binding b : bind){
                    Drone myd=findMine(b).d;
                    if(myd!=null){
                        _order.get(myd).set(firstZone.get(0));
                    }
                } 
            }else{
                boolean doneAtt=false;
                
                for(HypCalc.Binding b : bind){
                    HypCalc.DroneLevel myd=findMine(b);        
                    if(myd==null) break;
                    if(doneAtt){
                        if(myd!=null){
                            _order.get(myd.d).set(firstZone.get(1));
                        }                    
                        continue;
                    }
                    

                    if(myd.l>=lvlcapt-1 ||b.captureConflict){
                        _order.get(myd.d).set(firstZone.get(0));
                    }else{
                        _order.get(myd.d).set(firstZone.get(1));
                    }
                    if(b.underCapture) doneAtt=true;
                }             
            
            }
           
        }


        
        return false;
    }


    @Override
    public List<L0_2dLib.Point> outorders() {

        HashSet<Drone> inuseDrones = new HashSet<>();
        HashSet<RZoneZone> marked = new HashSet<>();

        if(openingStratDone()){                    
        }


        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ);
        
        calc_immediateProx();
    }

    @Override
    public void inputTurnPlayerBot(int p, List<L0_2dLib.Point> xyZ) {
        super.inputTurnPlayerBot(p, xyZ); //To change body of generated methods, choose Tools | Templates.
        
    }

}
