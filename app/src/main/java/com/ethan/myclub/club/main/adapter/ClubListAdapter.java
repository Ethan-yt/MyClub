package com.ethan.myclub.club.main.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ethan.myclub.R;
import com.ethan.myclub.club.main.model.Club;
import com.ethan.myclub.club.main.viewmodel.ClubItemViewModel;
import com.ethan.myclub.databinding.ItemClubBinding;

import java.util.List;

/**
 * Created by ethan on 2017/3/26.
 */

public class ClubListAdapter extends BaseQuickAdapter<Club, ClubListAdapter.ClubViewHolder> {

    private ClubItemViewModel mViewModel;

    public ClubListAdapter(int layoutResId, List<Club> data) {
        super(layoutResId, data);

        mViewModel = new ClubItemViewModel();
    }

    @Override
    protected void convert(ClubViewHolder helper, Club item) {
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

    public class ClubViewHolder extends BaseViewHolder {

        public ClubViewHolder(View view) {
            super(view);
        }

        public ViewDataBinding getBinding() {
            return (ViewDataBinding) getConvertView().getTag(R.id.BaseQuickAdapter_databinding_support);
        }
    }
}
