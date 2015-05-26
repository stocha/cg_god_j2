/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import tools.L0_2dLib.Point;

/**
 *
 * @author denis
 */
public class WorldBase {

    public static final int world_width = 4000;
    public static final int world_height = 1800;
    public static final int nb_turns = 200;

    public static interface WorldBot {

        /**
         *
         * @param xyZ Z*2 int (x,y)
         */
        public void setup(int P, int Id, int D, int Z, List<Point> xyZ);

        /**
         *
         * @param zline Z int (owner)
         * @param droneLinesPerPlayer P*D*2 p-> d->int (x,y)
         */
        public void turn(int[] zline, List<Point> droneLinesPerPlayer);

        /**
         * D*2 int (x,y)
         *
         * @return
         */
        public List<Point> outorders();
    }
    
    public static class BotDefault implements WorldBot{
        
        List<Point> droneLines;

        @Override
        public void setup(int P, int Id, int D, int Z, List<Point> xyZ) {
            
        }

        @Override
        public void turn(int[] zline, List<Point> droneLinesPerPlayer) {
            this.droneLines=droneLinesPerPlayer;
        }

        @Override
        public List<Point> outorders() {
            return this.droneLines;
        }
    }

    private class Turn {

        final List<List<Point>> playerDrones;
        final List<List<Point>> playerDronesOrders;
        final int owners[];
        final int scores[];

        public Turn() {
            playerDrones = new ArrayList<>(P);
            playerDronesOrders = new ArrayList<>(P);
            for (int p = 0; p < P; p++) {
                List<Point> dr = new ArrayList<>(D);
                List<Point> or = new ArrayList<>(D);
                for (int d = 0; d < D; d++) {
                    dr.add(new Point(0, 0));
                    or.add(new Point(0, 0));
                }
                playerDrones.add(dr);
                playerDronesOrders.add(or);
            }
            owners = new int[Z];
            scores = new int[P];
        }

        public void copy(Turn s) {
            for (int p = 0; p < P; p++) {
                for (int d = 0; d < D; d++) {
                    playerDrones.get(p).get(d).set(s.playerDrones.get(p).get(d));
                    playerDronesOrders.get(p).get(d).set(s.playerDronesOrders.get(p).get(d));
                }
                scores[p] = s.scores[p];
            }
            for (int z = 0; z < Z; z++) {
                owners[z]=s.owners[z];
            }
        }

    }

    int D;
    final int Z;
    int P;
    final List<WorldBot> bots;
    final List<Point> zones;
    final List<Turn> turn;
    final Random rand;

    public WorldBase(int D, int Z,long seed, WorldBot... bots) {
        this.D = D;
        this.Z = Z;
        this.P = bots.length;

        this.bots = Arrays.asList(bots);

        zones = new ArrayList<>(Z);
        for (int i = 0; i < Z; i++) {
            zones.add(new Point(0, 0));
        }

        this.turn = new ArrayList<>(nb_turns);
        
        this.rand=new Random(seed);
    }
    
    public void genWorld(){
        
        turn.add(new Turn());
        
        for(int p=0;p<P;p++){
            turn.get(turn.size()-1).owners[p]=-1;
        }
        
        int i=0;
        for(WorldBot b : bots){
            b.setup(P, i++, D, Z, zones);
        }
        
        final int dec=400;
        for(Point zz : zones){
            zz.set(((rand.nextInt()&0xFFFF)%((int)world_width-dec)),((rand.nextInt()&0xFFFF)%(int)(world_height-dec)));
            zz.x+=dec/2;
            zz.y+=dec/2;
        }
        
        for(Point d : turn.get(turn.size()-1).playerDrones.get(0)){
            d.set(((rand.nextInt()&0xFFFF)%(int)(world_width-dec)),((rand.nextInt()&0xFFFF)%(int)(world_height-dec)));
            d.x+=dec/2;
            d.y+=dec/2;
        }
    }
    public boolean genTurn(){
        int i=0;
        for(WorldBot b : bots){
            b.turn(turn.get(turn.size()-1).owners, turn.get(turn.size()-1).playerDrones.get(i++));
        }        
        
        return turn.size()<=200;
    }
    
    public void doRun(){
        while(genTurn()){}
    }
    
    public void doRun(int nbTurn){
        while(genTurn() && turn.size()<=nbTurn){}
    }    

    
    public String debug_turnAt(int num, double scale){
        String res="";
        int width=(int)(world_width*scale);
        int height=(int)(world_height*scale);
        
        String[][] im=new String[width][height];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                im[j][i]="--";
            }
        }
        
        for(int p=0;p<P;p++){
            for(int d=0;d<D;d++){
                int x=(int)(turn.get(num).playerDrones.get(p).get(d).x * scale);
                int y=(int)(turn.get(num).playerDrones.get(p).get(d).y * scale);
                int cp='0'+(char)p;
                int cd='a'+(char)d;
                
            im[x][y]=""+(char)cp+(char)cd;
                               
            }
        }
        
        int z=0;
        for(Point zz : zones){
            int x=(int)(zz.x()*scale);
            int y=(int)(zz.y()*scale);
            char p='#';
            
            im[x][y]=""+p+((char)((char)'A'+(char)z));
                    z++;
        }
        
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                res+= im[j][i];
            }
            res+="\n";
        }
        res+="\n";
        return res;
    }
}
