/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.apriori;

import cn.edu.buaa.sei.dm.dianping.preprocess.Utility;
import cn.edu.buaa.sei.dm.dianping.rawdata.ShopRawData;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import cn.edu.buaa.sei.dm.dianping.resolution.ReadOnlyDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gong
 */
public class Main {

    public static void main(String[] args) {
        final ReadOnlyDatabase database = Utility.loadDatabase(); // Shop database
        final Map<Integer, Location> locMap = Utility.loadLocationMap(); // Location database

        final Apriori apriori = new Apriori(database, locMap, new ReadOnlyDatabase.Filter() {
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

        apriori.min_support = 0.00;
        apriori.min_confidence = 0.10;
        apriori.max_item_set_size = 3;

	System.out.println(apriori.getTransactionCount() + " valid records loaded.");

        // define initial items
        List<ItemSet> init_set = new ArrayList<ItemSet>();
        initCandidate(init_set);

        // do mining
        List<Rule> strong_rules = apriori.doMining(init_set);
        
    }

    private static void initCandidate(List<ItemSet> c) {
	final int group_good = 1;
	final int group_bad = 2;
        final int group_feature = 3;
        final int group_buildtype = 4;

//        c.add(new Item("准五星", group_good) {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.overall >= 45;
//            }
//        }.makeSet());
//	c.add(new Item("少于三星", group_bad) {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.overall < 30;
//            }
//        }.makeSet());
//        c.add(new Item("环境优异", group_good) {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.environment >= 25; // top 10%
//            }
//        }.makeSet());
//        c.add(new Item("环境不佳", group_bad) {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.environment < 18; // avg = 17.7
//            }
//        }.makeSet());
//        c.add(new Item("服务优异", group_good) {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.service >= 23; // top 10%
//            }
//        }.makeSet());
//        c.add(new Item("服务不佳", group_bad) {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.service < 17; // avg = 17.0
//            }
//        }.makeSet());
//        c.add(new Item("口味优异", group_good) {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.taste >= 24; // top 10%
//            }
//        }.makeSet());
//        c.add(new Item("口味不佳", group_bad) {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.taste < 20; // avg = 19.9
//            }
//        }.makeSet());
//        c.add(new Item("便宜") {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.price <= 50;
//            }
//        }.makeSet());
//        c.add(new Item("昂贵") {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.price >= 100;
//            }
//        }.makeSet());
//        c.add(new Item("人气") {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.count >= 564; // avg = 245, top 10% = 564
//            }
//        }.makeSet());
//        c.add(new Item("冷清") {
//            @Override
//            public boolean includedInTranscation(ShopRawData data) {
//                return data.rating.count <= 12; // bottom 10%
//            }
//        }.makeSet());
//
//        for (String tag : tags) {
//            c.add(new Item.TagItem(tag).makeSet());
//        }
//        
//        for (String feature : features) {
//            c.add(new Item.FeatureItem(feature, group_feature).makeSet());
//        }
        
        c.add(new Item("日本料理", Item.Constraint.LeftOnly) {
            @Override
            public boolean includedInTranscation(ShopRawData data) {
                return (data.tags.contains("日本料理"));
            }
        }.makeSet());
        
//        for (String type : buildTypes) {
//            c.add(new Item(type, group_buildtype) {
//                
//                @Override
//                public boolean includedInTranscation(ShopRawData data) {
//                    Map<String, Integer> types = MapInfoCenter.getSurroundingBuildingTypes(data.id);
//                    if (types != null) {
//                        Integer cnt = types.get(this.getName());
//                        if (cnt != null && cnt > 0) {
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    } else {
//                        return false;
//                    }
//                }
//                
//            }.makeSet());
//        }

    }
    
    private static final String[] tags = {
        "婚宴酒店",
        "其他火锅",
        "家常菜",
        "韩国料理",
        "川菜",
        "湘菜",
        "粤菜",
        "面包西点",
        "自助餐",
        "烧烤",
        "快餐/简餐",
        "东北菜",
        "粉面馆",
        "烤鸭",
        "其他小吃",
        "甜品饮品",
        "寿司/简餐",
        "日本料理",
        "香锅",
        "老北京小吃",
        "比萨",
    };
    
    private static final String[] features = {
        "有午市套餐",	
        "有露天位",	
        "有表演",	
        "可以刷卡",	
        "免费停车",	
        "可送外卖",	
        "24小时营业",	
        "有包房",	
        "供应早餐",	
        "有景观位",	
        "供应夜宵",	
        "有下午茶",	
        "无线上网",	  
    };
    
    private static final String[] buildTypes = {
//        "restaurant",
        "store",
        "lodging",
        "health",
        "finance",
        "school",
        "hospital",
        "beauty_salon",
        "convenience_store",
        "bank",
        "bakery",
        "atm",
        "cafe",
        "bar",
        "parking",
        "pharmacy",
        "car_repair",
        "local_government_office",
        "shopping_mall",
        "university",
//        "car_dealer",
//        "gym",
//        "real_estate_agency",
//        "laundry",
//        "travel_agency",
//        "lawyer",
//        "museum",
//        "shoe_store",
//        "stadium",
//        "grocery_or_supermarket",
//        "movie_theater",
//        "night_club",
//        "transit_station",
//        "train_station",
//        "veterinary_care",
//        "subway_station",
//        "post_office",
//        "book_store",
//        "police",
//        "park",
//        "gas_station",
//        "embassy",
//        "home_goods_store",
//        "furniture_store",
//        "general_contractor",
//        "bicycle_store",
//        "electronics_store",
//        "accounting",
//        "jewelry_store",
//        "insurance_agency",
//        "clothing_store",
//        "place_of_worship",
//        "car_rental",
//        "florist",
//        "courthouse",
//        "church",
//        "doctor",
//        "library",
//        "locksmith",
//        "moving_company",
//        "amusement_park",
//        "zoo",
//        "aquarium",
//        "dentist",
//        "funeral_home",
//        "airport",
//        "bowling_alley",
//        "car_wash"
    };

}
