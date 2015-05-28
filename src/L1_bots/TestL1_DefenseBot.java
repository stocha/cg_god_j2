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
public class TestL1_DefenseBot extends L1_botStruct.BotBase {
    
    public class IA {
        
        List<RZoneDrone> sortedRzd=null;
        
        public class DroneInfo {

            HashMap<Zone, Integer> meLevel = new HashMap<>(Z);

            @Override
            public String toString() {
                return "DI{" + "meLevel=" + meLevel + '}';
            }
            
            
            
        }        
        HashMap<Drone, DroneInfo> droneInfo = new HashMap<>(D);
        
        public class ZoneInfo {

            List<Drone> closest = new ArrayList<>(D * P);
            List<Drone> defender= new ArrayList<>(D);
            List<Drone> defCandidat=new ArrayList<>(D);             

            @Override
            public String toString() {
                return "ZI{"+" defend "+defender + "clo=" + closest +""+ '}';
            } 
            
            
        }
         HashMap<Zone, ZoneInfo> zoneInfo = new HashMap<>(D);
        
        final int nbDroneDef;
        
        public IA(int nbDroneDef) {
            this.nbDroneDef = nbDroneDef;
        }
        
        private void buildDroneZoneInfo() {
            sortedRzd= _rzonedrone.stream().sorted(byDist.reversed()).collect(Collectors.toList())            ;
            
            for (PlayerAI p : _player) {
                for (Drone d : _drone.get(p)) {
                    droneInfo.put(d, new DroneInfo());
                }
            }
            for(Zone z : _zone){
                zoneInfo.put(z, new ZoneInfo());
            }
            
            for (RZoneDrone rdz : sortedRzd) {
                int level=(int)((rdz.dist-1)/100);
                droneInfo.get(rdz.d).meLevel.put(rdz.z, level);
                zoneInfo.get(rdz.z).closest.add(rdz.d);
                
              // System.err.println(""+rdz);
            }
            
        }
        
        public void greedyDoing() {
            L1_botStruct.BotBase.Zone cible = null;
            
            HashMap<Drone, Boolean> droneDone = new HashMap(D);
            
            for (RZoneDrone rzd : _rzonedrone.stream().sorted(byDist.reversed()).collect(Collectors.toList())) {
                // System.err.println(""+rzd);

                if (rzd.d.owner == _me && !droneDone.containsKey(rzd.d) && rzd.z.owner != _me) {
                    _order.get(rzd.d).set(rzd.z);
                    //   System.err.println(""+rzd.d+" is heading to "+rzd.z);
                    droneDone.put(rzd.d, true);
                }
            }
        }
        
        public void defenseDoing() {
            buildDroneZoneInfo();
            List<Drone> done=new ArrayList<>(D);          
            
            for(RZoneDrone rzd : sortedRzd){
                Drone d = rzd.d;
                Zone z = rzd.z;
                
                    if(done.contains(d)) continue;
                    
                    if(d.owner==_me){
                        zoneInfo.get(z).defCandidat.add(d);
                    }else
                    if(d.owner!=_me && zoneInfo.get(z).defCandidat.isEmpty()){
                        //heeem ... failure.
                    }else if(d.owner!=_me && !zoneInfo.get(z).defCandidat.isEmpty() && zoneInfo.get(z).defender.size()<2){
                        Drone dd = zoneInfo.get(z).defCandidat.get(0);
                        zoneInfo.get(z).defCandidat.remove(dd);
                        done.add(dd);
                        zoneInfo.get(z).defender.add(dd);
                        //System.err.println("Defending "+z+" with "+dd+" against "+d);
                        //System.err.println("Already defending "+done);
                        if(droneInfo.get(dd).meLevel.get(z) +2 <= droneInfo.get(d).meLevel.get(z)){
                            _order.put(dd, d.cor);
                        }else{
                            _order.put(dd, z.cor);
                        }
                    }                
            
            }                                    
        }
        
        public void reflechirTour() {            
            if (_controled.get(_me).size() < 2) {
                greedyDoing();
            } else {
                defenseDoing();
            }
            
        }
        
    }
    
    private final IA ia;
    
    public TestL1_DefenseBot(int P, int Id, int D, int Z) {
        super(P, Id, D, Z);
        ia = new IA(D / 2);
    }
    
    public static L1_botStruct.BotFactory fact = (int P1, int Id1, int D1, int Z1) -> new TestL1_DefenseBot(P1, Id1, D1, Z1);
    
    @Override
    public List<L0_2dLib.Point> outorders() {
        ia.reflechirTour();
        return super.outorders(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
