package com.fuprox.noqueue.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuprox.noqueue.activities.MapsActivityCurrentPlace;
import com.fuprox.noqueue.R;
import com.fuprox.noqueue.model.JSONParser;
import com.fuprox.noqueue.model.strings_;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class queue extends Fragment implements OnMapReadyCallback {
    private List<String> lastSearches;
    private MaterialSearchBar searchBar;


    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    public GoogleMap mMap;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-1.176433,36.929415);
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity_order_details state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private MapView mapView;

    static ProgressDialog pDialog;
    // products JSONArray
    static JSONArray products = null;
    static ArrayList<HashMap<String, String>> productsList;

    // Progress Dialog


    // Creating JSON Parser object
    final JSONParser jParser = new JSONParser();



    // url to get all products list

//    final String url_all_products = ip;

    // JSON Node names
    final String TAG_SUCCESS = "success";
    final String TAG_PRODUCTS = "branches";
    final String TAG_PID = "id";
    final String TAG_company = "company";
    final String Tag_latitude="latitude";
    final String Tag_longitude="longitude";
    final String Tag_branch_name="name";
    final String Tag_address="address";

    double longi=0, lat=0;
    String straddress,strcompany_name,strbranch_name,b_id;
    int list_size,list_id;

    double qlat,qlong;



    public queue(String get_long,String get_lat){
        this.qlat= Double.parseDouble(get_lat);
        this.qlong= Double.parseDouble(get_long);

        if (get_long.equals("0")){
            this.qlat=mDefaultLocation.latitude;
            this.qlong=mDefaultLocation.longitude;
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view=inflater.inflate(R.layout.activity_maps,container,false);




        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);

        }
            // Construct a GeoDataClient.
        // The entry points to the Places API.
        GeoDataClient mGeoDataClient = Places.getGeoDataClient(getContext(), null);

            // Construct a PlaceDetectionClient.
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);

            // Construct a FusedLocationProviderClient.
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

            // Build the map.
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

//            new LoadAllProducts_booking();
        productsList = new ArrayList<HashMap<String, String>>();





        return view;
        // Retrieve location and camera position from saved instance state.


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @return Boolean.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

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



        new LoadAllProducts().execute();
//        LoadAllProducts_booking.Status.values();
        // Prompt the user for permission.
//        getLocationPermission();
//
//        // Turn on the My Location layer and the related control on the map.
//        updateLocationUI();
//
//        // Get the current location of the device and set the position of the map.
//        getDeviceLocation();
//        MarkerOptions marker = new MarkerOptions().position(
//                new LatLng(-1.176433, 36.929415))
//                .title("").snippet("");
//        Marker marker1=mMap.addMarker(marker);
//        marker1.showInfoWindow();




//        new get_data();


        final ArrayList<Double> doubleslat=new ArrayList<>();
        final ArrayList <Double> doubleslong=new ArrayList<>();


        doubleslat.add(-1.176433);
        doubleslat.add(-1.176602);

        doubleslong.add(36.929415);
        doubleslong.add(36.940249);

        String[] array_string_latlong={
                "-1.176433,36.929415",//ku equity
                "-1.176602,36.940249"// unicity equity
        };

        ArrayList<String> arrayList_latlong= new ArrayList<String>(Arrays.asList(array_string_latlong));
//        get_data.execute();


//        for(int i = 0; i<productsList.size(); i++) {
//
//
//            // Storing each json item in variable
//
////            double latttitude= Double.parseDouble(arrayList_latlong.get(i).substring(0, arrayList_latlong.get(i).indexOf(",")));
////            double longittude= Double.parseDouble(arrayList_latlong.get(i).substring(arrayList_latlong.get(i).indexOf(",")+1, arrayList_latlong.get(i).length()));
//
//
//
//        }

//        marker onclick

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                System.out.println("marker position-------"+marker.getPosition());
//                marker.hideInfoWindow();

                String dblongi,dblat;
//                dblat=productsList.get(marker.)
                HashMap<String, String> map = (HashMap<String, String>) marker.getTag();

                //Getting Collection of values from HashMap

                Collection<String> values = null;
                ArrayList<String> listOfValues=null;
                String sname = null,sbranch=null,saddress=null;


//                if (!map.isEmpty()){
                    values = map.values();
                    listOfValues= new ArrayList<String>(values);


                    sname=listOfValues.get(3);
                    sbranch=listOfValues.get(0);
                    saddress=listOfValues.get(1);
                    b_id=listOfValues.get(4);


//                    bottom_sheet_fragment bottomSheetFragment = new bottom_sheet_fragment(b_id,sname,saddress,sbranch);
//                    bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
                    for (int i=0;i<listOfValues.size();i++){
                        Log.d(TAG, "onMarkerClick: "+listOfValues.get(i)+"  --"+i);
                    }
//                }
//                else {
//                    bottom_sheet_fragment bottomSheetFragment = new bottom_sheet_fragment("","","");
//                    bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());

