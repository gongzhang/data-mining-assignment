/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.apriori;

import cn.edu.buaa.sei.dm.dianping.preprocess.Utility;
import cn.edu.buaa.sei.dm.dianping.rawdata.ShopRawData;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import cn.edu.buaa.sei.dm.dianping.resolution.ReadOnlyDatabase;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author gong
 */
public class Main2 {
    
    public static void main(String[] args) {
        final ReadOnlyDatabase database = Utility.loadDatabase(); // Shop database
        final Map<Integer, Location> locMap = Utility.loadLocationMap(); // Location database
        Iterator<ShopRawData> it = database.iterator(new ReadOnlyDatabase.Filter() {
            @Override
            public boolean accept(ShopRawData data) {
                Location loc = locMap.get(data.id);
                return !(loc == null || 
                        loc.lat > cn.edu.buaa.sei.dm.dianping.building.Utility.MAX_LAT ||
                        loc.lat < cn.edu.buaa.sei.dm.dianping.building.Utility.MIN_LAT ||
                        loc.lng > cn.edu.buaa.sei.dm.dianping.building.Utility.MAX_LNG ||
                        loc.lng < cn.edu.buaa.sei.dm.dianping.building.Utility.MIN_LNG);
            }
        });
        
        Map<String, Integer> types = MapInfoCenter.getSurroundingBuildingTypes(3525907);
        System.out.println(types);
        types = MapInfoCenter.getSurroundingBuildingTypes(1942477);
        System.out.println(types);
        
//        int cnt = 0;
//        while (it.hasNext()) {
//            ShopRawData data = it.next();
//            Map<String, Integer> types = MapInfoCenter.getSurroundingBuildingTypes(data.id);
//            System.out.println(types);
//            cnt++;
//            if (cnt >= 100) break;
//        }
    }
    
}
