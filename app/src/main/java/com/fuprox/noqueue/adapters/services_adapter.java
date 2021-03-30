package com.fuprox.noqueue.adapters;

import android.app.Activity;
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
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.utils.service_details;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class services_adapter extends ArrayAdapter<service_details> implements Filterable {
    private Activity mActivity;
    private int mResource;
    private ArrayList<service_details> orders_list;
    private ArrayList<service_details> itemListPogo;
    private ListView mListView;
    service_details service_details;

    private int lastPosition = -1;

    private static class ViewHolder {
        TextView title,txtid,txt_serviceimage,txtvtime,tvticket,tvdate;
        ImageView popup;
        CardView cardView;
    }




    public services_adapter(Activity activity, ListView listView, int resource, ArrayList<service_details> objects) {
        super(activity, resource, objects);
        mActivity = activity;
        mListView = listView;
        mResource = resource;
        orders_list = objects;
        this.itemListPogo= new ArrayList<service_details>();
        itemListPogo.addAll(objects);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final services_adapter.ViewHolder holder;
        final String name = Objects.requireNonNull(getItem(position)).getTitle();
        final String serv_id=Objects.requireNonNull(getItem(position)).getId();

        service_details=new service_details(serv_id,name);


        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new services_adapter.ViewHolder();
            holder.title = convertView.findViewById(R.id.service_title);
            holder.txtid=convertView.findViewById(R.id.serviceid);
            holder.txt_serviceimage = convertView.findViewById(R.id.service_image);
            convertView.setTag(holder);
        } else {
            holder = (services_adapter.ViewHolder) convertView.getTag();
        }

        String s_title = service_details.getTitle();
        String s_id=service_details.getId();

        holder.title.setText(s_title);
        holder.txtid.setText(s_id);

        String initials = "";
        for (String s : s_title.split(" ")) {
            initials += s.charAt(0);
        }
        if (initials.length()>2){
            holder.txt_serviceimage.setText(initials.substring(0,2));
        }
        else {
            holder.txt_serviceimage.setText(initials);
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

    public ArrayList<service_details> getNoteList() {
        return orders_list;
    }

//    public Note getNote() {
//        return note;
//    }

    private void hidePopUpMenu(services_adapter.ViewHolder holder) {
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

    public void filter(String charText)
    {

        charText = charText.toLowerCase(Locale.getDefault());
            orders_list.clear();
//        Log.d("TAG", "filter: "+itemListPogo.get(0).getTitle());

        if (charText.length() == 0)
        {
            orders_list.addAll(itemListPogo);
            Log.d("TAG", "text=0   : "+orders_list.get(0).getTitle());
        }
        else
        {
            for (int i=0;i<itemListPogo.size();i++)
            {


                if (itemListPogo.get(i).getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText))
                {
                    service_details.setTitle(itemListPogo.get(i).getTitle());
                    service_details.setId(itemListPogo.get(i).getId());
                    orders_list.add(service_details);

//                    Log.d("TAG", "filter: "+orders_list.get(i));
                }

            }
        }
        notifyDataSetChanged();

    }
}