//                }

                //Creating an ArrayList of values










                return false;
            }
        });
//        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

//                MarkerOptions marker = new MarkerOptions().position(
//                        new LatLng(point.latitude, point.longitude)).title("New Marker");
//                for(int i=0;i<doubleslat.size();i++) {
//                    if (point.latitude==doubleslat.get(i) && point.longitude==doubleslong.get(i)){
//                        Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
//                        List<Address> addresses = null;
//                        try {
//                            addresses = geo.getFromLocation(point.latitude, point.longitude, 1);
//                            System.out.println(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
////                        if (addresses.isEmpty()) {
////                            yourtextboxname.setText("Waiting for Location");
////                        }
////                        else {
////                            if (addresses.size() > 0) {
////                                yourtextboxname.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
////                            }
////                        }
//
//                        Toast.makeText(getContext(), ""+addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName(), Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        Toast.makeText(getContext(), "No information for this service", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                }
////                //                mMap.addMarker(marker);
//
////                -1.176398, 36.940231
//                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
//                try {
//                    List<Address> address=geocoder.getFromLocation(point.latitude, point.longitude, 1);
//                    Address obj = address.get(0);
//                    String add = obj.getAddressLine(0);
//                    System.out.println(point.latitude+"---"+ point.longitude+"-----name"+add);
//                } catch (IOException e) {
//                    Log.e(TAG, "onMapClick: ", e);
//                    e.printStackTrace();
//                }
//
//                System.out.println(point.latitude+"---"+ point.longitude+"-----name");
            }
        });
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(qlat,qlong), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();

                }
            }
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
//     */
//    private void showCurrentPlace() {
//        if (mMap == null) {
//            return;
//        }
//
//        if (mLocationPermissionGranted) {
//            // Get the likely places - that is, the businesses and other points of interest that
//            // are the best match for the device's current location.
//            @SuppressWarnings("MissingPermission") final
//            Task<PlaceLikelihoodBufferResponse> placeResult =
//                    mPlaceDetectionClient.getCurrentPlace(null);
//            placeResult.addOnCompleteListener
//                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//                        @Override
//                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                            if (task.isSuccessful() && task.getResult() != null) {
//                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//
//                                // Set the count, handling cases where less than 5 entries are returned.
//                                int count;
//                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
//                                    count = likelyPlaces.getCount();
//                                } else {
//                                    count = M_MAX_ENTRIES;
//                                }
//
//                                int i = 0;
//                                mLikelyPlaceNames = new String[count];
//                                mLikelyPlaceAddresses = new String[count];
//                                mLikelyPlaceAttributions = new String[count];
//                                mLikelyPlaceLatLngs = new LatLng[count];
//
//                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                                    // Build a list of likely places to show the user.
//                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
//                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
//                                            .getAddress();
//                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
//                                            .getAttributions();
//                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
//
//                                    i++;
//                                    if (i > (count - 1)) {
//                                        break;
//                                    }
//                                }
//
//                                // Release the place likelihood buffer, to avoid memory leaks.
//                                likelyPlaces.release();
//
//                                // Show a dialog offering the user the list of likely places, and add a
//                                // marker at the selected place.
//                                openPlacesDialog();
//
//                            } else {
//                                Log.e(TAG, "Exception: %s", task.getException());
//                            }
//                        }
//                    });
//        } else {
//            // The user has not granted permission.
//            Log.i(TAG, "The user did not grant location permission.");
////
////            var SameThreshold = 0.1;
////            if (google.maps.geometry.spherical.computeDistanceBetween(latlng1,latlng2) < SameThreshold)
////                marker1.setAnimation(google.maps.Animation.BOUNCE);
//            // Add a default marker, because the user hasn't selected a place.
//            mMap.addMarker(new MarkerOptions()
//                    .title(getString(R.string.default_info_title))
//                    .position(mDefaultLocation)
//                    .snippet(getString(R.string.default_info_snippet)));
//
//            // Prompt the user for permission.
//            getLocationPermission();
//        }
//    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                View locat_butt=((View)getActivity().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)locat_butt.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
                layoutParams.setMargins(0,0,30,30);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public static void hideactivityitems(Activity activity){
//        activity.findViewById(R.id.searchBar_q).setVisibility(View.VISIBLE);
//        activity_order_details.findViewById(R.id.message).setVisibility(View.VISIBLE);

    }

    private class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Loading places. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            Log.d(TAG, "onPreExecute: "+new strings_().get_ipaddress(getContext())+"/branch/get");
            // Prompt the user for permission.
            getLocationPermission();

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }

        /**
         * getting All products from url
         * */

        protected String doInBackground(String... args) {
//            // Building Parameters
            List<NameValuePair> post_params = new ArrayList<NameValuePair>();
//            post_params.add(new BasicNameValuePair("id", "id"));
//            post_params.add(new BasicNameValuePair("title","title"));
//
//
//            // getting JSON Object
//            // Note that create product url accepts POST method
//            JSONObject json_post_course = jParser.makeHttpRequest(url_all_products,
//                    "POST", post_params);

//                post_params.add(new BasicNameValuePair("branch",bookingDetails.getB_name()));
//                post_params.add(new BasicNameValuePair("start",bookingDetails.getTime()));
//                post_params.add(new BasicNameValuePair("institution",String.valueOf(bookingDetails.getId())));

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(new strings_().get_ipaddress(getContext())+"/branch/get", "GET", params);
            JSONObject resultObject;
            JSONArray jsonArray = null;


            if (json!=null) {


//                Log.d(TAG, "doInBackground: branch get --" + json.toString());
                try {
                    resultObject = new JSONObject(json.toString());
                    jsonArray = resultObject.optJSONArray("branches");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonArray != null) {
                    // Check your log cat for JSON reponse


                    try {
                        // Checking for SUCCESS TAG
//                    int success = json.getInt(TAG_SUCCESS);

//                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        products = json.getJSONArray(TAG_PRODUCTS);
                        Log.d("All Products: ", json.toString());

                        // looping through All Products
                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            // Storing each json item in variable
                            String id = c.getString(TAG_PID);
                            String name = c.getString(TAG_company);
                            String dblongitude = c.getString(Tag_longitude);
                            String dblatitude = c.getString(Tag_latitude);
                            String dbaddress = c.getString(Tag_branch_name);
                            String dbbranch = c.getString(Tag_branch_name);

//                            Log.d("All Products: ", name);
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_PID, id);
                            map.put(TAG_company, name);//company id
                            map.put(Tag_longitude, dblongitude);
                            map.put(Tag_latitude, dblatitude);
                            map.put(Tag_address, dbaddress);
                            map.put(Tag_branch_name, dbbranch);
                            // adding HashList to ArrayList
                            productsList.add(map);
                            list_size = productsList.size();


                            //to pass data


                            Log.d("data size", "doInBackground: " + dblatitude + "----" + longi);
//                            Toast.makeText(activity_order_details, productsList.get(1).get(Tag_longitude), Toast.LENGTH_SHORT).show();
                        }

//                    else {
//                        // no products found
//
//                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            else {
//                Toast.makeText(getContext(), "No services available", Toast.LENGTH_SHORT).show();

                pDialog.dismiss();
                new LoadAllProducts().cancel(true);

            }






//
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        int i;
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products

            for(int i = 0; i<productsList.size(); i++) {

                list_id= Integer.parseInt(productsList.get(i).get(TAG_PID));
                lat= Double.parseDouble(productsList.get(i).get(Tag_latitude));
                longi= Double.parseDouble(productsList.get(i).get(Tag_longitude));
                strcompany_name= productsList.get(i).get(TAG_company);
                straddress=productsList.get(i).get(Tag_address);
                strbranch_name=productsList.get(i).get(Tag_branch_name);
                // Storing each json item in variable

//            double latttitude= Double.parseDouble(arrayList_latlong.get(i).substring(0, arrayList_latlong.get(i).indexOf(",")));
//            double longittude= Double.parseDouble(arrayList_latlong.get(i).substring(arrayList_latlong.get(i).indexOf(",")+1, arrayList_latlong.get(i).length()));

//                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//                Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
//                Canvas canvas1 = new Canvas(bmp);
//
//// paint defines the text color, stroke width and size
//                Paint color = new Paint();
//                color.setTextSize(35);
//                color.setColor(Color.BLACK);
//
//// modify canvas
//                canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
//                        R.drawable.ic_location_on_black_24dp), 0,0, color);
//                canvas1.drawText(strcompany_name, 30, 40, color);

//                MarkerOptions marker = new MarkerOptions().position(
//                        new LatLng(lat, longi))
//                        .title(strcompany_name).snippet(strbranch_name);

                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(lat, longi))
                        .title(strcompany_name).snippet(strbranch_name);
//
//
                Marker marker1=mMap.addMarker(marker);
                marker1.showInfoWindow();
                marker1.setTag(productsList.get(i));



                Log.d("TAG", "onPostExecute: "+lat+"  ---"+longi );


            }

            pDialog.dismiss();
//            getActivity().getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, new queue())
//                    .commit();
            // updating UI from Background Thread

//            new Thread() {
//                public void run() {
//                    while (i++ < 1000) {
//                        try {
//                            getActivity().runOnUiThread(new Runnable() {
//
//                                @Override
//                                public void run() {
//
//                                }
//                            });
//                            Thread.sleep(300);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }.start();
//            activity_order_details.runOnUiThread(new Runnable() {
//                public void run() {
//
//
//
//                }});



//            final Handler handler = new Handler();
//            Runnable refresh = new Runnable() {
//                @Override
//                public void run() {
//
//                    listView.invalidateViews();
//                    handler.postDelayed(this, 60 * 1000);
//                }
//            };
//            handler.postDelayed(refresh, 1000);
        }

    }



}
