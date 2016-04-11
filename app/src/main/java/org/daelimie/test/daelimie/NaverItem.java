package org.daelimie.test.daelimie;

/**
 * Created by YS on 2016-04-10.
 */
public class NaverItem {
    private String title;
    private String address;
    private double mapx;
    private double mapy;

    public NaverItem(String title, String address, double mapx, double mapy) {
        this.title = title;
        this.address = address;
        this.mapx = mapx;
        this.mapy = mapy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getMapx() {
        return mapx;
    }

    public void setMapx(double mapx) {
        this.mapx = mapx;
    }

    public double getMapy() {
        return mapy;
    }

    public void setMapy(double mapy) {
        this.mapy = mapy;
    }
}