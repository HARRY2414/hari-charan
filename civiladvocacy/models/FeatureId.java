
package com.example.civiladvocacy.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class FeatureId {

    @SerializedName("cellId")
    @Expose
    private String cellId;
    @SerializedName("fprint")
    @Expose
    private String fprint;

    /**
     * No args constructor for use in serialization
     * 
     */
    public FeatureId() {
    }

    /**
     * 
     * @param fprint
     * @param cellId
     */
    public FeatureId(String cellId, String fprint) {
        super();
        this.cellId = cellId;
        this.fprint = fprint;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getFprint() {
        return fprint;
    }

    public void setFprint(String fprint) {
        this.fprint = fprint;
    }

}
