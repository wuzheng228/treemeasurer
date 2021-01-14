package com.treemeasurer.measurer.tasks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.treemeasurer.measurer.BaseActivity;
import com.treemeasurer.measurer.R;
import com.treemeasurer.measurer.camera.CameraActivity;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 11;
    private static final String REQUIRED_PERMISSIONS = Manifest.permission.INTERNET;

    private final static String TAG = "MainActivity";

    static {
        boolean success = OpenCVLoader.initDebug();
        Log.d(TAG, "static initializer: OpenCVLoader is success:" + success);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this
                    , new String[]{REQUIRED_PERMISSIONS}, REQUEST_CODE_PERMISSIONS);
        }
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tableLayout = findViewById(R.id.tabs);
        TabLayoutMediator mediator = new TabLayoutMediator(tableLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                Log.d("MainActivity", "onConfigureTab: " + position);
                if (position == 0) {
                    tab.setIcon(R.drawable.ic_ruler);
                    tab.setText("测量");
                } else if (position == 1){
                    tab.setIcon(R.drawable.ic_level);
                    tab.setText("水平仪");
                }
            }
        });
        mediator.attach();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!allPermissionsGranted()) {
            Toast.makeText(this,
                    "用户权限授权失败.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean allPermissionsGranted() {
        if (ContextCompat.
                checkSelfPermission(this, REQUIRED_PERMISSIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static class DummyFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static DummyFragment newInstance(int sectionNumber) {
            DummyFragment fragment = new DummyFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);
            textView.setText("Fragment页面" + getArguments().getInt(ARG_SECTION_NUMBER, 0));
            return rootView;
        }
    }

    public static class SectionsPagerAdapter extends FragmentStateAdapter {
        public SectionsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0)
                return new MeasureFragment();
            else if (position == 1)
                return new GradienterFragment();
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
