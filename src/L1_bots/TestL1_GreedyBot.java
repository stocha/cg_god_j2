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
public class TestL1_GreedyBot extends L1_botStruct.BotBase {

    public TestL1_GreedyBot(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }
    
    public static L1_botStruct.BotFactory fact=(int P1, int Id1, int D1, int Z1) -> new TestL1_GreedyBot(P1, Id1, D1, Z1);

    @Override
    public List<L0_2dLib.Point> outorders() {
        
        
        System.err.println("generating orders "+_turnNumber);

        L1_botStruct.BotBase.Zone cible=null;
        
        
        HashMap<Drone,Boolean> droneDone=new HashMap(D);
        
        for(RZoneDrone rzd : _rzonedrone.stream().sorted(byDist.reversed()).collect(Collectors.toList())){
           // System.err.println(""+rzd);
            
            if(rzd.d.owner==_me && !droneDone.containsKey(rzd.d) && rzd.z.owner!=_me){
                _order.get(rzd.d).set(rzd.z);
             //   System.err.println(""+rzd.d+" is heading to "+rzd.z);
                droneDone.put(rzd.d, true);
            }        
        }
        
        
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
