package com.ethan.myclub.club.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ethan on 2017/4/5.
 */

public class Club extends BaseObservable implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;

    @Bindable
    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        if(!clubName.equals(this.clubName))
            mIsInfoEdited = true;
        this.clubName = clubName;
        notifyPropertyChanged(BR.clubName);
    }

    @Bindable
    public String getBriefIntroduce() {
        return briefIntroduce;
    }

    public void setBriefIntroduce(String briefIntroduce) {
        if(!briefIntroduce.equals(this.briefIntroduce))
            mIsInfoEdited = true;
        this.briefIntroduce = briefIntroduce;
        notifyPropertyChanged(BR.briefIntroduce);
    }

    @Bindable
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        if(!contact.equals(this.contact))
            mIsInfoEdited = true;
        this.contact = contact;
        notifyPropertyChanged(BR.contact);
    }

    public boolean mIsInfoEdited = false;


    @SerializedName("creator")
    @Expose
    public Integer creator;
    @SerializedName("club_name")
    @Expose
    public String clubName;
    @SerializedName("college")
    @Expose
    public Integer college;
    @SerializedName("brief_introduce")
    @Expose
    public String briefIntroduce;
    @SerializedName("contact")
    @Expose
    public String contact;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("max_population")
    @Expose
    public Integer maxPopulation;
    @SerializedName("current_population")
    @Expose
    public Integer currentPopulation;
    @SerializedName("badge")
    @Expose
    public String badge;
    @SerializedName("tag")
    @Expose
    public List<Tag> tag;

}
