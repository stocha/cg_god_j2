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
        WorldBase w=new WorldBase(3, 5, 32, new WorldBase.BotDefault(),new WorldBase.BotDefault());
        w.genWorld();
        
        System.err.println(""+w.debug_turnAt(0, 0.01));
    }
}
