package com.fuprox.noqueue.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.model.DirectionsJSONParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Direction_activity extends Fragment implements OnMapReadyCallback {
    public static String TAG = "";
    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    TextView tvDistanceDuration;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    LatLng origin,destination;
    double qlat,qlong;

    ImageButton walking_img,imgbus,imgdriving;
    ImageView back_btn;
    TextView tv_branchname,tv_distance,tv_duration,tv_mylocation,tv_mode;
    CardView cardmode,carddetail;
    private static final int DEFAULT_ZOOM = 12;
    String destination_name;
    Activity activity;
    String MODE = "walking";
    String str_view;
    public Direction_activity(Activity activity, String get_long, String get_lat, String destination_name, String str_view){
        this.qlat = Double.parseDouble(get_lat);
        this.qlong = Double.parseDouble(get_long);
        this.destination_name = destination_name;
        this.activity = activity;
        this.str_view = str_view;
    }
    private void hidelayout(String view){
        if (view.equals("minimised")){
            cardmode.setVisibility(View.GONE);
            carddetail.setVisibility(View.GONE);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        tv_branchname = view.findViewById(R.id.tv_branchname);
        tv_distance = view.findViewById(R.id.tv_distance);
        tv_duration = view.findViewById(R.id.tv_duration);
        tv_mylocation = view.findViewById(R.id.tv_mylocation);
        walking_img= view.findViewById(R.id.img_walk);
        imgbus = view.findViewById(R.id.img_drive);
        imgdriving = view.findViewById(R.id.img_bus);
        back_btn = view.findViewById(R.id.backbtn);
        tv_mode = view.findViewById(R.id.tv_mode);
        carddetail = view.findViewById(R.id.card_detail);
        cardmode = view.findViewById(R.id.card_mode);

        hidelayout(str_view);
        tv_branchname.setText(destination_name);

        imgbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = "transit";
                tv_mode.setText(MODE);
                updateLocationUI();
                getDeviceLocation();
            }
        });
        imgdriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = "driving";
                tv_mode.setText(MODE);
                updateLocationUI();
                getDeviceLocation();
            }
        });
        walking_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = "walking";
                tv_mode.setText(MODE);
                updateLocationUI();
                getDeviceLocation();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
//        tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);
        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        // Getting Map for the SupportMapFragment
        fm.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        destination = new LatLng(qlat,qlong);
        // Enable MyLocation Button in the Map
        return view;
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
//        String api_key = "AIzaSyBpUfCNHgjD-GF8UQ48_g6OSEN6hMTDX1I";
        String api_key = getResources().getString(R.string.api_key);
//        String mode = "walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&mode=" + MODE + "&key=" + api_key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }
    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(" downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.

                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) getView().findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });


        updateLocationUI();
        getDeviceLocation();


        // Setting onclick event listener for the map
//        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng point) {
//
//                // Already two locations
//                if (markerPoints.size() > 1) {
//                    markerPoints.clear();
//                    map.clear();
//                }
//
//                // Adding new item to the ArrayList
//                markerPoints.add(point);
//
//                // Creating MarkerOptions
//                MarkerOptions options = new MarkerOptions();
//
//                // Setting the position of the marker
//                options.position(point);
//
//                /**
//                 * For the start location, the color of marker is GREEN and
//                 * for the end location, the color of marker is RED.
//                 */
//                if (markerPoints.size() == 1) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                } else if (markerPoints.size() == 2) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }
//
//                // Add new marker to the Google Map Android API V2
//                map.addMarker(options);
//
//                // Checks, whether start and end locations are captured
//                if (markerPoints.size() >= 2) {
//                    LatLng origin = markerPoints.get(0);
//                    LatLng dest = markerPoints.get(1);
////                    LatLng origin = new LatLng(-1.176433,36.929415);
////                    LatLng dest = new LatLng(-1.176602,36.940249);
//                    // Getting URL to the Google Directions API
//                    String url = getDirectionsUrl(origin, dest);
//
//                    DownloadTask downloadTask = new DownloadTask();
//
//                    // Start downloading json data from Google Directions API
//                    downloadTask.execute(url);
//
//                    Log.d(TAG, "onMapClick: "+url);
//                }
//            }
//        });
    }


    private void setDirection(LatLng startpoint, LatLng destpoint){
         // Already two locations
        if (markerPoints.size() > 1) {
            markerPoints.clear();
            map.clear();
        }

        // Adding new item to the ArrayList
        markerPoints.add(startpoint);
        markerPoints.add(destpoint);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        for (int i= 0; i< markerPoints.size();i++){
            options.position(markerPoints.get(i));
            if (i==1){
                options.title(destination_name);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            }
            else {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            }
            map.addMarker(options);

        }

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
//        if (markerPoints.size() == 1) {
//            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        } else if (markerPoints.size() == 2) {
//            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        }

        // Add new marker to the Google Map Android API V2

        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);
//                    LatLng origin = new LatLng(-1.176433,36.929415);
//                    LatLng dest = new LatLng(-1.176602,36.940249);
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            Log.d(TAG, "onMapClick: "+url);
        }
    }
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Log.e(TAG, "onComplete: "+task.getResult() );
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                try {
                                    origin = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                                    setDirection(origin, destination);

                                    //                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    //                                    new LatLng(-1.176433,36.929415), 16));
                                    tv_distance.setText(getDistance(origin, destination));
//                                getAddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(qlat,qlong), DEFAULT_ZOOM));
                                }catch (Exception e){

                                }

//                                setDirection(origin, destination);
                            }
                            else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                map.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(new LatLng(qlat,qlong), DEFAULT_ZOOM));
                                map.getUiSettings().setMyLocationButtonEnabled(false);
                                markerPoints.add(new LatLng(qlat,qlong));
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(qlat,qlong), DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
//                locationResult.addOnCompleteListener(getContext(), new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//                            mLastKnownLocation = task.getResult();
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(mLastKnownLocation.getLatitude(),
//                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
//                            mMap.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String   add = obj.getAddressLine(0);
            String  currentAddress = obj.getSubAdminArea() + ","
                    + obj.getAdminArea();
            double   latitude = obj.getLatitude();
            double longitude = obj.getLongitude();
            String currentCity= obj.getSubAdminArea();
            String currentState= obj.getAdminArea();
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();


            System.out.println("obj.getCountryName()"+obj.getCountryName());
            System.out.println("obj.getCountryCode()"+obj.getCountryCode());
            System.out.println("obj.getAdminArea()"+obj.getAdminArea());
            System.out.println("obj.getPostalCode()"+obj.getPostalCode());
            System.out.println("obj.getSubAdminArea()"+obj.getSubAdminArea());
            System.out.println("obj.getLocality()"+obj.getLocality());
            System.out.println("obj.getSubThoroughfare()"+obj.getSubThoroughfare());

//            tv_mylocation.setText(obj.getLocality()+"");
            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "getAddress: "+e.toString() );
        }
    }
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
//                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
                View locat_butt=((View)getActivity().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                if (str_view.equals("minimised")){
                    locat_butt.setVisibility(View.GONE);
                }
                RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)locat_butt.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,0);
                layoutParams.setMargins(0,0,30,30);

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
//                Toast.makeText(getContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        Log.e(TAG, "onPostExecute: Distance: "+distance );
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(13);
                lineOptions.color(R.color.colorPrimaryDark);
            }


            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
//            tv_distance.setText(distance+"");
//            tv_duration.setText(duration+"");
        }
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
