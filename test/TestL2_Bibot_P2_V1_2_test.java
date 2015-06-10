/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import L1_bots.L1_botStruct;
import L1_bots.L1_botStruct.BotBase.Zone;
import L1_bots.TestL2_Bibot_P2_V1_2;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jahan
 */
public class TestL2_Bibot_P2_V1_2_test {
    
    public TestL2_Bibot_P2_V1_2_test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void testIt() {
         TestL2_Bibot_P2_V1_2 bot=new TestL2_Bibot_P2_V1_2(2, 0, 13, 1);
         L1_botStruct.BotBase.PlayerAI pa=bot.new PlayerAI(0);
         L1_botStruct.BotBase.PlayerAI pb=bot.new PlayerAI(1);
         L1_botStruct.BotBase.PlayerAI pnull=bot.new PlayerAI(-1);
         Zone target=bot.new Zone(0, pnull);
         List<TestL2_Bibot_P2_V1_2.HypCalc.DroneLevel> p1=new ArrayList<>();
         List<TestL2_Bibot_P2_V1_2.HypCalc.DroneLevel> p2=new ArrayList<>();
         
         List<TestL2_Bibot_P2_V1_2.HypCalc.Binding> bind=TestL2_Bibot_P2_V1_2.HypCalc.calcBinding(target, p1, p2);
     
     }
}
