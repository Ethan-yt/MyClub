package com.ethan.myclub.club.my.model;

import com.ethan.myclub.club.model.MemberResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ethan on 2017/3/26.
 */

public class MyClub implements Serializable {

    @SerializedName("club_id")
    @Expose
    public Integer clubId;
    @SerializedName("club_name")
    @Expose
    public String clubName;
    @SerializedName("club_badge")
    @Expose
    public String clubBadge;
    @SerializedName("club_brief_introduce")
    @Expose
    public String clubBriefIntroduce;
    @SerializedName("is_creator")
    @Expose
    public Boolean isCreator;
    @SerializedName("title_table")
    @Expose
    public List<Title> titleTable = null;
    @SerializedName("title_id")
    @Expose
    public Integer titleId;

    public int getMyPermission() {
        if (titleTable == null)
            return 0;
        if (isCreator)
            return 0xFFFFFFFF;
        for (Title title : titleTable) {
            if (title.id.equals(titleId))
                return title.permissionsPart1;
        }
        return 0;
    }

    public String getMyTitle() {
        if (isCreator)
            return "社长";
        for (Title title : titleTable) {
            if (title.id.equals(titleId))
                return title.titleName;
        }
        return "社员";
    }

    public String getTitleNameFromMemberResult(MemberResult memberResult) {
        if (memberResult.isCreator)
            return "社长";
        for (Title title : titleTable) {
            if (title.id.equals(memberResult.title))
                return title.titleName;
        }
        return "社员";
    }

    /**
     * low
     *
     * @param bit 权限位
     *            low
     *            1: 修改基本信息
     *            2: 发布通知
     *            3: 查看导出课表
     *            4: 记账
     *            5: 审核新成员/踢普通成员
     *            6: 修改、发布、删除活动
     *            high
     */
    public boolean checkPermission(int bit) {
/*
    现在的权限是  01101 MASK是00010

    授权	    	01101|00010=01111

    检查权限		01101&00010=00000

    现在的权限是  01111 MASK是00010

    撤销权限		01111&~00010=01111&11101=01101
 */
        int mask = 1 << bit - 1;
        return (getMyPermission() & mask) != 0;
    }
}
