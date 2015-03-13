/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.apriori;

import cn.edu.buaa.sei.dm.dianping.preprocess.Utility;
import cn.edu.buaa.sei.dm.dianping.rawdata.ShopRawData;
import cn.edu.buaa.sei.dm.dianping.resolution.Location;
import cn.edu.buaa.sei.dm.dianping.resolution.ReadOnlyDatabase;
import java.util.*;

/**
 *
 * @author gong
 */
public class Apriori {

    public double min_support = Double.NaN;
    public double min_confidence = Double.NaN;
    public int max_item_set_size = -1;

    private final ReadOnlyDatabase database; // Shop database
    private final ReadOnlyDatabase.Filter filter;
    private final int transaction_count;

    public Apriori(ReadOnlyDatabase database, Map<Integer, Location> locMap, ReadOnlyDatabase.Filter filter) {
        this.database = database;
        this.filter = filter;
        Iterator<ShopRawData> it = database.iterator(filter);
        int cnt = 0;
        while (it.hasNext()) {
            it.next();
            cnt++;
        }
        transaction_count = cnt;
    }

    public int getTransactionCount() {
	return transaction_count;
    }

    public List<Rule> doMining(final List<ItemSet> candidates) {
        final List<List<ItemSet>> frequents = new ArrayList<List<ItemSet>>();

        // 候选集仍有数据时继续
        int k = 1;
        while (candidates.size() > 0 && k <= max_item_set_size) {
            System.out.println("C" + k + ": " + candidates.size());

            // 去掉支持度过小的，形成频繁集
            List<ItemSet> f = new ArrayList<ItemSet>();
            for (ItemSet itemSet : candidates) {
                if (support(itemSet) >= min_support) {
                    f.add(itemSet);
                }
            }
            frequents.add(f);

            System.out.println("F" + k + ": " + f.size());
            //System.out.println("\t" + f);

            // 从频繁集生成新的候选集
            candidates.clear();
            if (k == max_item_set_size) break;
            
            for (int i = 0; i < f.size(); i++) {
                for (int j = i + 1; j < f.size(); j++) {
                    ItemSet a = f.get(i);
                    ItemSet b = f.get(j);
                    ItemSet c = a.join(b);
                    if (c.size() == a.size() + 1 && // 只增加一个项
                        !candidates.contains(c)) { // 去重复
                        candidates.add(c);
                    }
                }
            }

            k++;
        }

        // 依次分析每一个频繁集
        ArrayList<Rule> results = new ArrayList<Rule>();
	Set<Integer> groupSet = new HashSet<Integer>();
        System.out.println("\nResults:");
        for (List<ItemSet> f : frequents) {
            for (ItemSet set : f) {
                // 迭代所有的子集，建立候选规则
                Iterator<ItemSet> it = subsets(set);
                final double support_set = support(set);
		FOR_EACH_SUBSET:
                while (it.hasNext()) {
		    // 对于每个子集：
                    ItemSet subset = it.next();

		    // 排除违背分组规则的
		    groupSet.clear();
		    for (Item item : subset) {
			Integer group = item.getGroup();
			if (group != 0 && !groupSet.contains(group)) {
			    for (Item item2 : set) {
				if (!subset.contains(item2) && item2.getGroup() == group) {
				    continue FOR_EACH_SUBSET;
				}
			    }
			    groupSet.add(group);
			}
		    }
                    
                    // 排除违背左右约束的
                    for (Item item : set) {
                        if (item.getConstraint() == Item.Constraint.LeftOnly) {
                            if (!subset.contains(item)) continue FOR_EACH_SUBSET;
                        } else if (item.getConstraint() == Item.Constraint.RightOnly) {
                            if (subset.contains(item)) continue FOR_EACH_SUBSET;
                        }
                    }

                    Rule rule = new Rule(set, subset);
                    rule.setConfidence(support_set / support(subset));
                    if (rule.getConfidence() >= min_confidence) {
                        results.add(rule);
                        System.out.println(rule);
                        System.out.println(String.format("c = %.2f%%\n", rule.getConfidence() * 100));
                    }
                }
            }
        }

        return results;
    }
    
    private final Map<ItemSet, Double> support_cache = new HashMap<ItemSet, Double>();
    private final int support_cache_max_item_set_size = 2;

    public double support(ItemSet set) {
        // cache
        if (set.size() <= support_cache_max_item_set_size &&
            support_cache.containsKey(set)) {
            return support_cache.get(set);
        }
        
        // compute
        Iterator<ShopRawData> it = database.iterator(filter);
        int cnt = 0;
        DATA_ITERATE:
        while (it.hasNext()) {
            ShopRawData data = it.next();
            for (Item item : set) {
                if (!item.includedInTranscation(data))
                    continue DATA_ITERATE;
            }
            cnt++;
        }
        final double support = cnt / (double) transaction_count;
        
        if (set.size() <= support_cache_max_item_set_size) {
            support_cache.put(set.copy(), support);
        }
                
        return support;
    }

    /**
     * 迭代非空、非全的子集。
     * @return
     */
    public Iterator<ItemSet> subsets(final ItemSet set) {
        if (set.size() > 31) throw new UnsupportedOperationException("不能求大于31个元素集合的子集");
        return new Iterator<ItemSet>() {
            int i = 1;
            @Override
            public boolean hasNext() {
                if (set.size() == 0) return false;
                else return i < (1 << (set.size())) - 1; // i还不到2^n
            }

            @Override
            public ItemSet next() {
                ItemSet rst = new ItemSet();
                for (int j = 0; j < set.size(); j++) {
                    if (((i >> j) & 0x1) != 0) {
                        rst.addItem(set.get(j));
                    }
                }
                i++;
                return rst;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

}
