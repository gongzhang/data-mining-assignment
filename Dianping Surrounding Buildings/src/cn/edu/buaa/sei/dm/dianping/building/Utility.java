/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.sei.dm.dianping.building;

/**
 *
 * @author Alven
 */
public class Utility {

    public static double MAX_LAT = 40.185;
    public static double MIN_LAT = 39.7;
    public static double MAX_LNG = 116.725;
    public static double MIN_LNG = 116.09;

    public static double distanceToLat(double distance) {
        return distance / 108 * (MAX_LAT - MIN_LAT) / 500;
    }

    public static double distanceToLng(double distance) {
        return distance / 108 * (MAX_LNG - MIN_LNG) / 500;
    }
}
