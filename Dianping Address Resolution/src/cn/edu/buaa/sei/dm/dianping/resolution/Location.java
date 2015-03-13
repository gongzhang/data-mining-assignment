/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.resolution;

import java.io.Serializable;

/**
 *
 * @author gong
 */
public class Location implements Serializable {
    public double lat, lng;
    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    public Location() {
        this(0.0, 0.0);
    }
}
