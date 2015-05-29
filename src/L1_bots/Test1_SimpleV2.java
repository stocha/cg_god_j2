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
public class Test1_SimpleV2  extends L1_botStruct.BotBase {

    public Test1_SimpleV2(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }
    
    public static L1_botStruct.BotFactory fact=(int P1, int Id1, int D1, int Z1) -> new Test1_SimpleV2(P1, Id1, D1, Z1);
    
    
    int DronePerPlanet=D/3;
    List<Drone> attDrones=new ArrayList<>(D);
    
    public class ZoneDef{
        List<Drone> defDrone=new ArrayList<>(D);

        @Override
        public String toString() {
            return "ZoneDef{" + "" + defDrone + '}';
        }
        
    }
    
    HashMap<Zone,ZoneDef> zoneDefInfo=new HashMap<>(Z);
    
    HashMap<Zone,Boolean> ownedPrev=new HashMap<>(Z);
    
    RZoneDroneSet rzdStruct=null;
    
    boolean init=false;

    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ); //To change body of generated methods, choose Tools | Templates.
        
        for(Zone z : _zone){
            zoneDefInfo.put(z, new ZoneDef());
        }
        
        rzdStruct=_buildRZoneDrone();
    }
    
    public void conquestTest(){         
        //System.err.println("owned prev "+ownedPrev);
        for(Zone z : _zone){

            if(ownedPrev.get(z) != (z.owner==_me)){
                if(z.owner==_me){
                    //conquest
                   // System.err.println("Conquest "+z);
                    
                    int defd=DronePerPlanet;
                    for(RZoneDrone rzd : rzdStruct.stream()
                            .filter(e->e.d.owner==_me && attDrones.contains(e.d) && e.z==z)
                            .sorted(comp_rzd_byLevel.reversed()).collect(Collectors.toList())){
                        
                        zoneDefInfo.get(z).defDrone.add(rzd.d);
                        defd--;
                        if(defd==0) break;
                    }
                    attDrones.removeAll(zoneDefInfo.get(z).defDrone);
                
                }else{
                    // lost
                    //System.err.println("Lost "+z);
                    attDrones.addAll(zoneDefInfo.get(z).defDrone);
                    zoneDefInfo.get(z).defDrone.clear();
                    
                }
                
                
            }
            ownedPrev.put(z,z.owner==_me);
        }
        
    }
    
    public void debug_attackplan(){
        for(Zone z : _zone){
            if(z.owner==_me){
                System.err.println(""+z+" has "+zoneDefInfo.get(z));            
            }        
        }
        System.err.println("Attack group has "+attDrones);
        
    }
    
    public void defend(){
        
    }
    
    public void attack(){
        for(PlayerAI p: _player){
            //System.err.println(""+p+" "+p.owned+" _me is "+_me);
            if(p==_me) continue;
            
            Zone targ=null;
            if(p.owned.size()>0){
                for(Drone d :attDrones){
                    _order.put(d, p.owned.get(0).cor);
                    //System.err.println("Attacking "+p.owned.get(0));
                
                }
                break;
                
            }
        
        }
        
    }
    
    

    @Override
    public List<L0_2dLib.Point> outorders() {
        
        if(!init){
            attDrones.addAll(_drone.get(_me));
            init=true;
            
            for(Zone z : _zone){
                ownedPrev.put(z, false);
            }
        }
        rzdStruct.setDistanceCalc();                
        //System.err.println("generating orders "+_turnNumber);
        conquestTest();
        defend();
        attack();
        
        //debug_attackplan();

        
        
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }
    
}