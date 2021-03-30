package com.fuprox.noqueue.adapters;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.utils.Dbhelper;
import com.fuprox.noqueue.utils.fav_details;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.fuprox.noqueue.adapters.branches_adapter.TAG;


public class favourite_adapter  extends ArrayAdapter<fav_details> {


    private Activity mActivity;
    private int mResource;
    private ArrayList<fav_details> fav_list;
    private ListView mListView;
    fav_details fav_details;
    private int lastPosition = -1;
    double my_lattitude,my_longitude;
    FusedLocationProviderClient mFusedLocationClient;
    double d_long=0;
    double d_lat = 0;
    LatLng my_latlong;


    private static class ViewHolder {
        TextView title,txtdistance,tvfav_id;
        ImageView popup;
        CardView cardView;
        LikeButton likeButton;

//        CheckBox likeButton;
    }

    public favourite_adapter(Activity activity, ListView listView, int resource, ArrayList<fav_details> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        mListView = listView;
        mResource = resource;
        fav_list = objects;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final favourite_adapter.ViewHolder holder;
        final String name = Objects.requireNonNull(getItem(position)).getBranchname();
        final String fav_id = Objects.requireNonNull(getItem(position)).getId();
        final String fav_branchid = Objects.requireNonNull(getItem(position)).getBranchid();
        final String fav_branchlong = Objects.requireNonNull(getItem(position)).getLongitude();
        final String fav_branchlat = Objects.requireNonNull(getItem(position)).getLatitude();

        fav_details=new fav_details(fav_id,name,fav_branchid,fav_branchlong,fav_branchlat);

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new favourite_adapter.ViewHolder();
            holder.title = convertView.findViewById(R.id.favbranch_title);
            holder.txtdistance=convertView.findViewById(R.id.branch_dist);
            holder.likeButton=convertView.findViewById(R.id.favourite_btn);
            holder.tvfav_id=convertView.findViewById(R.id.tvfav_id);


            convertView.setTag(holder);
        } else {
            holder = (favourite_adapter.ViewHolder) convertView.getTag();
        }

        String branchname = fav_details.getBranchname();
        String branchlong= fav_details.getLongitude();
        String branchlat = fav_details.getLatitude();

        try{
            d_long= Double.parseDouble(branchlong);
            d_lat= Double.parseDouble(branchlat);
        }
        catch (Exception e){
            Log.d(TAG, "getView: " + e);
        }
        final LatLng other_latlong=new LatLng(d_lat,d_long);

        holder.title.setText(branchname);

        holder.tvfav_id.setText(fav_branchid);

        holder.likeButton.setLiked(true);

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                new Dbhelper(mActivity).delete_fav(fav_id);
                Animation loadAnimation = AnimationUtils.loadAnimation(
                        mActivity, R.anim.slide_out);

                ValueAnimator fader = ObjectAnimator.ofFloat(mListView, "translationX", 1, 0);
                AnimatorSet animation = new AnimatorSet();
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

//                        Toast.makeText(mActivity, "Place Removed From Favourites!", Toast.LENGTH_SHORT).show();
//                        Snackbar.make(mActivity.findViewById(R.id.nav_view),"Place Removed From Favourites!",Snackbar.LENGTH_SHORT).show();
                        Snackbar snackbar = Snackbar.make(mListView, "Removed From Favourites!", Snackbar.LENGTH_LONG);
//                        snackbar.setAnchorView(mActivity.findViewById(R.id.nav_view));
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
                        snackbar.setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        snackbar.setActionTextColor(mActivity.getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.setTextColor(mActivity.getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fav_list.remove(position);
                        notifyDataSetChanged();
                        if (fav_list.isEmpty()){

                            BottomNavigationView bottomNavigationView=mActivity.findViewById(R.id.nav_view);
                            bottomNavigationView.setSelectedItemId(R.id.navigation_fav);
                        }
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                animation.play(fader);
                animation.setDuration(500);
                animation.start();
            }
        });
