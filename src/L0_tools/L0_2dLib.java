/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package L0_tools;

import java.util.List;

/**
 *
 * @author denis
 */
public class L0_2dLib {
    
    public interface WithCoord{
        double x();
        double y();
        
        void set(WithCoord c);
        void set(double x, double y);
    }    
    
    public static class Point implements WithCoord {
        public double x;
        public double y;
        
        public Point(){
        }
        
        public Point(WithCoord c){
            set(c);
        }

        @Override
        public String toString() {
            return "{" + "" + (int)x + "," + (int)y + '}';
        }
        
        
        
        public Point(double x, double y){
            set(x,y);
        }

        @Override
        public double x() {
            return x;
        }

        @Override
        public double y() {
            return y;
         }

        @Override
        public void set(WithCoord c) {
            this.x=c.x();
            this.y=c.y();
        }

        @Override
        public void set(double x, double y) {
            this.x=x;this.y=y;
        }
        
        public double sqDist(WithCoord c){
            return (x-c.x())*(x-c.x()) + (y-c.y())*(y-c.y());
        }
        
        public double dist(WithCoord c){
            return Math.sqrt((x-c.x())*(x-c.x()) + (y-c.y())*(y-c.y()));
        }  
        
        public Point ABdirAtDistFromA(WithCoord B, double dist){
            Point res=new Point();
            
            double dd=this.dist(B);
            if(dd==0) return new Point(this);
            double rat=dist/dd;
            res.set(B.x()-x, B.y()-this.y);
            res.set(res.x*rat,res.y*rat);
            res.set(res.x+this.x,res.y+this.y);
            
            return res;            
        }
        
        public void setAsBarycentre(WithCoord a, double ad, WithCoord b, double bd){
            double sum=ad+bd;
            double dx=ad*a.x() / sum+bd*b.x()/sum;
            double dy=ad*a.y() /sum +bd*b.y() /sum;
            
            this.set(dx,dy);
        }        
    }
    
        public static Point baryCentre(List<? extends WithCoord> l){
            Point res=new Point();
            if(l.size()==0) return res;
            if(l.size()==1){res.set(l.get(0));return res;}
            
            double p=2;
            res.setAsBarycentre(l.get(0),1, l.get(1),1);
            for(int i=2;i<l.size();i++){
                res.setAsBarycentre(res,i, l.get(i),1);
            
            }
            
            
            return res;
        }    
    
}
