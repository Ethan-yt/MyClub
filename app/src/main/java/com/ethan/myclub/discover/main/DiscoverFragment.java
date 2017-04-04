package com.ethan.myclub.discover.main;


import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.ethan.myclub.discover.club.ClubFragment;
import com.ethan.myclub.main.BaseActivity;
import com.ethan.myclub.main.BaseFragment;
import com.ethan.myclub.network.ApiHelper;
import com.ethan.myclub.util.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends BaseFragment {

    private ViewPager mViewPager;
    private TabViewPagerAdapter mAdapter;

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
        setCurrentTab(1);
        willBeDisplayed();
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
                                    final SearchView searchView = (SearchView) menu
                                            .findItem(R.id.action_search).getActionView();
                                    searchView.setQueryHint("搜索…");
                                    searchView.setSubmitButtonEnabled(true);
                                    searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                                            mBaseActivity, android.R.layout.simple_list_item_1,
                                            null,
                                            new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                                            new int[]{android.R.id.text1},
                                            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));

                                    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                                        @Override
                                        public boolean onSuggestionSelect(int position) {
                                            Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                                            String term = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                                            cursor.close();
                                            searchView.setQuery(term, false);
                                            return true;
                                        }

                                        @Override
                                        public boolean onSuggestionClick(int position) {
                                            return onSuggestionSelect(position);
                                        }
                                    });
                                    //当输入搜索字符的时候，请求搜索建议
                                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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
                                                                searchView.getSuggestionsAdapter().changeCursor(cursor);
                                                            }
                                                        }, new Consumer<Throwable>() {
                                                            @Override
                                                            public void accept(@NonNull Throwable throwable) throws Exception {
                                                                throwable.printStackTrace();
                                                            }
                                                        });


                                            } else {
                                                searchView.getSuggestionsAdapter().changeCursor(null);
                                            }
                                            return true;
                                        }

                                        //提交搜索结果
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            TabFragment fragment = (TabFragment) mAdapter.getItem(mViewPager.getCurrentItem());
                                            fragment.update(1, 10);
                                            if (query.length() >= 2) {
                                                searchView.getSuggestionsAdapter().changeCursor(null);
                                                return true;
                                            }
                                            return true;
                                        }
                                    });

                                }
                            })
                    .showNavIcon(R.drawable.ic_discover_location, new View.OnClickListener() {
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
