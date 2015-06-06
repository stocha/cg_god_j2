/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package L1_bots;

import L0_tools.L0_2dLib;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class TestL1_OffenseV2_6 extends L1_botStruct.BotBase {
    
    public TestL1_OffenseV2_6(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }
    
    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL1_OffenseV2_6(P1, Id1, D1, Z1);
    HashMap<Zone, Integer> immediateProx    =null;  // Proximity immediate

    public void outputText(String t) {
        if (false) {
            return;
        }

        System.err.println("" + this.getClass().getSimpleName() + " " + t);
    }    
    
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
    
    private void setZoneThreats(){
        HashMap<Zone,ThreatLevel> threat =_buildRZoneDrone().setDistanceCalc().getThreat(e->e.level<=immediateProx.get(e.z)||e.headingPercent>=95);
        for(Zone z : _zone){
            outputText(""+z+"\n"+threat.get(z));
        }
    }
    
    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ); //To change body of generated methods, choose Tools | Templates.

        calc_immediateProx();
    }    
    
    @Override
    public List<L0_2dLib.Point> outorders() {
        outputText(""+immediateProx);
        
        setZoneThreats();
        
        //debug_attackplan();
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
