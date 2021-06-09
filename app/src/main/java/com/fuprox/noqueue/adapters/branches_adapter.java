package com.fuprox.noqueue.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.cardview.widget.CardView;

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

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.activities.MapActivity;
import com.fuprox.noqueue.activities.activity_booking;
import com.fuprox.noqueue.utils.Dbhelper;
import com.fuprox.noqueue.utils.branches_details;
import com.fuprox.noqueue.utils.fav_details;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;


public class branches_adapter extends ArrayAdapter<branches_details> {

    public static String TAG = "";

    ProgressDialog pDialog;
    private Activity mActivity;
    private int mResource;
    private ArrayList<branches_details> orders_list;
    private ListView mListView;
    branches_details branches_details;
    LatLng my_latlong, other_latlong;
    View tolocation_view;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    FusedLocationProviderClient mFusedLocationClient;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Location mLastKnownLocation;


    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private int lastPosition = -1;

    double my_lattitude, my_longitude;
    double d_long = 0;
    double d_lat = 0;

    Drawable drawable;

    private static class ViewHolder {
        TextView title, txtid, txtvdist,txtworkingtime, tvticket, tvdate;
        ImageView icon_view, imgdirection;
        //        CheckBox likeButton;
        CardView cardView;
        LikeButton likeButton;

    }

    public branches_adapter(Activity activity, ListView listView, int resource, ArrayList<branches_details> objects, Drawable drawable) {
        super(activity, resource, objects);
        mActivity = activity;
        mListView = listView;
        mResource = resource;
        orders_list = objects;
        this.drawable = drawable;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final branches_adapter.ViewHolder holder;
        final String name = Objects.requireNonNull(getItem(position)).getTitle();
        final String br_id = Objects.requireNonNull(getItem(position)).getId();
        final String br_open = Objects.requireNonNull(getItem(position)).getOpen_time();
        final String br_close = Objects.requireNonNull(getItem(position)).getClose_time();
        final String br_lat = Objects.requireNonNull(getItem(position)).getLattitude();
        final String br_long = Objects.requireNonNull(getItem(position)).getLongitude();
        final String ismedical = Objects.requireNonNull(getItem(position)).getIsmedical();
        final String icon_url = Objects.requireNonNull(getItem(position)).getIcon_url();

        branches_details = new branches_details(br_id, name, br_open, br_close, br_lat, br_long, ismedical, icon_url);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new branches_adapter.ViewHolder();
            holder.title = convertView.findViewById(R.id.branch_title);
            holder.txtid = convertView.findViewById(R.id.txtbranchid);
            holder.txtvdist = convertView.findViewById(R.id.branch_dist);
            holder.likeButton = convertView.findViewById(R.id.favourite_btn);
            holder.icon_view = convertView.findViewById(R.id.branch_icon);
            holder.imgdirection = convertView.findViewById(R.id.imgdirection);
            holder.txtworkingtime = convertView.findViewById(R.id.branch_workingtime);
            convertView.setTag(holder);
        } else {
            holder = (branches_adapter.ViewHolder) convertView.getTag();
        }
        String s_title = branches_details.getTitle();
        String s_id = branches_details.getId();
        String s_longitude = branches_details.getLongitude();
        String s_lattitude = branches_details.getLattitude();
        String sworkingtime = " ";
        if (branches_details.getClose_time().length()==0){
            sworkingtime = "Open " + branches_details.getOpen_time();

        }
        else {
            sworkingtime = "Open " + branches_details.getOpen_time() + " - " + branches_details.getClose_time();
        }

        try {
            d_long = Double.parseDouble(s_longitude);
            d_lat = Double.parseDouble(s_lattitude);
        } catch (Exception e) {
            Log.d(TAG, "getView: " + e);
        }

        final LatLng other_latlong = new LatLng(d_lat, d_long);
        boolean mLocationPermissionGranted = true;

        LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        } else {
//            get my location
            if (isLocationEnabled()) {
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
                                    my_lattitude = location.getLatitude();
                                    my_longitude = location.getLongitude();
                                    my_latlong = new LatLng(my_lattitude, my_longitude);
                                    if (d_long != 0) {
                                        holder.txtvdist.setText("(" + getDistance(my_latlong, other_latlong) + " from your location)");
                                    }
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(mActivity, "Turn on location", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings_activity.ACTION_LOCATION_SOURCE_SETTINGS);
//                mActivity.startActivity(intent);
            }

        }

        holder.title.setText(s_title);
        holder.txtid.setText(s_id);
        holder.txtworkingtime.setText(sworkingtime);
        holder.imgdirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(mActivity, MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("branch_name", s_title);
                bundle.putString("longitude", s_longitude);
                bundle.putString("latitude", s_lattitude);

                intent1.putExtras(bundle);
                mActivity.startActivity(intent1);
            }
        });
        if (new Dbhelper(mActivity).check_if_fav(s_id)) {
//            holder.likeButton.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
            holder.likeButton.setLiked(true);
//            holder.title.setText(s_title+"fav");
        } else {
//            holder.likeButton.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
            holder.likeButton.setLiked(false);
//            holder.title.setText(s_title+"not fav");
        }
        fav_details fav_details = new fav_details();
        fav_details.setBranchid(br_id);
        fav_details.setBranchname(name);
        fav_details.setLongitude(br_long);
        fav_details.setLatitude(br_lat);


//        holder.likeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    new Dbhelper(mActivity).insert_fav(fav_details);
//
//                }
//                else {
//                    new Dbhelper(mActivity).delete_fav(new Dbhelper(mActivity).get_favid(br_id));
//                }
//            }
//        });

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                new Dbhelper(mActivity).insert_fav(fav_details);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                new Dbhelper(mActivity).delete_fav(new Dbhelper(mActivity).get_favid(br_id));
            }
        });
        pDialog = new ProgressDialog(getContext());

        holder.icon_view.setImageDrawable(drawable);


//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Picasso.get().load(icon_url).into(new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    Log.i(TAG, "The image was obtained correctly");
//                    holder.icon_view.setImageBitmap(bitmap);
//                    pDialog.dismiss();
//
//                }
//
//                @Override
//                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                    pDialog.dismiss();
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                    Log.i(TAG, "Getting ready to get the image");
//                    pDialog.setMessage("Getting details...");
//                    pDialog.setIndeterminate(false);
//                    pDialog.setCancelable(true);
//                    pDialog.show();
//                    //Here you should place a loading gif in the ImageView
//                    //while image is being obtained.
//                }
//            });
//        }
//        else {
//            new DownloadImageTask(holder.icon_view).execute(icon_url);
//
//        }

        Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("getting details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            pDialog.dismiss();
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ArrayList<branches_details> getNoteList() {
        return orders_list;
    }

//    public Note getNote() {
//        return note;
//    }

    private void hidePopUpMenu(branches_adapter.ViewHolder holder) {
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

    private void requestNewLocationData() {

        @SuppressLint("RestrictedApi") LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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
