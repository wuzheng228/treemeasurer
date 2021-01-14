package com.treemeasurer.measurer.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.data.DataSource;
import com.treemeasurer.measurer.data.DataSourceImpl;

public class GuideFragment extends Fragment {

    private ViewPager2 viewPager2;
    private DataSource dataSource;
    private EditText editTextHeight;
    private LinearLayout editLayout;
    private Button btnPre;
    private Button btnNext;
    private Button btnStart;
    private ImageView imageViewGuide;
    private int position;

    public GuideFragment(int position, ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
        this.position = position;
        dataSource = DataSourceImpl.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        editLayout = view.findViewById(R.id.edit_height_layout);
        editTextHeight = view.findViewById(R.id.edit_text_height);
        btnPre = view.findViewById(R.id.button_pre);
        btnNext = view.findViewById(R.id.button_next);
        btnStart = view.findViewById(R.id.button_start);
        imageViewGuide = view.findViewById(R.id.img_view_guide);
        if (position == 0) {
            imageViewGuide.setImageResource(R.drawable.img_guide_1);
            editLayout.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setOnClickListener(v->{
                String text = editTextHeight.getText().toString() ;
                if (!TextUtils.isEmpty(text) && text.length() < 4) {
                    int height = Integer.parseInt(text);
                    if (height >=80 && height <= 200) {
                        dataSource.saveSettingHeight(getContext(), text);
                        viewPager2.setCurrentItem(position + 1);
                        btnPre.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                Toast.makeText(getContext(),"高度范围为80~100", Toast.LENGTH_LONG).show();
            });
        }else if (position == 1) {
            imageViewGuide.setImageResource(R.drawable.img_guide_2);
            btnPre.setVisibility(View.VISIBLE);
            btnPre.setOnClickListener(v->{
                viewPager2.setCurrentItem(position - 1);
                btnPre.setVisibility(View.INVISIBLE);
            });
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setOnClickListener(v->{
                viewPager2.setCurrentItem(position + 1);
                btnNext.setVisibility(View.INVISIBLE);
            });
        } else if (position == 2) {
            imageViewGuide.setImageResource(R.drawable.img_guide_3);
            btnPre.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            btnStart.setOnClickListener(v-> {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("first_use_dbh", false);
                edit.apply();
                getActivity().setResult(Activity.RESULT_OK, new Intent());
                getActivity().finish();
            });
        }
        return view;
    }
}
