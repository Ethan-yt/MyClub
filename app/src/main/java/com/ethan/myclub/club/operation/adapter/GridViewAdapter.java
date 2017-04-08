package com.ethan.myclub.club.operation.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethan.myclub.R;
import com.ethan.myclub.club.my.model.MyClub;
import com.ethan.myclub.club.operation.model.Operation;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ethan on 2017/4/5.
 */

public class GridViewAdapter extends BaseAdapter {

    private Activity mContext;
    private List<Operation> mLists;//数据源
    private MyClub mMyClub;


    public GridViewAdapter(Activity context, List<Operation> lists, MyClub myClub) {
        mContext = context;
        mLists = lists;
        mMyClub = myClub;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Operation getItem(int arg0) {
        return mLists.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_club_operation_grid, null);
            holder.tv_operation_name = (TextView) convertView.findViewById(R.id.tv_operation_name);
            holder.iv_operation_icon = (ImageView) convertView.findViewById(R.id.iv_operation_icon);
            holder.cardview = (CardView) convertView.findViewById(R.id.card_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_operation_name.setText(mLists.get(position).mName);
        holder.iv_operation_icon.setImageResource(mLists.get(position).mIconId);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> activity = mLists.get(position).mActivity;
                try {
                    Method method = activity.getMethod("start", Activity.class, MyClub.class);
                    method.invoke(null, mContext, mMyClub);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.e("", "onClick: " + position + "   " + arg0);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_operation_name;
        private ImageView iv_operation_icon;
        private CardView cardview;
    }
}