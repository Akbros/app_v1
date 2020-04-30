package com.oadevelopers.winbazi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import com.oadevelopers.winbazi.R;
import com.oadevelopers.winbazi.fragment.LiveFragment;
import com.oadevelopers.winbazi.fragment.PlayFragment;
import com.oadevelopers.winbazi.fragment.ResultFragment;

public class MatchActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private FloatingActionButton matchBt;

    private Bundle bundle;
    private String strId, strTitle, strURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        initBundel();
        initToolbar();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        LiveFragment liveFragment = new LiveFragment();
        bundle.putString("ID_KEY", strId);
        bundle.putString("TITLE_KEY", strTitle);
        bundle.putString("URL_KEY", strURL);
        liveFragment.setArguments(bundle);

        PlayFragment playFragment = new PlayFragment();
        bundle.putString("ID_KEY", strId);
        bundle.putString("TITLE_KEY", strTitle);
        bundle.putString("URL_KEY", strURL);
        playFragment.setArguments(bundle);

        ResultFragment resultFragment = new ResultFragment();
        bundle.putString("ID_KEY", strId);
        bundle.putString("TITLE_KEY", strTitle);
        bundle.putString("URL_KEY", strURL);
        resultFragment.setArguments(bundle);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(liveFragment);
        pagerAdapter.addFragments(playFragment);
        pagerAdapter.addFragments(resultFragment);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(0).setText("ONGOING");
            tabLayout.getTabAt(1).setText("UPCOMING");
            tabLayout.getTabAt(2).setText("RESULTS");
        }

        matchBt = (FloatingActionButton) findViewById(R.id.matchBt);
        matchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchActivity.this, MyMatchesActivity.class);
                intent.putExtra("ID_KEY", strId);
                intent.putExtra("TITLE_KEY", strTitle);
                intent.putExtra("URL_KEY", strURL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initBundel() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            strId = bundle.getString("ID_KEY");
            strTitle = bundle.getString("TITLE_KEY");
            strURL = bundle.getString("URL_KEY");
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (strTitle != null) {
            toolbar.setTitle((CharSequence) strTitle);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragments(Fragment fragments) {
            this.fragments.add(fragments);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }

}
