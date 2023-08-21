
package com.example.civiladvocacy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class GeocodingSummary {

    @SerializedName("queryString")
    @Expose
    private String queryString;
    @SerializedName("featureId")
    @Expose
    private FeatureId featureId;
    @SerializedName("featureType")
    @Expose
    private String featureType;
    @SerializedName("addressUnderstood")
    @Expose
    private Boolean addressUnderstood;

    /**
     * No args constructor for use in serialization
     * 
     */
    public GeocodingSummary() {
    }

    /**
     * 
     * @param featureType
     * @param addressUnderstood
     * @param queryString
     * @param featureId
     */
    public GeocodingSummary(String queryString, FeatureId featureId, String featureType, Boolean addressUnderstood) {
        super();
        this.queryString = queryString;
        this.featureId = featureId;
        this.featureType = featureType;
        this.addressUnderstood = addressUnderstood;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public FeatureId getFeatureId() {
        return featureId;
    }

    public void setFeatureId(FeatureId featureId) {
        this.featureId = featureId;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }


    public Boolean getAddressUnderstood() {
        return addressUnderstood;
    }

    public void setAddressUnderstood(Boolean addressUnderstood) {
        this.addressUnderstood = addressUnderstood;
    }

}
