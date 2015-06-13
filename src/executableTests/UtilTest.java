/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package executableTests;

/**
 *
 * @author Jahan
 */
public class UtilTest {
    
    static private char convHalfByte(int v){
        int k=v&15;
        
        if(k<10) return (char)('0'+k);
        
        return (char)('A'+(k-10));
    }
    
    public static String convHexByte(int v){
        return ""+convHalfByte(v/16)+""+convHalfByte(v);
        
    }
    
    static private int convHalfByte(char v){
        int k=v-'0';
        
        if(k<10 && k>=0) return (k);
        
        return (v-'A')+10;
    }
    
    public static int convHexByte(char a,char b){
        return (convHalfByte(a)*16)+convHalfByte(b);
        
    }    
}
