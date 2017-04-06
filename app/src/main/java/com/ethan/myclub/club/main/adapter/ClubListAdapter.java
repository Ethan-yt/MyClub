package com.ethan.myclub.club.main.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ethan.myclub.R;
import com.ethan.myclub.club.main.model.MyClub;
import com.ethan.myclub.club.main.viewmodel.ClubItemViewModel;
import com.ethan.myclub.databinding.ItemClubBinding;

import java.util.List;

/**
 * Created by ethan on 2017/3/26.
 */

public class ClubListAdapter extends BaseQuickAdapter<MyClub, ClubListAdapter.ViewHolder> {

    private ClubItemViewModel mViewModel;

    public ClubListAdapter(List<MyClub> data) {
        super(R.layout.item_club, data);

        mViewModel = new ClubItemViewModel();
    }

    @Override
    protected void convert(ViewHolder helper, MyClub item) {
        ItemClubBinding binding = (ItemClubBinding) helper.getBinding();
        binding.setClub(item);
        binding.setViewModel(mViewModel);
        binding.executePendingBindings();
//        switch (helper.getLayoutPosition() %
//                2) {
//            case 0:
//                helper.setImageResource(R.id.iv, R.mipmap.m_img1);
//                break;
//            case 1:
//                helper.setImageResource(R.id.iv, R.mipmap.m_img2);
//                break;
//
//        }
    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(mLayoutInflater, layoutResId, parent, false);
        if (binding == null) {
            return super.getItemView(layoutResId, parent);
        }
        View view = binding.getRoot();
        view.setTag(R.id.BaseQuickAdapter_databinding_support, binding);
        return view;
    }

    public class ViewHolder extends BaseViewHolder {

        public ViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) getConvertView().getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }
}
