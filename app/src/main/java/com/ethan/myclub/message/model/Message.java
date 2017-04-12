package com.ethan.myclub.message.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ethan.myclub.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ethan on 2017/4/11.
 */

public class Message  implements MultiItemEntity, Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("content_id")
    @Expose
    public Integer contentId;
    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("content")
    @Expose

    public String content;
    @SerializedName("club")
    @Expose
    public String club;
    @SerializedName("club_id")
    @Expose
    public Integer clubId;
    @SerializedName("sender_nickname")
    @Expose
    public String senderNickname;
    @SerializedName("sender_name")
    @Expose
    public String senderName;
    @SerializedName("sender_id")
    @Expose
    public Integer senderId;
    @SerializedName("receiver_nickname")
    @Expose
    public String receiverNickname;
    @SerializedName("receiver_name")
    @Expose
    public String receiverName;
    @SerializedName("receiver_id")
    @Expose
    public Integer receiverId;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("is_checked")
    @Expose
    public Boolean isChecked;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("image")
    @Expose
    public String image;
    public String standardTime;
    public String senderStandardName;

    @Override
    public int getItemType() {
        try {
            return Integer.parseInt(type);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
}

