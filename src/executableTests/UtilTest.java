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
    
    static final byte[] dat=new byte[]{0x13,0x72};
    
    public static String fromByteArrayToString(byte[] data,int perline){
        String res="\"";
        int lc=0;
        for(byte b : data){
            res+=""+convHexByte(b&255);
            res+="";
            lc++;
            
            if(lc>=perline){
                lc=0;
                res+="\"+\n\"";
            }
        }
        res+="\"";
        return res;
    }
    
    public static byte[] fromStringToByteArray(String in){
        byte[] res=new byte[in.length()/2];
        for(int i=0;i<res.length;i++){
            char a=in.charAt(i*2);
            char b=in.charAt(i*2+1);
            
            res[i]=(byte)convHexByte(a, b);
        }
        
        return res;
    }
}
