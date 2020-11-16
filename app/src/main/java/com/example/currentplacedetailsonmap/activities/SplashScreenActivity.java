package com.example.currentplacedetailsonmap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.MainActivity;
import com.example.currentplacedetailsonmap.R;
import com.example.currentplacedetailsonmap.utils.Dbhelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);

        if (new Dbhelper(this).getsettings()==0) {
            new Dbhelper(this).first_settings();
        }

        get_ipaddress(this);

        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }

    private void get_ipaddress(Activity activity) {
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("ip_");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
//                User user = dataSnapshot.getValue(User.class);
                Log.d("TAG", "Value is: " + value);
                if (new Dbhelper(activity).get_ipaddress().equals("122")||
                        !new Dbhelper(activity).get_ipaddress().equals(value)||
                        new Dbhelper(activity).get_ipaddress()!=null){
                    new Dbhelper(activity).set_ipaddress(value);
                }
//                Toast.makeText(activity, ""+value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }
}