/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.apriori;

import cn.edu.buaa.sei.dm.dianping.rawdata.ShopRawData;

/**
 *
 * @author gong
 */
public abstract class Item {
    
    public static enum Constraint {
        BothSides, LeftOnly, RightOnly
    }

    private final String name;
    private final int group;
    private final Constraint constraint;

    public Item(String name) {
	this(name, 0, Constraint.BothSides);
    }
    
    public Item(String name, int group) {
	this(name, group, Constraint.BothSides);
    }
    
    public Item(String name, Constraint constraint) {
	this(name, 0, constraint);
    }

    public Item(String name, int group, Constraint constraint) {
        this.name = name;
	this.group = group;
        this.constraint = constraint;
    }

    public String getName() {
        return name;
    }

    public int getGroup() {
	return group;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public abstract boolean includedInTranscation(ShopRawData data);

    @Override
    public String toString() {
        return String.format("[%s]", name);
    }

    public static class FeatureItem extends Item {
	public FeatureItem(String name) {
            super(name);
        }
        public FeatureItem(String name, int group) {
            super(name, group);
        }
        public FeatureItem(String name, Constraint constraint) {
            super(name, constraint);
        }
        public FeatureItem(String name, int group, Constraint constraint) {
            super(name, group, constraint);
        }
        @Override
        public boolean includedInTranscation(ShopRawData data) {
            for (ShopRawData.Feature f : data.features) {
                if (f.name.equals(getName()) && f.count >= 2) { // avg = 1.83
                    return true;
                }
            }
            return false;
        }
    }

    public static class TagItem extends Item {
	public TagItem(String name) {
            super(name);
        }
        public TagItem(String name, int group) {
            super(name, group);
        }
        public TagItem(String name, Constraint constraint) {
            super(name, constraint);
        }
        public TagItem(String name, int group, Constraint constraint) {
            super(name, group, constraint);
        }
        @Override
        public boolean includedInTranscation(ShopRawData data) {
            return data.tags.contains(getName());
        }
    }

    public ItemSet makeSet() {
	return new ItemSet(this);
    }

}
