package com.ethan.myclub.club.operation.viewmodel;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ethan.myclub.R;
import com.ethan.myclub.club.info.view.ClubInfoActivity;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.my.model.Title;
import com.ethan.myclub.club.operation.adapter.GridViewAdapter;
import com.ethan.myclub.club.operation.adapter.ViewPagerAdapter;
import com.ethan.myclub.club.operation.model.Operation;
import com.ethan.myclub.club.operation.view.ClubOperationActivity;
import com.ethan.myclub.databinding.ActivityClubOperationBinding;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ClubOperationViewModel {

    private ClubOperationActivity mActivity;
    private ActivityClubOperationBinding mBinding;

    public MyClub mClub;

    public ClubOperationViewModel(ClubOperationActivity activity, ActivityClubOperationBinding binding, MyClub club) {
        mActivity = activity;
        mBinding = binding;
        mBinding.setViewModel(this);
        mClub = club;

        GridView gridView;
        List<Operation> operations;
        List<View> views = new ArrayList<>();

        gridView = (GridView) View.inflate(mActivity, R.layout.item_club_operation_pager, null);
        operations = new ArrayList<>();
        operations.add(new Operation(ClubInfoActivity.class, "社团简介", R.drawable.ic_club_op_clubinfo));
        operations.add(new Operation(null, "成员列表", R.drawable.ic_club_op_member));
        operations.add(new Operation(null, "活动列表", R.drawable.ic_club_op_activity));
        operations.add(new Operation(null, "社团账单", R.drawable.ic_club_op_budget));
        operations.add(new Operation(null, "社团通知", R.drawable.ic_club_op_notification));
        operations.add(new Operation(null, "退出社团", R.drawable.ic_club_op_quit));
        gridView.setAdapter(new GridViewAdapter(mActivity, operations, mClub.clubId, getMyPermission()));
        views.add(gridView);


        gridView = (GridView) View.inflate(mActivity, R.layout.item_club_operation_pager, null);
        operations = new ArrayList<>();
        operations.add(new Operation(null, "招新管理", R.drawable.ic_club_op_freshmen));
        operations.add(new Operation(null, "空课表", R.drawable.ic_club_op_schedule));
        gridView.setAdapter(new GridViewAdapter(mActivity, operations, mClub.clubId, getMyPermission()));
        views.add(gridView);

        mBinding.viewPager.setAdapter(new ViewPagerAdapter(views));

        mBinding.indicator.setViewPager(mBinding.viewPager);

    }

    @BindingAdapter({"clubOperationBadge"})
    public static void loadImage(final ImageView view, String imageUrl) {
        Object target;
        if (imageUrl == null) {
            target = R.drawable.img_default_avatar;
        } else {
            target = imageUrl + "?imageView2/0/w/300/h/300";
        }
        Glide.with(view.getContext())
                .load(target)
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(view.getContext()))
                .into(view);
    }

    private int getMyPermission() {
        if (mClub.isCreator)
            return 0xFFFFFFFF;
        for (Title title : mClub.titleTable) {
            if (title.id.equals(mClub.titleId))
                return title.permissionsPart1;
        }
        return 0;
    }
}