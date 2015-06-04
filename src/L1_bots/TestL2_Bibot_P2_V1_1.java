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
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class TestL2_Bibot_P2_V1_1  extends L1_botStruct.BotBase {

    public TestL2_Bibot_P2_V1_1(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }

    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL2_Bibot_P2_V1_1(P1, Id1, D1, Z1);


    public void outputText(String t) {
        if(false) return;
        
        System.err.println("" + this.getClass().getSimpleName() + " " + t);
    }
    
    public void calcSquare(){
        
        
        final RZoneZone first;
        RZoneZone second;
        RZoneZone third=null;
        
        List<RZoneZone> lrzz=_buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());
        List<Drone> allDrone=_drone.get(_me);
        first=lrzz.get(0);
        _order.get(allDrone.get(0)).set(first);
        
        outputText("Selected first segment "+first);
        for(RZoneZone it : lrzz)
        {

            //outputText(" "+it);
        }
        
        lrzz=_buildRZoneZone().setDistance().stream()
                .filter( e->((e.a!=first.a) && (e.a!=first.b) && (e.b!=first.a) && (e.b!=first.b)) )
                        .sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());        
        
        second=lrzz.get(0);
        _order.get(allDrone.get(1)).set(second);
        
    }

    @Override
    public List<L0_2dLib.Point> outorders() {


        HashSet<Drone> inuseDrones = new HashSet<>();

        calcSquare();
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

}
