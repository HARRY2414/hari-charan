
package com.example.civiladvocacy.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class OfficialResponse {

    @SerializedName("normalizedInput")
    @Expose
    private NormalizedInput normalizedInput;
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("offices")
    @Expose
    private List<Office> offices = null;
    @SerializedName("officials")
    @Expose
    private List<Official> officials = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public OfficialResponse() {
    }

    /**
     *
     * @param normalizedInput
     * @param kind
     * @param offices
     * @param officials
     */
    public OfficialResponse(NormalizedInput normalizedInput, String kind, List<Office> offices, List<Official> officials) {
        super();
        this.normalizedInput = normalizedInput;
        this.kind = kind;
        this.offices = offices;
        this.officials = officials;
    }

    public NormalizedInput getNormalizedInput() {
        return normalizedInput;
    }

    public void setNormalizedInput(NormalizedInput normalizedInput) {
        this.normalizedInput = normalizedInput;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<Office> getOffices() {
        return offices;
    }

    public void setOffices(List<Office> offices) {
        this.offices = offices;
    }

    public List<Official> getOfficials() {
        return officials;
    }

    public void setOfficials(List<Official> officials) {
        this.officials = officials;
    }

}
