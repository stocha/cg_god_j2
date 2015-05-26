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
        public void setup(int P, int Id, int D, int Z, Stream<Integer> xyZ);

        /**
         *
         * @param zline Z int (owner)
         * @param droneLines P*D*2 p-> d->int (x,y)
         */
        public void turn(IntStream zline, IntStream droneLines);

        /**
         * D*2 int (x,y)
         *
         * @return
         */
        public IntStream outorders();
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
    }
    public boolean genTurn(){
        
        return turn.size()<200;
    }

}
