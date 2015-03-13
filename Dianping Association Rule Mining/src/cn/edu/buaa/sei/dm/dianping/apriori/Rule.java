/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.apriori;

/**
 *
 * @author gong
 */
public class Rule {
    
    private final ItemSet universeSet;
    private final ItemSet conditionSet;
    private double confidence = Double.NaN;
    
    public Rule(ItemSet universeSet, ItemSet conditionSet) {
        this.universeSet = universeSet;
        this.conditionSet = conditionSet;
    }

    @Override
    public String toString() {
        ItemSet target = new ItemSet();
        for (Item item : universeSet) {
            if (!conditionSet.contains(item)) {
                target.addItem(item);
            }
        }
        return conditionSet + " -> " + target;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
}
