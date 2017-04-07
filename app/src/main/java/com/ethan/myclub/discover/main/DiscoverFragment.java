package com.ethan.myclub.discover.main;


import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.myclub.R;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.util.Utils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends BaseFragment {

    private ViewPager mViewPager;
    private TabViewPagerAdapter mAdapter;
    private SearchView mSearchView;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = onCreateView(inflater, R.layout.fragment_discover, container);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mAdapter = new TabViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount());
        willBeDisplayed();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mSearchView != null)
                    switch (position) {
                        case 0:
                            mSearchView.setQueryHint("搜索活动");
                            break;
                        case 1:
                            mSearchView.setQueryHint("搜索社团名称、简介或标签");
                            break;
                        case 2:
                            mSearchView.setQueryHint("搜索商家名称、赞助类型或赞助活动");
                            break;
                    }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    public void setCurrentTab(int index) {
        mViewPager.setCurrentItem(index);
    }


    @Override
    public void willBeDisplayed() {
        super.willBeDisplayed();
        if (mBaseActivity != null) {
            mBaseActivity.getToolbarWrapper()
                    .dismiss()
                    .setScrollable()
                    .withAnimate()
                    .setTitle("")
                    .setMenu(R.menu.toolbar_discover,
                            new Toolbar.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    return true;
                                }
                            },
                            new BaseActivity.OnFinishCreateMenu() {
                                @Override
                                public void onFinish(Menu menu) {
                                    mSearchView = (SearchView) menu
                                            .findItem(R.id.action_search).getActionView();

                                    mSearchView.setSubmitButtonEnabled(true);
                                    mSearchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                                            mBaseActivity, android.R.layout.simple_list_item_1,
                                            null,
                                            new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                                            new int[]{android.R.id.text1},
                                            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));

                                    mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                                        @Override
                                        public boolean onSuggestionSelect(int position) {
                                            Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                                            String term = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                                            cursor.close();
                                            mSearchView.setQuery(term, true);
                                            return true;
                                        }

                                        @Override
                                        public boolean onSuggestionClick(int position) {
                                            return onSuggestionSelect(position);
                                        }
                                    });
                                    MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
                                        //搜索前设定提示
                                        @Override
                                        public boolean onMenuItemActionExpand(MenuItem item) {
                                            switch (mViewPager.getCurrentItem()) {
                                                case 0:
                                                    mSearchView.setQueryHint("搜索活动");
                                                    break;
                                                case 1:
                                                    mSearchView.setQueryHint("搜索社团名称、简介或标签");
                                                    break;
                                                case 2:
                                                    mSearchView.setQueryHint("搜索商家名称、赞助类型或赞助活动");
                                                    break;
                                            }
                                            return true;
                                        }

                                        //取消时清空
                                        @Override
                                        public boolean onMenuItemActionCollapse(MenuItem item) {
                                            TabFragment fragment = (TabFragment) mAdapter.getItem(mViewPager.getCurrentItem());
                                            fragment.mKeyWord = "";
                                            fragment.update(1, 10);
                                            return true;
                                        }
                                    });


                                    //当输入搜索字符的时候，请求搜索建议
                                    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                                        @Override
                                        public boolean onQueryTextChange(String query) {
                                            TabFragment fragment = (TabFragment) mAdapter.getItem(mViewPager.getCurrentItem());
                                            fragment.mKeyWord = query;
                                            if (query.length() >= 2) {
                                                fragment.getSuggestionObservable(query, mBaseActivity)
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new Consumer<List<String>>() {
                                                            @Override
                                                            public void accept(@NonNull List<String> strs) throws Exception {
                                                                final String[] sAutocompleteColNames = new String[]{
                                                                        BaseColumns._ID,                         // necessary for adapter
                                                                        SearchManager.SUGGEST_COLUMN_TEXT_1      // the full search term
                                                                };

                                                                MatrixCursor cursor = new MatrixCursor(sAutocompleteColNames);
                                                                // parse your search terms into the MatrixCursor
                                                                for (int index = 0; index < strs.size(); index++) {
                                                                    String term = strs.get(index);
                                                                    Object[] row = new Object[]{index, term};
                                                                    cursor.addRow(row);
                                                                }
                                                                mSearchView.getSuggestionsAdapter().changeCursor(cursor);
                                                            }
                                                        }, new Consumer<Throwable>() {
                                                            @Override
                                                            public void accept(@NonNull Throwable throwable) throws Exception {
                                                                throwable.printStackTrace();
                                                            }
                                                        });


                                            } else {
                                                mSearchView.getSuggestionsAdapter().changeCursor(null);
                                            }
                                            return true;
                                        }

                                        //提交搜索结果
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            TabFragment fragment = (TabFragment) mAdapter.getItem(mViewPager.getCurrentItem());
                                            fragment.update(1, 10);
                                            if (query.length() >= 2) {
                                                mSearchView.getSuggestionsAdapter().changeCursor(null);
                                                return true;
                                            }
                                            return true;
                                        }
                                    });

                                }
                            })
                    .showNavIcon(R.drawable.ic_toolbar_discover_location, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show(R.layout.view_toolbar_discover);


            Utils.StatusBarLightMode(mBaseActivity, true);
        }

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public void refresh() {

    }
}
