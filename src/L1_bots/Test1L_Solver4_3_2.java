/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package L1_bots;

import L0_tools.L0_2dLib;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class Test1L_Solver4_3_2 extends L1_botStruct.BotBase {
    
    final int nbMoveForward=100;

    public Test1L_Solver4_3_2(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }
    
    public class IA{
        public class GState{
            //final int[] stateLevel;
            final int[] statePos;
            final int[] stateSpeed;
            final int[] turnToChoice;
            final int[] score;
            final int[] owners;            

            //Z*(P*D)
            public GState() {
                //stateLevel=new int[Z*P*D];
                statePos=new int[P*D*2];
                stateSpeed=new int[P*D*2];
                turnToChoice=new int[P*D];
                score=new int[P];
                owners=new int[Z];
            }
            
            public int getPosX(int z,int p,int d){
                return statePos[d+(p*d)*2];            
            }    
            public int getPosY(int z,int p,int d){
                return statePos[d+(p*d)*2+1];            
            }        
            
            public void setPosX(int z,int p,int d,int val){
                statePos[d+(p*d)*2]=val;
            }
            public void setPosY(int z,int p,int d,int val){
                statePos[d+(p*d)*2+1]=val;
            }            
        
//            public int get(int z,int p,int d){
//                return stateLevel[d+(p*d)+(z*p*d)];            
//            }
//            
//            public void set(int z,int p,int d,int vallevel){
//                stateLevel[d+(p*d)+(z*p*d)]=vallevel;
//            }        
        }
                
        public class Simulation{
            final int rewards[] =new int[Z*2];
            
            final GState state[] =new GState[nbMoveForward];
            final int choice[] =new int[nbMoveForward];

            public Simulation() {
                for(int i=0;i<nbMoveForward;i++){
                    state[i]=new GState();                           
                }                
            }
            
            
        }
    
    
    
    }
    
    public static L1_botStruct.BotFactory fact=(int P1, int Id1, int D1, int Z1) -> new Test1L_Solver4_3_2(P1, Id1, D1, Z1);

    @Override
    public List<L0_2dLib.Point> outorders() {
        
        
        System.err.println("generating orders "+_turnNumber);

        L1_botStruct.BotBase.Zone cible=null;
        
        
        HashMap<Drone,Boolean> droneDone=new HashMap(D);
        

        for(RZoneDrone rzd : this._buildRZoneDrone().setDistanceCalc().stream().sorted(byDist.reversed()).collect(Collectors.toList())){
           // System.err.println(""+rzd);
            
            if(rzd.d.owner==_me && !droneDone.containsKey(rzd.d) && rzd.z.owner!=_me){
                _order.get(rzd.d).set(rzd.z);
             //   System.err.println(""+rzd.d+" is heading to "+rzd.z);
                droneDone.put(rzd.d, true);
            }        
        }
        
        
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }
    
}

