/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package L1_bots;

/**
 *
 * @author Jahan
 */
public class TestL2_BiBot_V1_1_N_1_1  {


    public static L1_botStruct.BotFactory fact = new L1_botStruct.BotFactory() {

           @Override
           public L1_botStruct.BotBase alloc(int P, int Id, int D, int Z) {
                    if(P==2){
                                return new Test1L_Solver4_3_2(P, Id, D, Z);
                    }else{
                                return new TestL1_OffenseV2_4(P, Id, D, Z);
                    }
           }

    };
}
