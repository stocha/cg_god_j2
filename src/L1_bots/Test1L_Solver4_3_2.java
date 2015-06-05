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
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author Jahan
 */
public class Test1L_Solver4_3_2 extends L1_botStruct.BotBase {

    final int nbMoveForward = 100;

    public void outputText(String t) {
        if (false) {
            return;
        }

        System.err.println("" + this.getClass().getSimpleName() + " " + t);
    }

    public class Automat {

        public class State {

            final int id;
            final L0_2dLib.WithCoord realityLink;

            public State(int id, L0_2dLib.WithCoord coord) {
                this.id = id;
                realityLink = coord;
            }

            @Override
            public String toString() {
                return "State{" + "id=" + id + ", realityLink=" + realityLink + '}';
            }

        }

        public class Transition {

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

        final List<State> state = new ArrayList<>(4 * 3);
        final List<Transition> transition = new ArrayList<>(4 * 4 * 4);
        final HashMap<State, List<Transition>> stateTrans = new HashMap<>(Z * Z);

        int idState = 0;
        int idTransition = 0;

        final HashMap<Zone, State> ztos = new HashMap<>(Z);

        private State addState(L0_2dLib.WithCoord coord) {
            State s = new State(idState++, coord);
            this.state.add(s);
            return s;
        }

        private Transition addTransition(int cost, State start, State end) {
            Transition tt1 = new Transition(idTransition++, cost, start, end);
            Transition tt2 = new Transition(idTransition++, cost, end, start);

            this.transition.add(tt1);
            this.transition.add(tt2);

            if (!stateTrans.containsKey(start)) {
                stateTrans.put(start, new ArrayList<>(100));
            }
            if (!stateTrans.containsKey(end)) {
                stateTrans.put(end, new ArrayList<>(100));
            }

            stateTrans.get(start).add(tt1);
            stateTrans.get(end).add(tt2);

            return tt1;

        }

        final void buildIt() {

            Automat au = new Automat();

            List<RZoneZone> lrzz = _buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());

            for (RZoneZone z : lrzz) {
                if (!ztos.containsKey(z.a)) {
                    State s = addState(z.a);
                    ztos.put(z.a, s);
                    addTransition(1, ztos.get(z.a), ztos.get(z.a));
                }
                if (!ztos.containsKey(z.b)) {
                    State s = addState(z.b);
                    ztos.put(z.b, s);
                    addTransition(1, ztos.get(z.b), ztos.get(z.b));
                }

                int cost = (int) ((z.distance + 99) / 100);
                addTransition(cost, ztos.get(z.a), ztos.get(z.b));

            }

        }

        final String debug() {
            String res = "";

            for (State s : state) {
                res += "" + s + "\n";
                res += "" + stateTrans.get(s) + "\n";

            }

            return res;
        }

        public Simulation createSimulation(List<RZoneDrone> zd) {
            return new Simulation(zd);
        }

        public class Simulation {

            public class SimDr {

                final int id;
                final Drone source;

                public SimDr(int id, Drone source) {
                    this.id = id;
                    this.source = source;
                }
            }

            private List<SimDr> alldr = new ArrayList<>();
            
            final SimState start;
            
            
            /**
             * Do a simulation
             * @param nbTurn 
             */
            public void simulate(int nbTurn){
                SimState ss=new SimState();
                ss.copyStatCost(start);
                
            }

            private void addDr(Drone d) {
                int id = alldr.size();
                alldr.add(new SimDr(id, d));
            }

            public Simulation(List<RZoneDrone> zd) {
                alldr.clear();
                for (RZoneDrone r : zd) {
                    addDr(r.d);
                }

                start = new SimState();
                for (int i = 0; i < zd.size(); i++) {
                    State s = ztos.get(zd.get(i).z);
                    start.tostate[i] = s.id;
                    start.cost[i] = (int) (zd.get(i).dist - 1) / 100;

                }
            }

            public class SimState {

                final int tostate[];
                final int cost[];
                
                int date;
                
                public void copyStatCost(SimState src){
                    for(int i=0;i<src.tostate.length;i++){
                        this.tostate[i]=src.tostate[i];
                        this.cost[i]=src.cost[i];
                        this.date=src.date;
                    }                    
                }

                public SimState() {
                    int nbDr = alldr.size();
                    this.tostate = new int[nbDr];
                    this.cost = new int[nbDr];                    
                }
                
