package com.ethan.myclub.club.operation.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ethan.myclub.R;
import com.ethan.myclub.activity.create.view.ActivityCreateActivity;
import com.ethan.myclub.club.activitylist.view.ClubActivityListActivity;
import com.ethan.myclub.club.detail.view.ClubInfoActivity;
import com.ethan.myclub.club.member.view.ClubMemberListActivity;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.model.Title;
import com.ethan.myclub.club.notification.view.ClubNotificationCreateActivity;
import com.ethan.myclub.club.operation.DeleteClub;
import com.ethan.myclub.club.operation.QuitClub;
import com.ethan.myclub.club.operation.ScheduleAnalysis;
import com.ethan.myclub.club.operation.adapter.GridViewAdapter;
import com.ethan.myclub.club.operation.adapter.ViewPagerAdapter;
import com.ethan.myclub.club.operation.model.Operation;
import com.ethan.myclub.club.operation.view.ClubOperationActivity;
import com.ethan.myclub.databinding.ActivityClubOperationBinding;
import com.ethan.myclub.message.view.MessageListActivity;
import com.ethan.myclub.util.Utils;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ClubOperationViewModel {

    private ClubOperationActivity mActivity;
    private ActivityClubOperationBinding mBinding;

    public MyClub mClub;
    public ObservableField<String> mClubNameAndTitle = new ObservableField<>();

    public ClubOperationViewModel(ClubOperationActivity activity, ActivityClubOperationBinding binding, MyClub club) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mClub = club;
        String str = club.clubName + "  " + club.getMyTitle();
        mClubNameAndTitle.set(str);

        GridView gridView;
        List<Operation> operations;
        List<View> views = new ArrayList<>();

        gridView = (GridView) View.inflate(mActivity, R.layout.item_club_operation_pager, null);
        operations = new ArrayList<>();
        operations.add(new Operation(ClubInfoActivity.class, "社团简介", R.drawable.ic_club_op_clubinfo));
        operations.add(new Operation(ClubMemberListActivity.class, "成员列表", R.drawable.ic_club_op_member));
        operations.add(new Operation(ClubActivityListActivity.class, "活动列表", R.drawable.ic_club_op_activity));
        //operations.add(new Operation(null, "社团账单", R.drawable.ic_club_op_budget));
        operations.add(new Operation(MessageListActivity.class, "社团通知", R.drawable.ic_club_op_notification));
        gridView.setAdapter(new GridViewAdapter(mActivity, operations, mClub));
        views.add(gridView);


        gridView = (GridView) View.inflate(mActivity, R.layout.item_club_operation_pager, null);
        operations = new ArrayList<>();
        if (mClub.checkPermission(5))
            operations.add(new Operation(null, "招新管理", R.drawable.ic_club_op_freshmen));
        if (mClub.checkPermission(3))
            operations.add(new Operation(ScheduleAnalysis.class, "空课表", R.drawable.ic_club_op_schedule));
        if (mClub.isCreator)
            operations.add(new Operation(DeleteClub.class, "解散社团", R.drawable.ic_club_op_quit));
        else
            operations.add(new Operation(QuitClub.class, "退出社团", R.drawable.ic_club_op_quit));

        gridView.setAdapter(new GridViewAdapter(mActivity, operations, mClub));
        views.add(gridView);

        mBinding.viewPager.setAdapter(new ViewPagerAdapter(views));

        mBinding.indicator.setViewPager(mBinding.viewPager);

    }
}