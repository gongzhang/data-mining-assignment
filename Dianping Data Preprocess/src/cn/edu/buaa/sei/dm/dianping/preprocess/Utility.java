/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.preprocess;

import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import cn.edu.buaa.sei.dm.dianping.resolution.ReadOnlyDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gong
 */
public class Utility {

    private static Map<Integer, Location> locationMap;
    
    public static Map<Integer, Location> loadLocationMap() {
        if (locationMap != null)
            return locationMap;
        try {
            File file = new File("../Dianping Address Resolution/location.dat");
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Map<Integer, Location> rst = (Map<Integer, Location>) ois.readObject();
            ois.close();
            locationMap = rst;
            return locationMap;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(cn.edu.buaa.sei.dm.dianping.resolution.Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(cn.edu.buaa.sei.dm.dianping.resolution.Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static ReadOnlyDatabase loadDatabase() {
	return ReadOnlyDatabase.getInstance();
    }

}
