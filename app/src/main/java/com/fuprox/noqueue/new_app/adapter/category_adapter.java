package com.fuprox.noqueue.new_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.utils.service_details;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


public class category_adapter extends RecyclerView.Adapter<category_adapter.ViewHolder> {
    private ArrayList<service_details> orderList;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public category_adapter(Context context, ArrayList<service_details> orderList) {
        this.orderList = orderList;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_home_category, parent, false);
        return new ViewHolder(view);
    }

    int row_index = RecyclerView.NO_POSITION;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final service_details order = orderList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index=position;
                notifyDataSetChanged();
            }
        });
        CardView cardView = holder.itemView.findViewById(R.id.orders_cardview);
        TextView tvtit=holder.itemView.findViewById(R.id.service_title);
        ImageView img_background = holder.itemView.findViewById(R.id.background_image);
        if(row_index==position){
//            holder.itemView.setBackgroundColor(Color.parseColor("#567845"));
//            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));

//
//            float ThirteenDp = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 13,
//                    context.getResources().getDisplayMetrics() );
////            cardView.setCardElevation(ThirteenDp);
//            cardView.setRadius(ThirteenDp);

            img_background.setImageDrawable(context.getResources().getDrawable(R.drawable.bottom_selected));
            tvtit.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else if (row_index == RecyclerView.NO_POSITION && position==0){
//            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
//            float ThirteenDp = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 13,
//                    context.getResources().getDisplayMetrics() );
//            cardView.setRadius(ThirteenDp);
            img_background.setImageDrawable(context.getResources().getDrawable(R.drawable.bottom_selected));
            tvtit.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else {
//            cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//            float ThirteenDp = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 0,
//                    context.getResources().getDisplayMetrics() );
//            cardView.setRadius(ThirteenDp);
            img_background.setImageDrawable(context.getResources().getDrawable(R.drawable.bottom_unselected));
            tvtit.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        String s_title = order.getTitle();
        String s_id=order.getId();

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
        //Here, calling itemView (equivalent of listAdapter.getView()) and setting a onClickListener as an example. You can do whatever you want.
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, OrderDetailActivity.class);
//                intent.putExtra("order", order);
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    //In the ViewHolder you declare all the components of your xml layout for you recyclerView items ( and the data is assigned in the onBindViewHolder, above)
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,txtid,txt_serviceimage;

        private ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.service_title);
            txtid=itemView.findViewById(R.id.serviceid);
            txt_serviceimage = itemView.findViewById(R.id.service_image);
        }
    }
}

