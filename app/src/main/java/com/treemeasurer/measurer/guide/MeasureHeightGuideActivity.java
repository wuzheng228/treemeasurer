package com.treemeasurer.measurer.guide;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.utils.ValidHelper;

public class MeasureHeightGuideActivity extends AppCompatActivity {
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
            return new PageFragment(viewPager2, position);
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

     public static class PageFragment extends Fragment {
        private ViewPager2 viewPager2;
        private EditText editTextHeight;
        private TextView textUnit;
        private LinearLayout editLayout;
        private Button btnPre;
        private Button btnNext;
        private Button btnStart;
        private Button btnPrint;
        private ImageView imageViewGuide;
        private int position;
        private SharedPreferences pref;

        public PageFragment(ViewPager2 viewPager2, int position) {
            this.viewPager2 = viewPager2;
            this.position = position;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_guide, container, false);
            pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            editLayout = view.findViewById(R.id.edit_height_layout);
            editTextHeight = view.findViewById(R.id.edit_text_height);
            textUnit = view.findViewById(R.id.text_unit);
            btnPre = view.findViewById(R.id.button_pre);
            btnNext = view.findViewById(R.id.button_next);
            btnStart = view.findViewById(R.id.button_start);
            imageViewGuide = view.findViewById(R.id.img_view_guide);
            btnPre.setOnClickListener(v -> {
                viewPager2.setCurrentItem(position - 1);
            });
            btnStart.setOnClickListener(v->{
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            });
            switch (position) {
                case 0:
                    stepOne();
                    break;
                case 1:
                    stepTwo();
                    break;
                case 2:
                    stepTree();
                    break;
            }
            return view;
        }


        private void stepTree() {
            imageViewGuide.setImageResource(R.drawable.height_guide_3);
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(v->{
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("first_use_height", false);
                edit.apply();
                getActivity().finish();
            });
        }

        private void stepTwo() {
            imageViewGuide.setImageResource(R.drawable.height_guide_2);
            btnNext.setVisibility(View.VISIBLE);
            btnPre.setVisibility(View.VISIBLE);
            btnNext.setOnClickListener(v->{
                viewPager2.setCurrentItem(position+1);
            });
        }

        private void stepOne() {
            imageViewGuide.setImageResource(R.drawable.img_guide_1);
            editLayout.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setOnClickListener(v->{
                String value = editTextHeight.getText().toString();
                if (ValidHelper.isEmptyAndNotDigitOnly(getContext(),value)) return;
                if (ValidHelper.isDigitOutRange(getContext(), value,80, 200,"高度范围为"))
                    return;
                pref.edit().putString("height", value).apply();
                viewPager2.setCurrentItem(position + 1);
            });
        }
    }
}
