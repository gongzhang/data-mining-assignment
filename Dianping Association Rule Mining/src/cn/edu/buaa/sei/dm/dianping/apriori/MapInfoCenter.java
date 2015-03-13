/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.apriori;

import cn.edu.buaa.sei.dm.dianping.building.BuildingDatabase;
import cn.edu.buaa.sei.dm.dianping.building.BuildingRawData;
import cn.edu.buaa.sei.dm.dianping.preprocess.Utility;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Qiang
 */
public class MapInfoCenter {
    
    public static double RADIUS = 100;
    
    private static Map<Integer, Map<String, Integer>> mapInfoCache = new HashMap<Integer, Map<String, Integer>>();
    
    public static Map<String, Integer> getSurroundingBuildingTypes(int shopID) {
        if (mapInfoCache.containsKey(shopID))
            return mapInfoCache.get(shopID);
        Map<String, Integer> statistic = new HashMap<String, Integer>();
        
        Map<Integer, Location> map = Utility.loadLocationMap();
        Location loc = map.get(shopID);
        if (loc == null) return null;
        BuildingDatabase buildDB = BuildingDatabase.getInstance();
        List<BuildingRawData> buildings = buildDB.getBuildings(loc, RADIUS);
        for (BuildingRawData build : buildings) {
            for (String type : build.types) {
                if (statistic.containsKey(type))
                    statistic.put(type, statistic.get(type) + 1);
                else
                    statistic.put(type, 1);
            }
        }
        mapInfoCache.put(shopID, statistic);
        return statistic;
    }
}