                public boolean incDate(){
                    date++;
                    boolean hasZero=false;
                    for(int i=0;i<cost.length;i++){
                        cost[i]--;
                        hasZero|=(cost[i]==0);
                    }
                                                           
                    return hasZero;
                }
            }

            public class Choice {

                final int drId;
                final int transTaken;
                int transWeight = 100;

                public Choice(int drId, int transTaken, int transWeight) {
                    this.drId = drId;
                    this.transTaken = transTaken;
                    this.transWeight = transWeight;
                }

            }
            
            public class ChoiceSpace{
                List<List<Choice>> choicePerDrone=new ArrayList<>(D);

                public ChoiceSpace(SimState from) {
                    for(int i=0;i<from.cost.length;i++){
                        if(from.cost[i]==0){
                            List<Choice> cc=new ArrayList<>();
                            choicePerDrone.add(cc);
                            
                            int nb=stateTrans.get(state.get(i)).size();
                            for(Transition t : stateTrans.get(state.get(i))){
                                cc.add(new Choice(i, t.id, 1024/nb));                            
                            }
                        }                        
                    }                    
                }
                
                public List<Choice> reduce(Random rand){
                    List<Choice> res=new ArrayList<>();
                    
                    
                    for(List<Choice> drc : choicePerDrone){
                        Choice last=drc.get(0);
                        int ranc=rand.nextInt()&1023;
                        int acc=0;
                        
                        for(Choice curc : drc){
                            acc+=curc.transWeight;
                            if(acc >= ranc) break;
                            
                            last=curc;
                        }
                        res.add(last);
                    }
                    
                    return res;
                }
                
            }     

        }// Automat

    }

    public Automat calcAutomat() {
        outputText("Calc automat");

        Automat aut = new Automat();
        aut.buildIt();
        outputText("" + aut.debug());

        final RZoneZone first;
        RZoneZone second;
        RZoneZone third = null;

        List<RZoneZone> lrzz = _buildRZoneZone().setDistance().stream().sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());
        List<Drone> allDrone = _drone.get(_me);
        first = lrzz.get(0);
        _order.get(allDrone.get(0)).set(first);

        outputText("Selected first segment " + first);
        for (RZoneZone it : lrzz) {

            //outputText(" "+it);
        }

        lrzz = _buildRZoneZone().setDistance().stream()
                .filter(e -> ((e.a != first.a) && (e.a != first.b) && (e.b != first.a) && (e.b != first.b)))
                .sorted(comp_zz_bydist.reversed()).collect(Collectors.toList());

        second = lrzz.get(0);
        _order.get(allDrone.get(1)).set(second);

        return aut;

    }

    public Test1L_Solver4_3_2(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
    }

    public class IA {

        public class GState {

            //final int[] stateLevel;

            final int[] statePos;
            final int[] stateSpeed;
            final int[] turnToChoice;
            final int[] score;
            final int[] owners;

            //Z*(P*D)
            public GState() {
                //stateLevel=new int[Z*P*D];
                statePos = new int[P * D * 2];
                stateSpeed = new int[P * D * 2];
                turnToChoice = new int[P * D];
                score = new int[P];
                owners = new int[Z];
            }

            public int getPosX(int z, int p, int d) {
                return statePos[d + (p * d) * 2];
            }

            public int getPosY(int z, int p, int d) {
                return statePos[d + (p * d) * 2 + 1];
            }

            public void setPosX(int z, int p, int d, int val) {
                statePos[d + (p * d) * 2] = val;
            }

            public void setPosY(int z, int p, int d, int val) {
                statePos[d + (p * d) * 2 + 1] = val;
            }

//            public int get(int z,int p,int d){
//                return stateLevel[d+(p*d)+(z*p*d)];            
//            }
//            
//            public void set(int z,int p,int d,int vallevel){
//                stateLevel[d+(p*d)+(z*p*d)]=vallevel;
//            }        
        }

        public class Simulation {

            final int rewards[] = new int[Z * 2];

            final GState state[] = new GState[nbMoveForward];
            final int choice[] = new int[nbMoveForward];

            public Simulation() {
                for (int i = 0; i < nbMoveForward; i++) {
                    state[i] = new GState();
                }
            }

        }

    }

    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new Test1L_Solver4_3_2(P1, Id1, D1, Z1);

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

        System.err.println("generating orders " + _turnNumber);

        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }

}
