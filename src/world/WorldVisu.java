/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import L0_tools.L0_2dLib;

/**
 *
 * @author Jahan
 */
public class WorldVisu {

    static class View extends JPanel {

        final WorldBase w;
        int currTurn = 0;
        double scale = 0.4;
        double scaleScree = scale;
        int droneRadius = 4;

        double xs = 0;
        double ys = 0;

        final Color pcol[] = new Color[]{java.awt.Color.GREEN, java.awt.Color.BLUE, java.awt.Color.RED, java.awt.Color.WHITE, java.awt.Color.YELLOW};

        public View(WorldBase w) {
            this.w = w;

            this.setPreferredSize(new Dimension((int) (4000 * scaleScree), (int) (1800 * scaleScree)));

            this.addMouseWheelListener(new MouseWheelListener() {

                @Override
                public void mouseWheelMoved(MouseWheelEvent mwe) {
                    int cli = mwe.getWheelRotation();
                    if (cli > 0) {
                        for (int i = 0; i < cli; i++) {
                            double pcenterx = scale * (WorldBase.world_width / 2.0);
                            double pcentery = scale * (WorldBase.world_height / 2.0);
                            scale *= 1.10;
                            double ncenterx = scale * (WorldBase.world_width / 2.0);
                            double ncentery = scale * (WorldBase.world_height / 2.0);
                            xs -= ncenterx - pcenterx;
                            ys -= ncentery - pcentery;

                        }
                    } else {
                        cli = -cli;
                        for (int i = 0; i < cli; i++) {
                            double pcenterx = scale * (WorldBase.world_width / 2.0);
                            double pcentery = scale * (WorldBase.world_height / 2.0);
                            scale *= 0.9;
                            double ncenterx = scale * (WorldBase.world_width / 2.0);
                            double ncentery = scale * (WorldBase.world_height / 2.0);
                            xs -= ncenterx - pcenterx;
                            ys -= ncentery - pcentery;
                        }
                    }
                    repaint();
                }
            });

            this.addMouseMotionListener(new MouseMotionListener() {

                int lastx = 0;
                int lasty = 0;

                @Override
                public void mouseDragged(MouseEvent me) {
                    xs += me.getX() - lastx;
                    ys += me.getY() - lasty;

                    lastx = me.getX();
                    lasty = me.getY();

                    repaint();
                }

                @Override
                public void mouseMoved(MouseEvent me) {
                    lastx = me.getX();
                    lasty = me.getY();
                }
            });
        }

        public void nextTurn() {
            currTurn++;
            if (currTurn >= w.turn.size()) {
                currTurn = w.turn.size() - 1;
            }
        }

        public void setTurn(int i) {
            // System.err.println("setting "+i+" turn gen : "+w.turn.size());

            currTurn = i;

            if (currTurn >= w.turn.size()) {
                currTurn = w.turn.size() - 1;
            }

            this.repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g); //To change body of generated methods, choose Tools | Templates.

            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            int ox = (int) (xs);
            int oy = (int) (ys);

            int zoneradius = (int) ((WorldBase.world_ctdist * scale));

            for (L0_2dLib.Point zz : w.zones) {
                int x = (int) (zz.x() * scale);
                int y = (int) (zz.y() * scale);
                g.setColor(new Color(25, 25, 25));
                for (int i = 2; i < 10; i++) {
                    g.drawOval(ox + x - zoneradius * i, oy + y - zoneradius * i, zoneradius * i * 2, zoneradius * i * 2);
                }
            }

            for (int p = 0; p < w.P; p++) {
                g.setColor(pcol[p]);
                for (int d = 0; d < w.D; d++) {
                    int x = (int) (w.turn.get(currTurn).playerDrones.get(p).get(d).x * scale);
                    int y = (int) (w.turn.get(currTurn).playerDrones.get(p).get(d).y * scale);
                    int cp = '0' + (char) p;
                    int cd = 'a' + (char) d;

                    g.fillOval(ox + x - droneRadius, oy + y - droneRadius, droneRadius * 2, droneRadius * 2);
                    g.drawString("" + (char) cp + (char) cd, ox + x - droneRadius, oy + y - droneRadius);

                    //im[x][y] = "" + (char) cp + (char) cd;
                }
            }

            int z = 0;
            g.setColor(java.awt.Color.darkGray);
            for (L0_2dLib.Point zz : w.zones) {
                int x = (int) (zz.x() * scale);
                int y = (int) (zz.y() * scale);
                char cc = '#';

                int owner = w.turn.get(currTurn).owners[z];

                if (owner != -1) {
                    cc = (char) ('0' + w.turn.get(currTurn).owners[z]);
                    g.setColor(pcol[owner]);
                } else {
                    g.setColor(java.awt.Color.darkGray);
                }

                String zs = "" + cc + ((char) ((char) 'A' + (char) z));

                g.fillOval(ox + x - zoneradius, oy + y - zoneradius, zoneradius * 2, zoneradius * 2);
                g.drawString(zs, ox + x - zoneradius, oy + y - zoneradius);

                g.setColor(java.awt.Color.darkGray);
                g.fillRect(ox + (20 * (0)) + x - zoneradius + 15, oy + y - 15, 55, 20);
                for (int p = 0; p < w.P; p++) {

                    g.setColor(pcol[p]);
                    g.drawString("" + w.turn.get(currTurn).zonePlayCount.get(z).get(p), ox + (20 * (p + 1)) + x - zoneradius, oy + y);
                }

                z++;
            }

        }

    }

    public static class MyPanel extends JPanel {

        final WorldBase w;

        public MyPanel(WorldBase wPar) {
            View v = new View(wPar);
            this.setLayout(new BorderLayout());
            this.add(v, BorderLayout.CENTER);
            this.w = wPar;

            JPanel right = new JPanel();
            right.setLayout(new GridLayout(0, 2));
            right.add(new JLabel("Turn"));
            JLabel numTurn = new JLabel("0000000");
            right.add(numTurn);

            JLabel lab[] = new JLabel[w.P * 2];
            for (int i = 0; i < w.P; i++) {
                lab[i * 2] = new JLabel(w.bots.get(i).botName());
                lab[i * 2 + 1] = new JLabel(w.bots.get(i).botName());
                right.add(lab[i * 2]);
                right.add(lab[i * 2 + 1]);
            }

            JSlider slide = new JSlider(0, 200, 0);
            slide.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    int val = slide.getValue();
                    // System.err.println("New value for turn = "+val);
                    v.setTurn(val);

                    for (int i = 0; i < w.P; i++) {
                        String sc = "" + w.turn.get(v.currTurn).scores[i];
                        lab[i * 2].setText(sc);
                    }

                    for (int i = 0; i < w.P; i++) {
                        String bn = "" + w.bots.get(i).botName();
                        lab[i * 2 + 1].setText(bn);
                    }

                    numTurn.setText("" + v.currTurn);
                }
            });

            this.add(slide, BorderLayout.SOUTH);
            this.add(right, BorderLayout.EAST);

        }

    }

    public static void create(WorldBase world) {
        //1. Create the frame.
        JFrame frame = new JFrame("Wolrd");

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //3. Create components and put them in the frame.
        //...create emptyLabel...
        frame.getContentPane().add(new MyPanel(world), BorderLayout.CENTER);

        //4. Size the frame.
        frame.pack();

        //5. Show it.
        frame.setVisible(true);

    }

}
