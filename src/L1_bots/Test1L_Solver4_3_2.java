/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package L1_bots;

import L0_tools.L0_2dLib;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class Test1L_Solver4_3_2 extends L1_botStruct.BotBase {
    
    final int nbMoveForward=100;
    

    public void outputText(String t) {
        if(false) return;
        
        System.err.println("" + this.getClass().getSimpleName() + " " + t);
    }    
    

    
    public class Automat{
        
        public class State{
            final int id;
            final L0_2dLib.WithCoord realityLink;

            public State(int id, L0_2dLib.WithCoord coord) {
                this.id = id;
                realityLink=coord;
            }

            @Override
            public String toString() {
                return "State{" + "id=" + id + ", realityLink=" + realityLink + '}';
            }

            
        }

        public class Transition{
            final int id;
            final int cost;
            final State start;
            final State end;

            public Transition(int id, int cost, State start, State end) {
                this.id = id;
                this.cost = cost;
                this.start = start;
                this.end = end;
            }

            @Override
            public String toString() {
                return "Transition{" + "id=" + id + ", cost=" + cost + ", start=" + start + ", end=" + end + '}';
            }
            
            
        }        
        
        
    
    
        final List<State> state=new ArrayList<>(4*3);
        final List<Transition> transition=new ArrayList<>(4*4*4);
        final HashMap<State,List<Transition>> stateTrans=new HashMap<>(Z*Z);
        
        int idState=0;
        int idTransition=0;        
        
        
        final HashMap<Zone,State> ztos=new HashMap<>(Z);        
        
        private State addState(L0_2dLib.WithCoord coord){
            State s=new State(idState++,coord);
            this.state.add(s);
            return s;
        }
        
        private Transition addTransition(int cost, State start, State end){
            Transition tt1=new Transition(idTransition++, cost, start, end);
            Transition tt2=new Transition(idTransition++, cost, end, start);
            
            this.transition.add(tt1);this.transition.add(tt2);
            
            if(!stateTrans.containsKey(start)){
                stateTrans.put(start, new ArrayList<>(100));
            }
            if(!stateTrans.containsKey(end)){
                stateTrans.put(end, new ArrayList<>(100));
            }            

            stateTrans.get(start).add(tt1);
            stateTrans.get(end ).add(tt2);
            
            
            return tt1;
            
        }
        
        final void buildIt(){

            
            Automat au=new Automat();
            

            List<RZoneZone> lrzz=_buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());
            
            for(RZoneZone z : lrzz){
                if(!ztos.containsKey(z.a)){
                    State s=addState(z.a);
                    ztos.put(z.a, s);
                }
                if(!ztos.containsKey(z.b)){
                    State s=addState(z.b);
                    ztos.put(z.b, s);
                }                
                
                int cost=(int)((z.distance+99)/100);
                addTransition(cost, ztos.get(z.a), ztos.get(z.b));
   
                
                
                
            
            }
            
            
        }
        
        final String debug(){
            String res="";
            
            for(State s : state){
                res+=""+s+"\n";
                res+=""+stateTrans.get(s)+"\n";
            
            }
            
            return res;
        }
        
    }// Automat
    
    public Automat calcAutomat(){
        outputText("Calc automat");
        
        Automat aut =new Automat();
        aut.buildIt();
        outputText(""+aut.debug());
        
        final RZoneZone first;
        RZoneZone second;
        RZoneZone third=null;
        
        List<RZoneZone> lrzz=_buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());
        List<Drone> allDrone=_drone.get(_me);
        first=lrzz.get(0);
        _order.get(allDrone.get(0)).set(first);
        
        outputText("Selected first segment "+first);
        for(RZoneZone it : lrzz)
        {

            //outputText(" "+it);
        }
        
        lrzz=_buildRZoneZone().setDistance().stream()
                .filter( e->((e.a!=first.a) && (e.a!=first.b) && (e.b!=first.a) && (e.b!=first.b)) )
                        .sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());        
        
        second=lrzz.get(0);
        _order.get(allDrone.get(1)).set(second);
        
        return aut;
        
    }    

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
    public void inputTurnPlayerBot(int p, List<L0_2dLib.Point> xyZ) {
        super.inputTurnPlayerBot(p, xyZ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void inputZones(List<L0_2dLib.Point> xyZ) {
        super.inputZones(xyZ); //To change body of generated methods, choose Tools | Templates.
        
        calcAutomat();
    }

    
    
    @Override
    public List<L0_2dLib.Point> outorders() {
        
        
        System.err.println("generating orders "+_turnNumber);       

        
        
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }
    
}

