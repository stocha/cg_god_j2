/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package L1_bots;

import java.util.List;
import L0_tools.L0_2dLib;

/**
 *
 * @author Jahan
 */
public class TestL1_SwarmBot extends L1_botStruct.BotBase {

    public TestL1_SwarmBot(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }
    
    public static L1_botStruct.BotFactory fact=(int P1, int Id1, int D1, int Z1) -> new TestL1_SwarmBot(P1, Id1, D1, Z1);

    @Override
    public List<L0_2dLib.Point> outorders() {
        //System.err.println("generating orders");
        
        // find Zone where owner != me
        //for(Zone z : _
        Zone cible=null;
        
        for(Zone z : _zone){
            //System.err.println("Z->"+z);
            if(z.owner!=_me){
                //System.err.println("Found one not mine !");
                cible=z;
                break;
            }
        }
        
        if(cible!=null){
            for(Drone d : _drone.get(_me)){
                _order.get(d).set(cible);
            }
        }
        
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
