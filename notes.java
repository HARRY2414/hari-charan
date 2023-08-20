package com.christopherhield.AndroidNotes;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.Date;
import java.util.jar.JarException;

import androidx.annotation.NonNull;

public class notes implements Comparable<notes>, Serializable{
    private String title,desc;
    private long date;

    public notes(String title, String desc, long savedate){
        this.title=title;
        this.desc=desc;
        this.date=savedate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return desc;
    }

    public void setDes(String desc) {
        this.desc = desc;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Notes{" +
                "title='" + title + '\'' +
                "des='" + desc + '\'' +
                "date"+ date + '}';
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("title", title);
        obj.put("desc", desc);
        obj.put("date", date);
        return obj;
    }

    public static notes createFromJSON(JSONObject jsonObject) throws JSONException {
        String title = jsonObject.getString("title");
        String des = jsonObject.getString("desc");
        long date = jsonObject.getLong("date");
        return new notes(title, des, date);
    }

    @Override
    public int compareTo(notes note) {


        long diff = date - note.date;
        if (diff > 1){
            return -1;
        } else if(diff < 0){
            return 1;
        }
        else return 0;
    }

}