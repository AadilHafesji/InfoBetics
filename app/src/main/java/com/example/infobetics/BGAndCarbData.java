package com.example.infobetics;

import android.widget.Spinner;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class BGAndCarbData {
    private Double glucoseAmount;
    private Integer carbohydrateAmount;
    private Integer insulinAmount;
    private String userID;
    private @ServerTimestamp Date currentDateAndTime;
    private Spinner insulinType;

    public BGAndCarbData() {
//        No Argument
    }

    public BGAndCarbData(Date currentDateAndTime, Spinner insulinType) {
        this.currentDateAndTime = currentDateAndTime;
        this.insulinType = insulinType;
    }

    public BGAndCarbData(String userID, Double glucoseAmount, Integer carbohydrateAmount, Integer insulinAmount) {
        this.userID = userID;
        this.glucoseAmount = glucoseAmount;
        this.carbohydrateAmount = carbohydrateAmount;
        this.insulinAmount = insulinAmount;
    }

    public String getUserID() {
        return userID;
    }

    public Double getGlucoseAmount() {
        return glucoseAmount;
    }

    public Integer getCarbohydrateAmount() {
        return carbohydrateAmount;
    }

    public Integer getInsulinAmount() {
        return insulinAmount;
    }

    public Date getCurrentDateAndTime() {
        return currentDateAndTime;
    }

    public Spinner getInsulinType() {
        return insulinType;
    }
}
