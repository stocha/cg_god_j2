/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import executableTests.UtilTest;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jahan
 */
public class Divers {
    
    public Divers() {
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
     public void testConvertHex() {
         assertEquals(UtilTest.convHexByte('0', '3'),3);              
         assertEquals(UtilTest.convHexByte('F', 'F'),255);
         assertEquals(UtilTest.convHexByte('F', 'E'),254);
         assertEquals(UtilTest.convHexByte('1', '1'),17);
         assertEquals(UtilTest.convHexByte('1', '0'),16);
         assertEquals(UtilTest.convHexByte('0', 'F'),15);
         assertEquals(UtilTest.convHexByte('0', 'A'),10);
         assertEquals(UtilTest.convHexByte('0', '9'),9);
    
         
         assertEquals(""+UtilTest.convHexByte(255),"FF");
         assertEquals(""+UtilTest.convHexByte(254),"FE");
         assertEquals(""+UtilTest.convHexByte(17),"11");
         assertEquals(""+UtilTest.convHexByte(16),"10");
         assertEquals(""+UtilTest.convHexByte(15),"0F");
         assertEquals(""+UtilTest.convHexByte(10),"0A");
         assertEquals(""+UtilTest.convHexByte(9),"09");
         assertEquals(""+UtilTest.convHexByte(3),"03");
     
     }
     
     @Test
     public void testConvertByteJava() {
         
//         System.out.println(""+UtilTest.fromByteArrayToString(new byte[]{(byte)0xFF,(byte)0x00,
//         (byte)0x7F,(byte)0x01
//         }, 2)
//         );
         assertEquals(
                 UtilTest.fromByteArrayToString(new byte[]{(byte)0xFF,(byte)0x00,
         (byte)0x7F,(byte)0x01
         }, 2),"\"FF00\"+\n\"7F01\"+\n\"\""
         );     
         
         byte[] it=new byte[]{(byte)0xFF,(byte)0x00,
         (byte)0x7F,(byte)0x01
         };
         
         assertEquals(Arrays.equals(it,UtilTest.fromStringToByteArray("FF007F01")),true);
         
         System.out.println(""+Arrays.toString(it));
     
     }     
     
     @Test
     public void testTruc() {
         byte[] toto=new byte[]{0x12,0x13,0x01};
     
     }     
}
