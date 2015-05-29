package executableTests;


import L1_bots.L1_botStruct;
import L1_bots.TestL1_SwarmBot;
import java.util.List;
import L0_tools.L0_2dLib;
import L1_bots.Test1_SimpleV2;
import L1_bots.TestL1_DefenseBot;
import L1_bots.TestL1_GreedyBot;
import L1_bots.TestL1_OffenseBot;
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
    
    final static int NBTURNS=200;
    
    public static class TesterBot implements WorldBase.WorldBot{
        
        final L1_botStruct.BotBridge br;
        
        TesterBot(L1_botStruct.BotFactory fact){
            br=new L1_botStruct.BotBridgeImpl(fact);        
        }

            @Override
            public void setup(int P, int Id, int D, int Z, List<L0_2dLib.Point> xyZ) {
                //System.err.println("Wrapper setup exec "+xyZ);
                br.setup(P, Id, D, Z, xyZ);
                
            }
            
            int turn=0;

            @Override
            public void turn(int[] zline, List<List<L0_2dLib.Point>> droneLinesPerPlayer) {
                System.err.println("Turn "+turn++);
                br.turn(zline, droneLinesPerPlayer);
            }

            @Override
            public List<L0_2dLib.Point> outorders() {
                //System.err.println("Wrapper outorders exec");
                List<L0_2dLib.Point> res=br.outorders();
                //System.err.println("Wrapper outorders exec "+res);
                return res;
            }

            @Override
            public String botName() {
                return ""+br.botName();
            }        
        
    }
    
   public static void testV2Simple(){
 
        //WorldBase w=new WorldBase(10, 4, 9937777,new TesterBot(TestL1_GreedyBot.fact),new TesterBot(TestL1_DefenseBot.fact));
        //WorldBase w=new WorldBase(5, 4, 9937777,new TesterBot(TestL1_OffenseBot.fact),new TesterBot(TestL1_DefenseBot.fact));
        //WorldBase w=new WorldBase(9, 4, 9937777,new TesterBot(TestL1_DefenseBot.fact),new TesterBot(TestL1_GreedyBot.fact));
        WorldBase w=new WorldBase(3, 6, 9937777,new TesterBot(Test1_SimpleV2.fact), new TesterBot(TestL1_GreedyBot.fact));
        
        w.genWorld();
        
        int nbturn=100;
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
    
    public static void testOffensiveL1Bot(){
 
        //WorldBase w=new WorldBase(10, 4, 9937777,new TesterBot(TestL1_GreedyBot.fact),new TesterBot(TestL1_DefenseBot.fact));
        //WorldBase w=new WorldBase(5, 4, 9937777,new TesterBot(TestL1_OffenseBot.fact),new TesterBot(TestL1_DefenseBot.fact));
        //WorldBase w=new WorldBase(9, 4, 9937777,new TesterBot(TestL1_DefenseBot.fact),new TesterBot(TestL1_GreedyBot.fact));
        WorldBase w=new WorldBase(1, 3, 9937777,new TesterBot(TestL1_OffenseBot.fact),new TesterBot(TestL1_DefenseBot.fact));
        
        w.genWorld();
        
        int nbturn=NBTURNS;
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
                //System.err.println("Wrapper setup exec "+xyZ);
                br.setup(P, Id, D, Z, xyZ);
            }

            @Override
            public void turn(int[] zline, List<List<L0_2dLib.Point>> droneLinesPerPlayer) {
                br.turn(zline, droneLinesPerPlayer);
            }

            @Override
            public List<L0_2dLib.Point> outorders() {
                //System.err.println("Wrapper outorders exec");
                List<L0_2dLib.Point> res=br.outorders();
                //System.err.println("Wrapper outorders exec "+res);
                return res;
            }

            @Override
            public String botName() {
                return "TestL1_SwarmBot";
            }
        };
        
        WorldBase w=new WorldBase(10, 10, 9937777,brii,new TesterBot(TestL1_GreedyBot.fact), new WorldBase.BotSwarm());
        w.genWorld();
        
        int nbturn=NBTURNS;
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
    
    public static void testDefenderL1Bot(){
        L1_botStruct.BotBridge br=new L1_botStruct.BotBridgeImpl(TestL1_SwarmBot.fact);
        
        final WorldBase.WorldBot brii=new WorldBase.WorldBot(){

            @Override
            public void setup(int P, int Id, int D, int Z, List<L0_2dLib.Point> xyZ) {
                //System.err.println("Wrapper setup exec "+xyZ);
                br.setup(P, Id, D, Z, xyZ);
            }

            @Override
            public void turn(int[] zline, List<List<L0_2dLib.Point>> droneLinesPerPlayer) {
                br.turn(zline, droneLinesPerPlayer);
            }

            @Override
            public List<L0_2dLib.Point> outorders() {
                //System.err.println("Wrapper outorders exec");
                List<L0_2dLib.Point> res=br.outorders();
                //System.err.println("Wrapper outorders exec "+res);
                return res;
            }

            @Override
            public String botName() {
                return "TestL1_SwarmBot";
            }
        };
        
        //WorldBase w=new WorldBase(10, 4, 9937777,new TesterBot(TestL1_GreedyBot.fact),new TesterBot(TestL1_DefenseBot.fact));
        //WorldBase w=new WorldBase(5, 4, 9937777,new WorldBase.TranquilleBot(),new TesterBot(TestL1_DefenseBot.fact));
        //WorldBase w=new WorldBase(9, 4, 9937777,new TesterBot(TestL1_DefenseBot.fact),new TesterBot(TestL1_GreedyBot.fact));
        WorldBase w=new WorldBase(5, 4, 110,new WorldBase.TranquilleBot(),new TesterBot(TestL1_DefenseBot.fact),new WorldBase.TranquilleBot(),new WorldBase.TranquilleBot());
        w.genWorld();
        
        int nbturn=NBTURNS;
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
    
    
    public static void main (String[] args){
        //testDefenderL1Bot();
        //testOffensiveL1Bot();
        testV2Simple();

    }
}
