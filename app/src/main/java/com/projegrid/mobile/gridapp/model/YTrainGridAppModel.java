package com.projegrid.mobile.gridapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projegrid.mobile.gridapp.parser.GridAppParser;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

/**
 * Created by yuki on 8/15/16.
 */
public class YTrainGridAppModel extends GridAppModel {
    private String departureStation;
    @JsonIgnore
    private DateTime departureTime;
    private String departureTimeStr;
    private String arrivalStation;
    @JsonIgnore
    private DateTime arrivalTime;
    private String arrivalTimeStr;

    public YTrainGridAppModel(){
        setType("Y_TRAIN");
    }

    public DateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(DateTime arrivalTime) {

        this.arrivalTime = arrivalTime;
        this.arrivalTimeStr = arrivalTime.toString();
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public DateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(DateTime departureTime) {

        this.departureTime = departureTime;
        this.departureTimeStr = departureTime.toString();
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
}
