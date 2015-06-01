/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import L1_bots.L1_botStruct;
import L1_bots.TestL1_GreedyBot;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import world.WorldBase;

/**
 *
 * @author denis
 */
public class WorldTest {
    
    public WorldTest() {
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
    
    @Test
    public void testIt(){
        System.err.println("testing");
        WorldBase w=new WorldBase(3, 5, 9937777, new WorldBase.BotSwarm(),new WorldBase.BotLost(),new WorldBase.TranquilleBot());
        w.genWorld();
        
        int nbturn=40;
        int pas=0;
        for(int i=0;i<nbturn;i++){
            System.err.println(""+w.debug_turnAt(i, 0.01));
            w.genTurn();
        }
    }
    
    /**
     * Reader from input stream / output
     * @param args 
     */
    public static void main(String[] args) {
        L1_botStruct.BotBridgeGodIn theBot=new L1_botStruct.BotBridgeGodIn(System.in, TestL1_GreedyBot.fact);
        
        
        double maxT=0;

        while (true) {
            long t0 = System.currentTimeMillis();
            theBot.readTurn();
            theBot.writeOrders(System.out);

            long t1 = System.currentTimeMillis();
            double t = t1 - t0;
            if(maxT <t) maxT=t;
            System.err.println("------------------------------");
            System.err.println("temps mili " + t+" maxT "+maxT);
        }

    }        
    
}
