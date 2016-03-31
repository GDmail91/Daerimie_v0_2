package org.daelimie.test.daelimie;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by YS on 2016-03-31.
 */
public class DTOAlarmValues implements Serializable {
    private static final long serialVersionUID = 1209L;

    // 선택된 경로 정보
    private double departureLocateLat; // 출발지 위도
    private double departureLocateLng; // 출발지 경도
    private String departurePlaceId; // 출발지 지역 정보
    private String departureName; // 출발지 이름
    private double destinationLocateLat; //도착지 위도
    private double destinationLocateLng; //도착지 경도
    private String destinationPlaceId; //도착지 지역 정보
    private String destinationName; // 도착지 이름
    private int arrivalTimeHour; // 도착하고 싶은 시간
    private int arrivalTimeMinute; // 도착하고 싶은 분
    private int preAlram; // 출발전 미리알림 시간
    private int departureTimeHour; // 출발 예상 시간
    private int departureTimeMinute; // 출발 예산 분

    public DTOAlarmValues() {
        departureLocateLat = 0.0;
        departureLocateLng = 0.0;
        departurePlaceId = "";
        departureName = "";
        destinationLocateLat = 0.0;
        destinationLocateLng = 0.0;
        destinationPlaceId = "";
        destinationName = "";
        arrivalTimeHour = 0;
        arrivalTimeMinute = 0;
        departureTimeHour = 0;
        departureTimeMinute = 0;
        preAlram = 0;
    }

    public void setDeparture(LatLng departureLocate, String departurePlaceId, String departureName) {
        this.departureLocateLat = departureLocate.latitude;
        this.departureLocateLng = departureLocate.longitude;
        this.departurePlaceId = departurePlaceId;
        this.departureName = departureName;
    }

    public void setDestination(LatLng destinationLocate, String destinationPlaceId, String destinationName) {
        this.destinationLocateLat = destinationLocate.latitude;
        this.destinationLocateLng = destinationLocate.longitude;
        this.destinationPlaceId = destinationPlaceId;
        this.destinationName = destinationName;
    }

    public void setArrivalTime(int arrivalTimeHour, int arrivalTimeMinute) {
        this.arrivalTimeHour = arrivalTimeHour;
        this.arrivalTimeMinute = arrivalTimeMinute;
    }

    public void setDepartureTime(int departureTimeHour, int departureTimeMinute) {
        this.departureTimeHour = departureTimeHour;
        this.departureTimeMinute = departureTimeMinute;
    }

    public void setAlarmInfo(int preAlram) {
        this.preAlram = preAlram;
    }

    public LatLng getDepartureLocate() {
        return new LatLng(departureLocateLat, departureLocateLng);
    }

    public String getDeparturePlaceId() {
        return departurePlaceId;
    }

    public String getDepartureName() {
        return departureName;
    }

    public LatLng getDestinationLocate() {
        return new LatLng(destinationLocateLat, destinationLocateLng);
    }

    public String getDestinationPlaceId() {
        return destinationPlaceId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public int getArrivalTimeHour() {
        return arrivalTimeHour;
    }

    public int getArrivalTimeMinute() {
        return arrivalTimeMinute;
    }

    public int getPreAlram() {
        return preAlram;
    }

    public int getDepartureTimeHour() {
        return departureTimeHour;
    }

    public int getDepartureTimeMinute() {
        return departureTimeMinute;
    }

}
