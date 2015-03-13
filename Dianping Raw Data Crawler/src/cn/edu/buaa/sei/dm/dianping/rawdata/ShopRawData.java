/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.rawdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 商户原始信息。
 * @author gong
 */
public final class ShopRawData implements Serializable {
    
    /** 商户ID (商户信息页面地址最后的数字)，用于判断是否为同一个商户信息 */
    public int id;
    
    /** 商户名字，如“麦当劳(新王府井店)”，非空 */
    public String shopName;
    
    /** 商户评级，内容包括总评、评价次数、口味打分、环境打分、服务打分 */
    public final Rating rating = new Rating();
    
    /** 人均 */
    public int price;
    
    /** 地址 */
    public String address;
    
    /** 标签（大众点评的标准标签，如“快餐/简餐”、“王府井/东单”） */
    public final List<String> tags = new ArrayList<String>();
    
    /** 商户特色，如“可以刷卡”、“免费停车”等，每个特色都有投票计数 */
    public final List<Feature> features = new ArrayList<Feature>();
    
    /** 商户氛围，如“随便吃吃”、“休闲小憩”、“朋友聚餐” */
    public final List<Feature> feelings = new ArrayList<Feature>();
    
    /** 商户特点，如“无线上网”、“可送外卖”、“有下午茶” */
    public final List<Feature> characteristics = new ArrayList<Feature>();

    /** 营业时间，可能为空 */
    public String openingHours;

    @Override
    public boolean equals(Object o) {
        return o instanceof ShopRawData ? ((ShopRawData) o).id == this.id : false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("[ID:%d, %s, 评级:%d, 人均:%d, 地址:%s, 标签总数:%d]",
                id, shopName, rating.overall, price, address,
                tags.size() + features.size() + feelings.size() + characteristics.size());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ShopRawData data = new ShopRawData();
        data.id = this.id;
        data.shopName = this.shopName;
        data.rating.overall = this.rating.overall;
        data.rating.count = this.rating.count;
        data.rating.taste = this.rating.taste;
        data.rating.environment = this.rating.environment;
        data.rating.service = this.rating.service;
        data.price = this.price;
        data.address = this.address;
        for (String tag : this.tags) data.tags.add(tag);
        for (Feature f : this.features) data.features.add((Feature) f.clone());
        for (Feature f : this.feelings) data.feelings.add((Feature) f.clone());
        for (Feature f : this.characteristics) data.characteristics.add((Feature) f.clone());
        data.openingHours = this.openingHours;
        return data;
    }
    
    public static final class Rating implements Serializable {
        public int overall; // 50、45、40、35、30...
        public int count;
        public int taste, environment, service;
    }
    
    public static final class Feature implements Serializable {
        public String name;
        public int count;
        public Feature(String name, int count) {
            this.name = name;
            this.count = count;
        }
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new Feature(name, count);
        }
    }
        
}
