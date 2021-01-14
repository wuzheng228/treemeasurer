package com.treemeasurer.measurer.entity;

import java.io.Serializable;

public class HeightResult implements Serializable {
    private Integer height;
    private Double targetHeight;
    private Double distance;
    private Float depression;
    private Float elevation;

    private static HeightResult instance;

    public static HeightResult getInstance() {
        if (instance == null)
            instance = new HeightResult();
        return instance;
    }

    public static void setInstance(HeightResult instance) {
        HeightResult.instance = instance;
    }

    private HeightResult() {}

    public Double getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(Double targetHeight) {
        this.targetHeight = targetHeight;
    }

    private String getTargetHeght() {
        return null;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Float getDepression() {
        return depression;
    }

    public void setDepression(Float depression) {
        this.depression = depression;
    }

    public Float getElevation() {
        return elevation;
    }

    public void setElevation(Float elevation) {
        this.elevation = elevation;
    }
}
