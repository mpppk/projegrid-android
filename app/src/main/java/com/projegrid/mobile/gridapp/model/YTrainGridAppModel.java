package com.projegrid.mobile.gridapp.model;

/**
 * Created by yuki on 8/15/16.
 */
public class YTrainGridAppModel implements GridAppModel {
    private String departureStation;
    private String departureTimeStr;
    private String arrivalStation;
    private String arrivalTimeStr;

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalTimeStr() {
        return arrivalTimeStr;
    }

    public String getDepartureTimeStr() {
        return departureTimeStr;
    }

    public void setDepartureTimeStr(String departureTimeStr) {
        this.departureTimeStr = departureTimeStr;
    }

    public void setArrivalTimeStr(String arrivalTimeStr) {
        this.arrivalTimeStr = arrivalTimeStr;
    }

    @Override
    public String getType() {
        return "Y_TRAIN";
    }
}
