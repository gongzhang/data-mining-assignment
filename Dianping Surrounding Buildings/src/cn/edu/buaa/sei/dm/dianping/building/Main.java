/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.building;

import cn.edu.buaa.sei.dm.dianping.rawdata.ShopRawData;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import cn.edu.buaa.sei.dm.dianping.resolution.ReadOnlyDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 *
 * @author Qiang
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BuildingDatabase builddb = BuildingDatabase.getInstance();
        //ReadOnlyDatabase shopdb = cn.edu.buaa.sei.dm.dianping.preprocess.Utility.loadDatabase();
        Map<Integer, Location> map = cn.edu.buaa.sei.dm.dianping.preprocess.Utility.loadLocationMap();
        Location loc = map.get(512251);
        List<BuildingRawData> buildings = builddb.getBuildings(loc, 100);
        Map<String, Integer> statistic = new HashMap<String, Integer>();
        for (BuildingRawData build : buildings) {
            for (String type : build.types) {
                if (statistic.containsKey(type))
                    statistic.put(type, statistic.get(type) + 1);
                else
                    statistic.put(type, 1);
            }
        }
        for (Entry<String, Integer> e : statistic.entrySet()) {
            System.out.println(e.getKey() + "\t" + e.getValue());
        }
    }
}
