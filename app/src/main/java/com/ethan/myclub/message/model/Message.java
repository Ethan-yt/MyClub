package com.ethan.myclub.message.model;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ethan.myclub.BR;
import com.ethan.myclub.R;
import com.ethan.myclub.message.view.MessageAnalysisActivity;
import com.ethan.myclub.message.view.MessageDetailClubActivity;
import com.ethan.myclub.util.ImageUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ethan on 2017/4/11.
 */

public class Message implements MultiItemEntity, Serializable {

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
        return 0;
    }

    public String generateTitle() {

        switch (type) {
            case "0": //社团通知
                return "[通知]" + club;
            case "1": //申请加入社团
                return "[申请]" + club;
            case "2": //加入通过或拒绝
                switch (title) {
                    case "待审核":
                        return "[已提交]" + club;
                    case "接受":
                        return "[同意]" + club;
                    case "拒绝":
                        return "[拒绝]" + club;
                    default:
                        return "发生了内部错误：T2";
                }
            case "3":
                return "[通知]" + club;
            case "4":
                return "[通知]用户退出社团 " + club;
            case "5":
                return "[通知]社团已解散  " + club;
            case "7":
                return "[权限变更]" + club;
            case "8":
                return "[社团转让]" + club;
            default:
                return "未知消息";
        }

    }

    public String generateContent() {

        String stdSenderName = senderNickname + (TextUtils.isEmpty(senderName) ? "" : " (" + senderName + ")");

        switch (type) {
            case "0": //社团通知
                return title;
            case "1": //申请加入社团
                switch (title) {
                    case "待审核":
                        return stdSenderName + " 申请加入社团";
                    case "接受":
                        return "已经批准了 " + stdSenderName + " 的加入请求";
                    case "拒绝":
                        return "已经拒绝了 " + stdSenderName + " 的加入请求";
                    default:
                        return "发生了内部错误：T1";
                }
            case "2": //加入通过或拒绝
                switch (title) {
                    case "待审核":
                        return "您加入社团的申请已经提交";
                    case "接受":
                        return "管理员已经批准了你的申请";
                    case "拒绝":
                        return "管理员拒绝了你的申请";
                    default:
                        return "发生了内部错误：T2";
                }
            case "3":
                return "您已被社长请出社团";
            case "4":
                return stdSenderName + " 已经退出社团";
            case "5":
                return "社长已经解散社团";
            case "7":
                return "社长变更了你的权限：" + content;
            case "8":
                return "社长转让了社团：" + content;
            default:
                return "当前版本还不支持显示这个消息，升级您的app即可查看";
        }


    }
}

