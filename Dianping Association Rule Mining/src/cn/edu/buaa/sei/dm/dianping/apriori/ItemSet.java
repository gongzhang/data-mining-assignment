/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.apriori;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author gong
 */
public class ItemSet implements Iterable<Item> {
    
    private final List<Item> set;
    
    public ItemSet() {
        set = new ArrayList<Item>();
    }
    
    public ItemSet copy() {
        ItemSet rst = new ItemSet();
        for (Item i : set) {
            rst.addItem(i);
        }
        return rst;
    }
    
    public ItemSet(Item... items) {
        this();
        for (Item item : items) {
            addItem(item);
        }
    }
    
    public void addItem(Item item) {
        if (!set.contains(item)) set.add(item);
    }

    @Override
    public Iterator<Item> iterator() {
        return set.iterator();
    }
    
    public int size() {
        return set.size();
    }
    
    public Item get(int index) {
        return set.get(index);
    }
    
    public boolean contains(Item i) {
        return set.contains(i);
    }
    
    public ItemSet join(ItemSet set) {
        ItemSet rst = new ItemSet();
        for (Item item : this.set) {
            rst.addItem(item);
        }
        for (Item item : set.set) {
            rst.addItem(item);
        }
        return rst;
    }

    @Override
    public int hashCode() {
        if (size() == 0) return 0;
        else return size() + set.get(0).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ItemSet) {
            ItemSet itemSet = (ItemSet) o;
            if (itemSet.size() == this.size()) {
                for (int i = 0; i < this.size(); i++) {
                    if (!itemSet.set.contains(this.set.get(i)))
                        return false;
                }
                return true;
            } else return false;
        } else return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");
        for (Item item : set) {
            sb.append(item);
            sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    
}
