package com.treemeasurer.measurer.guide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.treemeasurer.measurer.R;

public class CalibGuideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ViewPager2 viewPager2 = findViewById(R.id.dbh_guide_container);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle(),viewPager2));
    }

    public static class SectionsPagerAdapter extends FragmentStateAdapter {
        ViewPager2 viewPager2;
        public SectionsPagerAdapter(@NonNull FragmentManager fragmentManager,
                                    @NonNull Lifecycle lifecycle, ViewPager2 viewPager2) {
            super(fragmentManager, lifecycle);
            this.viewPager2 = viewPager2;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new GuideCalibFragment(position,viewPager2);
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }

}
