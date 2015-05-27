/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tools.L0_2dLib;

/**
 *
 * @author Jahan
 */
public class L1_botStruct {

    public static interface BotBridge {

        /**
         *
         * @param xyZ Z*2 int (x,y)
         */
        public void setup(int P, int Id, int D, int Z, List<L0_2dLib.Point> xyZ);

        /**
         *
         * @param zline Z int (owner)
         * @param droneLinesPerPlayer P*D*2 p-> d->int (x,y)
         */
        public void turn(int[] zline, List<List<L0_2dLib.Point>> droneLinesPerPlayer);

        /**
         * D*2 int (x,y)
         *
         * @return
         */
        public List<L0_2dLib.Point> outorders();
    }
    
    public static interface BotFactory{
        BotBase alloc(int P, int Id, int D, int Z);
    }

    public static class BotBase {        
        
        public class ZoneBase implements L0_2dLib.WithCoord{
            final int id;
            final L0_2dLib.Point cor;

            public ZoneBase(int id,L0_2dLib.Point cor) {
                this.id = id;
                this.cor=new L0_2dLib.Point(cor);
            }

            public String toString() {
                return "{Z"+id+cor.toString()+"}";
            }

            public double x() {
                return cor.x();
            }

            public double y() {
                return cor.y();
            }           

            public double sqDist(L0_2dLib.WithCoord c) {
                return cor.sqDist(c);
            }

            public double dist(L0_2dLib.WithCoord c) {
                return cor.dist(c);
            }
            
            @Override
            public void set(L0_2dLib.WithCoord c) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void set(double x, double y) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }            
            
        }
        
        public class DroneBase implements L0_2dLib.WithCoord{
            final int id;
            final PlayerBase owner;
            
            final L0_2dLib.Point cor;

            public DroneBase(PlayerBase owner,int id) {
                this.owner=owner;
                this.id = id;
                this.cor=new L0_2dLib.Point();
            }

            public String toString() {
                return "{Z"+id+cor.toString()+"}";
            }

            public double x() {
                return cor.x();
            }

            public double y() {
                return cor.y();
            }           

            public double sqDist(L0_2dLib.WithCoord c) {
                return cor.sqDist(c);
            }

            public double dist(L0_2dLib.WithCoord c) {
                return cor.dist(c);
            }            

            @Override
            public void set(L0_2dLib.WithCoord c) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void set(double x, double y) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
        
        public class PlayerBase{
            final int id;

            public PlayerBase(int id) {
                this.id = id;
            }
            
            
        }        
        
        public class Score{
            int score=0;
        }

        
        final int P;
        final int Id;
        final int D;
        final int Z;
        
        final PlayerBase _nullPlayer=new PlayerBase(-1);
        final PlayerBase _me;
        final List<PlayerBase> _player;
        final HashMap<PlayerBase,List<DroneBase>> _drone;
        final HashMap<PlayerBase,Score> _score;
        final HashMap<PlayerBase,List<ZoneBase>> _controled;

        public BotBase(int P, int Id, int D, int Z) {
            this.P = P;
            this.Id = Id;
            this.D = D;
            this.Z = Z;
            
            _player =new ArrayList<>(P);
            for(int p=0;p<P;p++){
                _player.add(new PlayerBase(p));
            }
            _me=_player.get(Id);
            
            _drone=new HashMap<>(P);            
            
            for(PlayerBase p : _player){
                List<DroneBase> bbl=new ArrayList<>(D);
                for(int d=0;d<D;d++){
                    DroneBase bb=new DroneBase(p, d);
                    bbl.add(bb);
                }
                _drone.put(p,bbl);
                
            }
            
            _controled=new HashMap<>(P);
            _score=new HashMap<>(P);
            for(PlayerBase p : _player){
                _controled.put(p, new ArrayList<>(Z));
                _score.put(p, new Score());
            }
        }
        
        public void inputZones( List<L0_2dLib.Point> xyZ){
            
            
        }
        
        public void inputTurnZonesOwner(int[] owners){
            
        }        
        
        public void inputTurnPlayerBot(int p,List<L0_2dLib.Point> xyZ){
            
        }
        
        
        public List<L0_2dLib.Point> outorders(){
            return null;
        }
        
        
        
        public static class BotBridgeImpl implements BotBridge {
            
            final BotFactory fact;
            BotBase bot=null;

            public BotBridgeImpl(BotFactory fact) {
                this.fact = fact;
            }
            
            
            
            @Override
            public void setup(int P, int Id, int D, int Z, List<L0_2dLib.Point> xyZ) {
                fact.alloc(P, Id, D, Z);
            }

            @Override
            public void turn(int[] zline, List<List<L0_2dLib.Point>> droneLinesPerPlayer) {
                bot.inputTurnZonesOwner(zline);
                
                for(int i=0;i<droneLinesPerPlayer.size();i++){
                    bot.inputTurnPlayerBot(i,droneLinesPerPlayer.get(i));
                }
                
            }

            @Override
            public List<L0_2dLib.Point> outorders() {
                return bot.outorders();
            }
        }
        
        
    }
}
