package com.fuprox.noqueue.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fuprox.noqueue.R;

public class MapActivity extends AppCompatActivity {

    String branch_name,latitude,longitude;
    ImageView imgback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (getIntent().getExtras()!=null){
            branch_name = getIntent().getExtras().getString("branch_name");
            latitude = getIntent().getExtras().getString("latitude");
            longitude = getIntent().getExtras().getString("longitude");
            loadFragment(new Direction_activity(this,longitude,latitude,branch_name," ")
            );
            imgback = findViewById(R.id.imgback);
            imgback.setVisibility(View.GONE);
            imgback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        else {
            finish();
        }
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
//                    .addToBackStack(null)
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }

}