package cn.edu.buaa.sei.dm.dianping.preprocess;

import cn.edu.buaa.sei.dm.dianping.rawdata.ShopRawData;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import cn.edu.buaa.sei.dm.dianping.resolution.ReadOnlyDatabase;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        final ReadOnlyDatabase database = Utility.loadDatabase(); // Shop database
        final Map<Integer, Location> locMap = Utility.loadLocationMap(); // Location database

        // create an iterator
//        Iterator<ShopRawData> it = database.iterator(new ReadOnlyDatabase.Filter() {
//            @Override
//            public boolean accept(ShopRawData data) {
//                // filter by this condition:
//                return data.rating.overall >= 45 && // 4.5 star or higher
//                        locMap.containsKey(data.id); // has location info. of the shop
//            }
//        });
        Iterator<ShopRawData> it = database.iterator();

        // iterates
        Map<String, Integer> tag_cnt = new HashMap<String, Integer>();
        while (it.hasNext()) {
            ShopRawData data = it.next();
            for (ShopRawData.Feature feature : data.features) {
                if (tag_cnt.containsKey(feature.name)) {
                    tag_cnt.put(feature.name, tag_cnt.get(feature.name) + 1);
                } else {
                    tag_cnt.put(feature.name, 1);
                }
            }
//	    System.out.println(sum / data.features.size());
        }

        for (Map.Entry<String, Integer> entry : tag_cnt.entrySet()) {
            System.out.println("\"" + entry.getKey() + "\",\t" + entry.getValue());
        }
    }

}
