package com.fuprox.noqueue.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.utils.institution_details;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

import static com.fuprox.noqueue.adapters.orderadapter.TAG;

public class institution_adapater extends ArrayAdapter<institution_details> {

    private Activity mActivity;
    private int mResource;
    private ArrayList<institution_details> orders_list;
    private ListView mListView;
    institution_details institution_details;
    private int lastPosition = -1;
    View convertView;

    private static class ViewHolder {
        TextView title,txtid,txtvtime,tvticket,tvdate;
        ImageView txt_serviceimage;
        CardView cardView;
        ProgressBar pDialog;
    }
    public institution_adapater(Activity activity, ListView listView, int resource, ArrayList<institution_details> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        mListView = listView;
        mResource = resource;
        orders_list = objects;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final institution_adapater.ViewHolder holder;
        final String name = Objects.requireNonNull(getItem(position)).getTitle();
        final String ins_id = Objects.requireNonNull(getItem(position)).getId();
        final String icon_url = Objects.requireNonNull(getItem(position)).getIcon_url();
        this.convertView=convertView;
        institution_details=new institution_details(ins_id,name,icon_url);


        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new institution_adapater.ViewHolder();
            holder.title = convertView.findViewById(R.id.institution_title);
            holder.txtid=convertView.findViewById(R.id.txtinstitution_id);
            holder.txt_serviceimage=convertView.findViewById(R.id.service_image);
            holder.pDialog = convertView.findViewById(R.id.imageprogress);

            convertView.setTag(holder);
        } else {
            holder = (institution_adapater.ViewHolder) convertView.getTag();
        }

        String s_title = institution_details.getTitle();
        String s_id= institution_details.getId();



        holder.title.setText(s_title);
        holder.txtid.setText(s_id);


//        String initials = "";
//        for (String s : s_title.split(" ")) {
//            initials+=s.charAt(0);
//        }
//        if (initials.length()>2){
//            holder.txt_serviceimage.setText(initials.substring(0,2));
//        }
//        else {
//            holder.txt_serviceimage.setText(initials);
//        }

//        new load_icon(icon_url,holder).execute();

//        new DownloadImageTask(holder.txt_serviceimage).execute(icon_url);
//        Picasso.get().load(icon_url).into(holder.txt_serviceimage);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for gingerbread and newer versions
            Picasso.get().load(icon_url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.i(TAG, "The image was obtained correctly");
                    holder.txt_serviceimage.setImageBitmap(bitmap);
//                    holder.pDialog.setVisibility(View.GONE);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                    pDialog.dismiss();
//                    holder.pDialog.setVisibility(View.GONE);

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.i(TAG, "Getting ready to get the image");
//                    holder.pDialog.setVisibility(View.VISIBLE);

                    //Here you should place a loading gif in the ImageView
                    //while image is being obtained.
                }
            });
        }
        else {
            new DownloadImageTask(holder.txt_serviceimage,holder.pDialog).execute(icon_url);
        }



//        holder.txt_serviceimage.setText(initials.substring(0,1));

        Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;


        return convertView;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        ProgressBar pDialog;
        public DownloadImageTask(ImageView bmImage,ProgressBar pDialog) {
            this.bmImage = bmImage;
            this.pDialog = pDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog.setVisibility(View.VISIBLE);
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
//            pDialog.setVisibility(View.GONE);
            bmImage.setImageBitmap(result);
        }
    }

//
//    private class load_icon extends AsyncTask<String, String, String> {
//        String icon_url,error,error_title;
//        ViewHolder holder;
//        public load_icon(String icon_url,ViewHolder holder) {
//            this.icon_url = icon_url;
//            this.holder = holder;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog.setVisibility(View.VISIBLE);
//
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            HttpClient client = new DefaultHttpClient();
//            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
//            HttpResponse response;
//            JSONObject json = new JSONObject();
//            StringBuilder sb;
//            InputStream is = null;
//            String result = null;
//
//            try {
//                HttpPost post = new HttpPost(icon_url);
////                json.put("branch", "");
//
//                StringEntity se = new StringEntity(json.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                post.setEntity(se);
//                response = client.execute(post);
//                HttpEntity entity = response.getEntity();
//                is = entity.getContent();
//
//                /*Checking response */
//                if (response != null) {
//
//                    try {
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                                is, "iso-8859-1"), 8);
//                        sb = new StringBuilder();
//                        sb.append(reader.readLine()).append("\n");
//                        String line = "0";
//
//                        while ((line = reader.readLine()) != null) {
//                            sb.append(line).append("\n");
//                        }
//
//                        is.close();
//                        result = sb.toString();
//
////                            CONVERT THE RESPONSE RESOULT TO JSON FROM STRRING
//
//                        JSONObject obj = null;
//                        JSONArray jsonarray;
//                        try {
////                            obj= new JSONObject(result);
//
//                            jsonarray = new JSONArray(result);
//
//
//                            String name = null;
//                            int i = 0;
//                            while (i < jsonarray.length()) {
//                                obj = new JSONObject(jsonarray.getString(i));
//
//                                Log.d(TAG, "doInBackground: "+obj);
//                                i++;
//                            }
//                            Log.d("TAG", "get all services " + jsonarray);
//
//
//                        } catch (Throwable t) {
//                            Log.e("My App", "Could not parse malformed JSON: QUESTIONS\"" + obj + "\"" + t.getMessage());
//                            error = "Oops.. \n There was a problem communicating with the servers.";
//                            error_title="Server error";
////                            network_error(view,"Server error",error);
//                        }
////                            Log.d(TAG, "run: string" + result+new Dbhelper(activity).get_user_id());
//                    } catch (Exception e) {
//                        Log.e("log_tag", "Error converting result " + e.toString());
//                        error = "Oops.. \n There was a problem communicating with the servers.";
//                        error_title="Server error";
////                        network_error(view,error_title,error);
////                            pDialog.dismiss();
//
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
////                    Toast.makeText(getActivity(), "Problem Establishing Connection", Toast.LENGTH_SHORT).show();
////                    new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
////                            .setTitleText("There was a problem communicating with the servers.")
////                            .setContentText("Try again later.")
////                            .show();
//                Log.d("", "run: services get");
//                error = "Oops.. \nThere was a problem establishing internet connection.";
//                error_title="Internet connection error!";
//
////                network_error(view,error_title,error);
////                    pDialog.cancel();
////                    createDialog("Error", "Cannot Estabilish Connection");
//            }
//
//            return null;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            pDialog.dismiss();
//        }
//    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ArrayList<institution_details> getNoteList() {
        return orders_list;
    }

//    public Note getNote() {
//        return note;
//    }

    private void hidePopUpMenu(institution_adapater.ViewHolder holder) {
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
}