//        holder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isChecked){
//                    new Dbhelper(mActivity).delete_fav(fav_id);
//
//                    Animation loadAnimation = AnimationUtils.loadAnimation(
//                            mActivity, R.anim.slide_out);
//
//                    ValueAnimator fader = ObjectAnimator.ofFloat(mListView, "translationX", 1, 0);
//                    AnimatorSet animation = new AnimatorSet();
//                    animation.addListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//
////                        Toast.makeText(mActivity, "Place Removed From Favourites!", Toast.LENGTH_SHORT).show();
////                        Snackbar.make(mActivity.findViewById(R.id.nav_view),"Place Removed From Favourites!",Snackbar.LENGTH_SHORT).show();
//                            Snackbar snackbar = Snackbar.make(mListView, "Removed From Favourites!", Snackbar.LENGTH_LONG);
////                        snackbar.setAnchorView(mActivity.findViewById(R.id.nav_view));
//                            View snackBarView = snackbar.getView();
//                            snackBarView.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
//                            snackbar.setAction("Ok", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                }
//                            });
//                            snackbar.setActionTextColor(mActivity.getResources().getColor(R.color.colorPrimaryDark));
//                            snackbar.setTextColor(mActivity.getResources().getColor(R.color.colorPrimaryDark));
//                            snackbar.show();
//                        }
//                        @Override
//                        public void onAnimationRepeat(Animator animation) {
//                        }
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            fav_list.remove(position);
//                            notifyDataSetChanged();
//                            if (fav_list.isEmpty()){
//
//                                BottomNavigationView bottomNavigationView=mActivity.findViewById(R.id.nav_view);
//                                bottomNavigationView.setSelectedItemId(R.id.navigation_fav);
//                            }
//                        }
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//                        }
//                    });
//                    animation.play(fader);
//                    animation.setDuration(500);
//                    animation.start();
//                }
//            }
//        });


        if(isLocationEnabled()){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
//                                latTextView.setText(location.getLatitude()+"");
//                                lonTextView.setText(location.getLongitude()+"");
                                my_lattitude=location.getLatitude();
                                my_longitude=location.getLongitude();
                                my_latlong=new LatLng(my_lattitude,my_longitude);
                                if (d_long!=0){
                                    holder.txtdistance.setText("("+getDistance(my_latlong,other_latlong) +" from your location)");
                                }
                            }
                        }
                    }
            );
        }
        else {
            Toast.makeText(mActivity, "Turn on location", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings_activity.ACTION_LOCATION_SOURCE_SETTINGS);
//                mActivity.startActivity(intent);
        }


        Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ArrayList<fav_details> getFav_list() {
        return fav_list;
    }

//    public Note getNote() {
//        return note;
//    }

    private void hidePopUpMenu(favourite_adapter.ViewHolder holder) {
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
//        if (checkedItems.size() > 0) {
//            for (int i = 0; i < checkedItems.size(); i++) {
//                int key = checkedItems.keyAt(i);
//                if (checkedItems.get(key)) {
//                    holder.popup.setVisibility(View.INVISIBLE);
//                    }
//            }
//        } else {
//            holder.popup.setVisibility(View.VISIBLE);
//        }
    }


    private void requestNewLocationData(){

        @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            my_longitude=mLastLocation.getLongitude();
            my_lattitude=mLastLocation.getLatitude();

//            latTextView.setText(mLastLocation.getLatitude()+"");
//            lonTextView.setText(mLastLocation.getLongitude()+"");
        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private String getDistance(LatLng my_latlong, LatLng other_latlong){
        Location l1=new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2=new Location("Two");
        l2.setLatitude(other_latlong.latitude);
        l2.setLongitude(other_latlong.longitude);

        float distance=l1.distanceTo(l2);
        String dist=distance+" M";

        if(distance>1000.0f)
        {
            distance=distance/1000.0f;

            dist=(int)distance+" KM";
        }

        return dist;

    }

}
