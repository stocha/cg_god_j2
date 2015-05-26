/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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
        WorldBase w=new WorldBase(3, 5, 9937777, new WorldBase.BotSwarm(),new WorldBase.BotLost());
        w.genWorld();
        
        int nbturn=40;
        int pas=0;
        for(int i=0;i<nbturn;i++){
            System.err.println(""+w.debug_turnAt(i, 0.01));
            w.genTurn();
        }
    }
}
