package com.example.currentplacedetailsonmap.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.R;
import com.example.currentplacedetailsonmap.utils.suggestion_details;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;

public class suggestion_adapter extends SuggestionsAdapter<suggestion_details, suggestion_adapter.SuggestionHolder>  {
    public suggestion_adapter(LayoutInflater inflater) {

        super(inflater);
    }

    @Override
    public void onBindSuggestionHolder(suggestion_details suggestion, SuggestionHolder holder, int position) {
        try {

            holder.title.setText(suggestion.getTitle());
            holder.tvlongitude.setText(suggestion.getCompany_id());//company id
            holder.tvlattitude.setText(suggestion.getId());
            holder.tvmedical.setText(suggestion.getIsmedical());
        }catch (Exception e){
            Log.d("TAG", "onBindSuggestionHolder: "+e.getLocalizedMessage());
        }

    }


    @Override
    public int getSingleViewHeight() {
        return 80;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


        View view = null;
        try{
           view = getLayoutInflater().inflate(R.layout.layout_suggestion, viewGroup, false);

        }catch (Exception e){
            
        }
        return new SuggestionHolder(view);


    }
    /**
     * <b>Override to customize functionality</b>
     * <p>Returns a filter that can be used to constrain data with a filtering
     * pattern.</p>
     * <p>
     * <p>This method is usually implemented by
     * classes.</p>
     *
     * @return a filter used to constrain data
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                try {
                    String term = constraint.toString();
                    if(term.isEmpty()){
                        suggestions = suggestions_clone;
                    }

                    else {
                        suggestions = new ArrayList<>();
                        for (suggestion_details item: suggestions_clone)
                            if(item.getTitle().toLowerCase().contains(term.toLowerCase())||item.getCompany().toLowerCase().contains(term.toLowerCase()))
                                suggestions.add(item);
                    }
                    results.values = suggestions;
                }
                catch (Exception e){

                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                suggestions = (ArrayList<suggestion_details>) results.values;
                notifyDataSetChanged();

            }
        };
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        protected TextView tvlongitude,tvlattitude,tvmedical;
        protected ImageView image;

        public SuggestionHolder(View itemView) {
            super(itemView);
            try {
                tvmedical=itemView.findViewById(R.id.tvsuggestion_bmedical);
                title = (TextView) itemView.findViewById(R.id.suggestion_title);
                tvlongitude = (TextView) itemView.findViewById(R.id.suggestion_long);
                tvlattitude = (TextView) itemView.findViewById(R.id.suggestion_lat);
            }catch (Exception e){

            }

        }
    }
}
