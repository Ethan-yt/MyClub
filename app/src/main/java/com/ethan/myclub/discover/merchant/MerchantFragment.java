package com.ethan.myclub.discover.merchant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethan.myclub.club.info.view.ClubInfoActivity;
import com.ethan.myclub.discover.club.adapter.ClubAdapter;
import com.ethan.myclub.discover.club.model.ClubResult;
import com.ethan.myclub.discover.club.model.Hit;
import com.ethan.myclub.discover.main.TabFragment;
import com.ethan.myclub.discover.merchant.adapter.MerchantAdapter;
import com.ethan.myclub.discover.merchant.model.Merchant;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.network.ApiHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MerchantFragment extends TabFragment {

    private static final String TAG = "Discover Merchant";

    public MerchantFragment() {
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mAdapter = new MerchantAdapter(this, null);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final Merchant merchant = (Merchant) adapter.getItem(position);

                new AlertDialog.Builder(getActivity())
                        .setMessage("️\uD83D\uDCDE 联系电话：" + merchant.contact + "\n\n" +
                                "\uD83C\uDFE0 地址：" + merchant.location)
                        .setPositiveButton("拨打电话", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_DIAL);   //android.intent.action.DIAL
                                intent.setData(Uri.parse("tel:"+merchant.contact));
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void update(final int page, int items) {
        ApiHelper.getProxyWithoutToken((BaseActivity) getActivity())
                .searchMerchant(mKeyWord, page, items)
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Merchant>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //下拉刷新动画开始
                        if (page == 1)
                            mSwipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onNext(List<Merchant> merchantList) {
                        Log.i(TAG, "update: 获取Merchant完成");
                        //允许读取更多
                        mAdapter.setEnableLoadMore(true);
                        mCurrentPage++;
                        if (page == 1) {
                            if (merchantList == null || merchantList.size() == 0) {
                                mEmptyView.showEmptyView("还没有这个商家", "请换一个关键字试试哦");
                                mAdapter.setNewData(null);
                                mRecyclerView.setLayoutFrozen(true);
                                mAdapter.setEmptyView(mEmptyView);
                            } else {
                                mRecyclerView.setLayoutFrozen(false);
                                mAdapter.setNewData(merchantList);
                            }
                        } else {
                            //允许下拉刷新
                            mSwipeRefreshLayout.setEnabled(true);
                            mAdapter.loadMoreComplete();
                            mAdapter.addData(merchantList);
                        }
                        if (merchantList.size() < 10) {
                            mIsNoMore = true;
                            mAdapter.loadMoreEnd();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        //允许读取更多
                        mAdapter.setEnableLoadMore(true);
                        e.printStackTrace();
                        Log.i(TAG, "update: 获取ClubList失败");
                        if (page == 1) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mEmptyView.showErrorView(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    update(1, 10);
                                }
                            });
                            mAdapter.setNewData(null);
                            mRecyclerView.setLayoutFrozen(true);
                            mAdapter.setEmptyView(mEmptyView);
                        } else {
                            //允许下拉刷新
                            mSwipeRefreshLayout.setEnabled(true);
                            mAdapter.loadMoreFail();
                        }


                    }

                    @Override
                    public void onComplete() {
                        if (page == 1)
                            mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public Observable<List<String>> getSuggestionObservable(String query, BaseActivity activity) {
        return ApiHelper.getProxyWithoutToken(activity).getMerchantSuggestion(query);
    }
}
