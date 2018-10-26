package com.liferay.mobile.screens.rating.interactor;

import com.liferay.mobile.screens.base.interactor.event.CacheEvent;
import org.json.JSONObject;

public class RatingEvent extends CacheEvent {

    private double score;
    private long classPK;
    private String className;
    private int ratingGroupCounts;

    public RatingEvent() {
        super();
    }

    public RatingEvent(JSONObject jsonObject) {
        super(jsonObject);
    }

    public RatingEvent(long classPK, String className, int ratingGroupCounts, JSONObject jsonObject) {
        super(jsonObject);
        this.classPK = classPK;
        this.className = className;
        this.ratingGroupCounts = ratingGroupCounts;
    }

    public RatingEvent(long classPK, String className, int ratingGroupCounts, double score) {
        this(classPK, className, ratingGroupCounts, null);
        this.score = score;
    }

    public long getClassPK() {
        return classPK;
    }

    public String getClassName() {
        return className;
    }

    public int getRatingGroupCounts() {
        return ratingGroupCounts;
    }

    public double getScore() {
        return score;
    }
}