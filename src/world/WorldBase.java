/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.util.stream.Stream;



/**
 *
 * @author denis
 */
public class WorldBase {
    public static final int world_width=4000;
    public static final int world_height=1800;
    
    public static interface WorldBot{
        public void setup(int P, int Id,int D,int Z,Stream<Integer> xyZ);
        public void turn(Stream<Integer> zline,Stream<Integer> droneLines);
        public Stream<Integer> outorders();
    }
    
    
    
    
}
