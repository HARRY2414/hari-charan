
package com.example.civiladvocacy.models;

import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Official {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private List<Address> address = null;
    @SerializedName("party")
    @Expose
    private String party;
    @SerializedName("phones")
    @Expose
    private List<String> phones = null;
    @SerializedName("urls")
    @Expose
    private List<String> urls = null;
    @SerializedName("channels")
    @Expose
    private List<Channel> channels = null;
    @SerializedName("geocodingSummaries")
    @Expose
    private List<GeocodingSummary> geocodingSummaries = null;
    @SerializedName("photoUrl")
    @Expose
    private String photoUrl;
    @SerializedName("emails")
    @Expose
    private List<String> emails = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Official() {
    }

    /**
     * 
     * @param emails
     * @param photoUrl
     * @param urls
     * @param address
     * @param channels
     * @param name
     * @param phones
     * @param geocodingSummaries
     * @param party
     */
    public Official(String name, List<Address> address, String party, List<String> phones, List<String> urls, List<Channel> channels, List<GeocodingSummary> geocodingSummaries, String photoUrl, List<String> emails) {
        super();
        this.name = name;
        this.address = address;
        this.party = party;
        this.phones = phones;
        this.urls = urls;
        this.channels = channels;
        this.geocodingSummaries = geocodingSummaries;
        this.photoUrl = photoUrl;
        this.emails = emails;
    }

    public Official(String name, String party, List<String> phones, List<String> urls, List<Channel> channels, List<GeocodingSummary> geocodingSummaries, String photoUrl) {
        super();
        this.name = name;
        this.party = party;
        this.phones = phones;
        this.urls = urls;
        this.channels = channels;
        this.geocodingSummaries = geocodingSummaries;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<GeocodingSummary> getGeocodingSummaries() {
        return geocodingSummaries;
    }

    public void setGeocodingSummaries(List<GeocodingSummary> geocodingSummaries) {
        this.geocodingSummaries = geocodingSummaries;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Official)) return false;
        Official official = (Official) o;
        return Objects.equals(getName(), official.getName()) && Objects.equals(getAddress(), official.getAddress()) && Objects.equals(getParty(), official.getParty()) && Objects.equals(getPhones(), official.getPhones()) && Objects.equals(getUrls(), official.getUrls()) && Objects.equals(getChannels(), official.getChannels()) && Objects.equals(getGeocodingSummaries(), official.getGeocodingSummaries()) && Objects.equals(getPhotoUrl(), official.getPhotoUrl()) && Objects.equals(getEmails(), official.getEmails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAddress(), getParty(), getPhones(), getUrls(), getChannels(), getGeocodingSummaries(), getPhotoUrl(), getEmails());
    }
}
