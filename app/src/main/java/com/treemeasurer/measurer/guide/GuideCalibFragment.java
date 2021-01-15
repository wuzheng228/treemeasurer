package com.treemeasurer.measurer.guide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.print.PrintHelper;
import androidx.viewpager2.widget.ViewPager2;

import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.data.DataSource;
import com.treemeasurer.measurer.data.DataSourceImpl;
import com.treemeasurer.measurer.utils.ValidHelper;

import org.w3c.dom.Text;

public class GuideCalibFragment extends Fragment {

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

    public GuideCalibFragment(int position, ViewPager2 viewPager2) {
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
        btnPrint = view.findViewById(R.id.button_print);
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
            case 3:
                stepFour();
                break;
            case 4:
                stepFive();
                break;
        }
        return view;
    }

    private void stepFive() {
        imageViewGuide.setImageResource(R.drawable.calib_guide_4);
        btnStart.setText("开始拍摄");
        btnStart.setVisibility(View.VISIBLE);
//        btnPre.setVisibility(View.VISIBLE);
    }

    private void stepFour() {
        textUnit.setText("mm");
        editLayout.setVisibility(View.VISIBLE);
        editTextHeight.setText(pref.getString("board_box_size","25"));
        imageViewGuide.setImageResource(R.drawable.calib_guide_3);
        btnPre.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setOnClickListener(v->{
            String value = editTextHeight.getText().toString();
            if (ValidHelper.isEmptyAndNotDigitOnly(getActivity(), value)) return;
            if (ValidHelper.isDigitOutRange(getContext(), value, 10, 35, "黑格宽度范围为"))
                return;
            viewPager2.setCurrentItem(position + 1);
        });
    }

    private void stepTree() {
        textUnit.setText("格");
        editTextHeight.setText(pref.getString("board_long","7"));
        imageViewGuide.setImageResource(R.drawable.calib_guide_2);
        btnNext.setVisibility(View.VISIBLE);
        btnPre.setVisibility(View.VISIBLE);
        editLayout.setVisibility(View.VISIBLE);
        btnNext.setOnClickListener(v->{
            String value = editTextHeight.getText().toString();
            if (ValidHelper.isEmptyAndNotDigitOnly(getContext(),value)) return;
            if (ValidHelper.isDigitOutRange(getContext(), value,5, 15,"标定板宽度范围为"))
                return;
            pref.edit().putString("board_long", value).apply();
            viewPager2.setCurrentItem(position + 1);
        });
        btnPre.setOnClickListener(v -> {
            viewPager2.setCurrentItem(position - 1);
        });
    }

    private void stepTwo() {
        textUnit.setText("格");
        editTextHeight.setText(pref.getString("board_short","5"));
        imageViewGuide.setImageResource(R.drawable.calib_guide_1);
        btnNext.setVisibility(View.VISIBLE);
        btnPre.setVisibility(View.VISIBLE);
        editLayout.setVisibility(View.VISIBLE);
        btnNext.setOnClickListener(v->{
            String value = editTextHeight.getText().toString();
            if (ValidHelper.isEmptyAndNotDigitOnly(getContext(),value)) return;
            if (ValidHelper.isDigitOutRange(getContext(), value,5, 15,"标定板高度范围为"))
                return;
            pref.edit().putString("board_short", value).apply();
            viewPager2.setCurrentItem(position + 1);
        });
    }

    private void stepOne() {
        imageViewGuide.setImageResource(R.drawable.calib_guide_0);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setOnClickListener(v->{
            viewPager2.setCurrentItem(position + 1);
        });
        btnPrint.setVisibility(View.VISIBLE);
        btnPrint.setOnClickListener((v)->{
            doPhotoPrint();
        });
    }

    private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(getActivity());
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.chess_board);
        photoPrinter.printBitmap("打印标定板图片", bitmap);
    }
}
