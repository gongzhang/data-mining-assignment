/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.building;

import cn.edu.buaa.sei.dm.dianping.preprocess.Utility;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import cn.edu.buaa.sei.dm.dianping.resolution.ReadOnlyDatabase;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;

/**
 *
 * @author Qiang
 */
public class DensityFrame extends javax.swing.JFrame {

    /**
     * Creates new form DensityFrame
     */
    public DensityFrame() {
        initComponents();
        final Map<Integer, Location> map = Utility.loadLocationMap();
        final ReadOnlyDatabase db = Utility.loadDatabase();
        final BuildingDatabase bdb = BuildingDatabase.getInstance();

        final Location lt = new Location(39.7, 116.09);
        final Location rb = new Location(40.185, 116.725);
        JPanel panel = new JPanel() {

            @Override
            public void paint(Graphics g) {
                super.paint(g);


//                g.setColor(Color.blue);
//                for (BuildingRawData build : bdb.getMap().values()) {
//                    Location loc = build.location;
//                    int x = (int)((loc.lng - lt.lng) / (rb.lng - lt.lng) * this.getWidth());
//                    int y = (int)((rb.lat - loc.lat) / (rb.lat - lt.lat) * this.getHeight());
//                    g.fillRect(x, y, 1, 1);
//                }
                for (Entry<Integer, Location> e : map.entrySet()) {
                    Location loc = e.getValue();
                    int x = (int) ((loc.lng - lt.lng) / (rb.lng - lt.lng) * this.getWidth());
                    int y = (int) ((rb.lat - loc.lat) / (rb.lat - lt.lat) * this.getHeight());
                    //System.out.println(loc.lat + "," + loc.lng + ";" + x + "," + y);
                    double count = db.get(e.getKey()).rating.count / 100.0;
                    int red = (int) (count * 255);
                    red = red > 255 ? 255 : red;
                    g.setColor(new Color(red, 0, 0));
                    g.fillRect(x, y, 1, 1);
                }

                Location center = new Location(39.9425, 116.4075);

                g.setColor(Color.blue);
                List<BuildingRawData> builds = bdb.getBuildings(center, 500);
                System.out.println(builds.size());
                for (BuildingRawData build : builds) {
                    Location loc = build.location;
                    int x = (int) ((loc.lng - lt.lng) / (rb.lng - lt.lng) * this.getWidth());
                    int y = (int) ((rb.lat - loc.lat) / (rb.lat - lt.lat) * this.getHeight());
                    //System.out.println(loc.lat + "," + loc.lng + ";" + x + "," + y);
                    g.fillRect(x, y, 1, 1);
                }
                int x2 = (int) ((center.lng - lt.lng) / (rb.lng - lt.lng) * this.getWidth());
                int y2 = (int) ((rb.lat - center.lat) / (rb.lat - lt.lat) * this.getHeight());
                g.setColor(Color.yellow);
                g.fillRect(x2, y2, 5, 5);
            }
        };

        this.add(panel);
        panel.setBackground(Color.white);
        panel.setBounds(0, 0, 1000, 600);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 807, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 444, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DensityFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DensityFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DensityFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DensityFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new DensityFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
