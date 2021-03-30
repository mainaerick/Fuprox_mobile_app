package com.fuprox.noqueue.adapters;

import android.app.Activity;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.utils.services_offered_details;

import java.util.ArrayList;
import java.util.Objects;

public class service_offered_adapter extends ArrayAdapter<services_offered_details> {

    private Activity mActivity;
    private int mResource;
    private ArrayList<services_offered_details> ser_offered_list;
    private ListView mListView;
    services_offered_details services_offered_details;
    private int lastPosition = -1;


    private static class ViewHolder {
        TextView title,txtid,txtvtime,tvticket,tvdate;
        ImageView popup;
        CardView cardView;
    }

    public service_offered_adapter(Activity activity, int resource, ArrayList<services_offered_details> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        mResource = resource;
        ser_offered_list = objects;

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final service_offered_adapter.ViewHolder holder;
        final String name = Objects.requireNonNull(getItem(position)).getService_name();
        final String ser_id = Objects.requireNonNull(getItem(position)).getService_id();
        final String ser_code = Objects.requireNonNull(getItem(position)).getService_code();
        final String ser_teler = Objects.requireNonNull(getItem(position)).getService_teller();



        services_offered_details=new services_offered_details(name,ser_id,ser_code,ser_teler);


        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new service_offered_adapter.ViewHolder();
            holder.title = convertView.findViewById(R.id.textview_services);
            holder.txtid=convertView.findViewById(R.id.ser_id);

            convertView.setTag(holder);
        } else {
            holder = (service_offered_adapter.ViewHolder) convertView.getTag();
        }

        String s_title = services_offered_details.getService_name();
        String s_id= services_offered_details.getService_id();



        holder.title.setText(s_title);
        holder.txtid.setText(s_id);


        Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ArrayList<services_offered_details> getNoteList() {
        return ser_offered_list;
    }

//    public Note getNote() {
//        return note;
//    }

    private void hidePopUpMenu(service_offered_adapter.ViewHolder holder) {
//        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
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
