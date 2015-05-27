/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L1_bots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import L0_tools.L0_2dLib;

/**
 *
 * @author Jahan
 */
public class L1_botStruct {

    public static interface BotBridge {

        /**
         *
         * @param P
         * @param Id
         * @param xyZ Z*2 int (x,y)
         * @param Z
         * @param D
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
        
        public class Zone implements L0_2dLib.WithCoord{
            final int id;
            final L0_2dLib.Point cor;
            Player owner;

            public Zone(int id,Player owner) {
                this.id = id;
                this.cor=new L0_2dLib.Point();
                this.owner=owner;
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
        
        public class Drone implements L0_2dLib.WithCoord{
            final int id;
            final Player owner;
            
            final L0_2dLib.Point cor;

            public Drone(Player owner,int id) {
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
        
        public class Player{
            final int id;
            int score=0;

            public Player(int id) {
                this.id = id;
            }
            
            
        }        
        
        
        final int P;
        final int Id;
        final int D;
        final int Z;
        
        final Player _nullPlayer=new Player(-1);
        final Player _me;
        final List<Player> _player;
        final List<Zone> _zone;
        final HashMap<Player,List<Drone>> _drone;
        final HashMap<Player,List<Zone>> _controled;
        final HashMap<Drone,L0_2dLib.Point> _order;
        private final List<L0_2dLib.Point> res;     

        public BotBase(int P, int Id, int D, int Z) {
            this.P = P;
            this.Id = Id;
            this.D = D;
            this.Z = Z;
            
            _player =new ArrayList<>(P);
            for(int p=0;p<P;p++){
                _player.add(new Player(p));
            }
            _me=_player.get(Id);
            
            _zone =new ArrayList<>(Z);
            for(int z=0;z<Z;z++){
                _zone.add(new Zone(z,_nullPlayer));
            }            
            
            _drone=new HashMap<>(P);            
            
            for(Player p : _player){
                List<Drone> bbl=new ArrayList<>(D);
                for(int d=0;d<D;d++){
                    Drone bb=new Drone(p, d);
                    bbl.add(bb);
                }
                _drone.put(p,bbl);
                
            }
            
            _controled=new HashMap<>(P);
            for(Player p : _player){
                _controled.put(p, new ArrayList<>());
            }
            _controled.put(_nullPlayer, new ArrayList<>());
            
            _order=new HashMap<>(D);
            for(Drone d : _drone.get(_me)){
                _order.put(d, new L0_2dLib.Point());
            }
            
            res=new ArrayList<>(D);
            for(int d=0;d<D;d++){
                res.add(new L0_2dLib.Point());
            }
        }
        
        public void inputZones( List<L0_2dLib.Point> xyZ){
            for(Zone z : _zone){
                z.cor.set(xyZ.get(z.id));
                System.err.println("Inputing zone "+z+" from"+xyZ);
            }
            
        }
        
        public void inputTurnZonesOwner(int[] owners){
            for(Player p : _player){
                _controled.get(p).clear();
            }
            for(Zone z : _zone){
                if(owners[z.id]==-1) z.owner=_nullPlayer;
                else z.owner=_player.get(owners[z.id]);
                
                _controled.get(z.owner).add(z);
            }            
        }        
        
        public void inputTurnPlayerBot(int p,List<L0_2dLib.Point> xyZ){
            
        }
        
        
        public List<L0_2dLib.Point> outorders(){

            for(Drone d : _drone.get(_me)){
                res.set(d.id, _order.get(d));
            }
            
            return res;
        }
  
    }
    
        public static class BotBridgeImpl implements BotBridge {
            
            final BotFactory fact;
            BotBase bot=null;

            public BotBridgeImpl(BotFactory fact) {
                this.fact = fact;
            }
            
            
            
            @Override
            public void setup(int P, int Id, int D, int Z, List<L0_2dLib.Point> xyZ) {
                bot=fact.alloc(P, Id, D, Z);
                bot.inputZones(xyZ);
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
