package com.ethan.myclub.message.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ethan.myclub.R;
import com.ethan.myclub.message.model.Message;
import com.ethan.myclub.util.ImageUtils;

import java.util.List;

/**
 * Created by ethan on 2017/4/12.
 */

public class UnreadMemberAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {


    public UnreadMemberAdapter(int layoutResId, List<Message> data) {
        super(R.layout.item_club_member, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Message item) {
        String name = item.receiverName;
        if (TextUtils.isEmpty(name))
            name = "无名氏";
        helper.setText(R.id.tv_username, name);
        helper.setText(R.id.tv_title_name, "");
        helper.setText(R.id.tv_nickname, "昵称 " + item.receiverNickname);

        ImageUtils.loadImageUrl((ImageView) helper.getView(R.id.iv_avatar), item.image);

    }
}
