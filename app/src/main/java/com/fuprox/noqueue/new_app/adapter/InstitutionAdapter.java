package com.fuprox.noqueue.new_app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fuprox.noqueue.R;
import com.fuprox.noqueue.adapters.institution_adapater;
import com.fuprox.noqueue.utils.institution_details;
import com.fuprox.noqueue.utils.service_details;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import static com.fuprox.noqueue.adapters.orderadapter.TAG;

public class InstitutionAdapter extends RecyclerView.Adapter<InstitutionAdapter.ViewHolder> {
    private ArrayList<institution_details> orderList;
    private LayoutInflater mLayoutInflater;
    private Context context;

    public InstitutionAdapter(Context context, ArrayList<institution_details> orderList) {
        this.orderList = orderList;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public InstitutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_home_institutions, parent, false);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.width = (parent.getWidth() / 2) - layoutParams.leftMargin - layoutParams.rightMargin;
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final institution_details order = orderList.get(position);

        final String name = order.getTitle();
        final String ins_id = order.getId();
        final String icon_url = order.getIcon_url();

        holder.title.setText(name);
        holder.txtid.setText(ins_id);

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
                    Log.e(TAG, "onBitmapFailed: instituion: "+e.toString() );

//                    String initials = "";
//                    for (String s : name.split(" ")) {
//                        initials += s.charAt(0);
//                    }
//                    if (initials.length()>2){
//                        holder.txt_serviceimage.setImageDrawable(new TextDrawable(context.getResources(),initials.substring(0,2)));
//
//                    }
//                    else {
//                        holder.txt_serviceimage.setImageDrawable(new TextDrawable(context.getResources(),initials));
//
//                    }

//                    holder.txt_serviceimage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_business_black_24dp));

//                    pDialog.dismiss();
//                    holder.pDialog.setVisibility(View.GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.i(TAG, "Getting ready to get the image");
//                    holder.pDialog.setVisibility(View.VISIBLE);
//                    holder.txt_serviceimage.setImageDrawable(placeHolderDrawable);

                    //Here you should place a loading gif in the ImageView
                    //while image is being obtained.
                }
            });
        }
        else {
            new DownloadImageTask(holder.txt_serviceimage).execute(icon_url);
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
        TextView title,txtid,txtvtime,tvticket,tvdate;
        ImageView txt_serviceimage;
        CardView cardView;
        ProgressBar pDialog;

        private ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.institution_title);
            txtid=itemView.findViewById(R.id.txtinstitution_id);
            txt_serviceimage = itemView.findViewById(R.id.service_image);
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
    public class TextDrawable extends Drawable {
//        private static final int DEFAULT_COLOR = ;
        private static final int DEFAULT_TEXTSIZE = 30;
        private Paint mPaint;
        private CharSequence mText;
        private int mIntrinsicWidth;
        private int mIntrinsicHeight;

        public TextDrawable(Resources res, CharSequence text) {
            mText = text;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(res.getColor(R.color.colorPrimaryDark));
            mPaint.setTextAlign(Paint.Align.CENTER);
            float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    DEFAULT_TEXTSIZE, res.getDisplayMetrics());
            mPaint.setTextSize(textSize);
            mIntrinsicWidth = (int) (mPaint.measureText(mText, 0, mText.length()) + .5);
            mIntrinsicHeight = mPaint.getFontMetricsInt(null);
        }
        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.drawText(mText, 0, mText.length(),
                    bounds.centerX(), bounds.centerY(), mPaint);
        }
        @Override
        public int getOpacity() {
            return mPaint.getAlpha();
        }
        @Override
        public int getIntrinsicWidth() {
            return mIntrinsicWidth;
        }
        @Override
        public int getIntrinsicHeight() {
            return mIntrinsicHeight;
        }
        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }
        @Override
        public void setColorFilter(ColorFilter filter) {
            mPaint.setColorFilter(filter);
        }
    }
}
