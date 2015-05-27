package executableTests;


import L1_bots.L1_botStruct;
import L1_bots.TestL1_SwarmBot;
import java.util.List;
import L0_tools.L0_2dLib;
import world.WorldBase;
import static world.WorldVisu.create;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jahan
 */
public class TestScenarios {
    
    
    public static void testVisu(){
        WorldBase w=new WorldBase(3, 5, 9937777, new WorldBase.BotSwarm(),new WorldBase.BotLost(),new WorldBase.TranquilleBot());
        w.genWorld();
        
        int nbturn=40;
        int pas=0;
        
        Thread genIt=new Thread(){

            @Override
            public void run() {
                for(int i=0;i<nbturn;i++){
                    w.genTurn();
                }  
            }
            
            
        };        
        genIt.start();
        
        create(w);        
    }
    
    public static void testL1Bot(){
        L1_botStruct.BotBridge br=new L1_botStruct.BotBridgeImpl(TestL1_SwarmBot.fact);
        
        final WorldBase.WorldBot brii=new WorldBase.WorldBot(){

            @Override
            public void setup(int P, int Id, int D, int Z, List<L0_2dLib.Point> xyZ) {
                br.setup(P, Id, D, Z, xyZ);
            }

            @Override
            public void turn(int[] zline, List<List<L0_2dLib.Point>> droneLinesPerPlayer) {
                br.turn(zline, droneLinesPerPlayer);
            }

            @Override
            public List<L0_2dLib.Point> outorders() {
                //System.err.println("Wrapper outorders exec");
                
                return br.outorders();
            }

            @Override
            public String botName() {
                return "TestL1_SwarmBot";
            }
        };
        
        WorldBase w=new WorldBase(3, 5, 9937777,brii,new WorldBase.TranquilleBot(), new WorldBase.BotSwarm());
        w.genWorld();
        
        int nbturn=3;
        int pas=0;
        
        Thread genIt=new Thread(){

            @Override
            public void run() {
                for(int i=0;i<nbturn;i++){
                    w.genTurn();
                }  
            }
            
            
        };        
        genIt.start();
        
        //create(w);                
        
    }
    
    
    public static void main (String[] args){
        testL1Bot();

    }
}
