package org.daelimie.test.daelimie;

/**
 * Created by YS on 2016-04-10.
 */
public class DTOSearchItem {
    private String title;
    private String address;
    private double mapx;
    private double mapy;
    private String placeId;

    public DTOSearchItem(String title, String address, double mapx, double mapy, String placeId) {
        this.title = title;
        this.address = address;
        this.mapx = mapx;
        this.mapy = mapy;
        this.placeId = placeId;
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}