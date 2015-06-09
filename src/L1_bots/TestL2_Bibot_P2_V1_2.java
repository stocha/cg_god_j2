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
public class TestL2_Bibot_P2_V1_2 extends L1_botStruct.BotBase {

    public TestL2_Bibot_P2_V1_2(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }
    
    public class HypCalc{
        public class DroneLevel{
            final Drone d;
            final int l;

            public DroneLevel(Drone d, int l) {
                this.d = d;
                this.l = l;
            }                        
        }
        
        public class Binding{
            Drone under;
            Drone top;
            int leveldif;
            boolean underCapture;
            boolean captureConflict;
        
        }
    
        /**
         * 
         * @param target
         * @param p1 non empty
         * @param p2 non empty (define player)
         * @return 
         */
        List<Binding> calcBinding(Zone target,List<DroneLevel> p1,List<DroneLevel> p2){
            List<Binding> res=new ArrayList<>(D);
            
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
    
    List<Zone> firstZone=new ArrayList<>();
    public boolean openingStratDone(){
        if(firstZone.size()==0 ){
           firstZone.add( _buildRZoneDrone().setDistanceCalc().stream().filter(e->e.d.owner==_me).sorted(comp_rzd_byLevel.reversed()).findFirst().get().z);             
        }
        
        for(Drone d : _drone.get(_me)){
            _order.get(d).set(firstZone.get(0));
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
