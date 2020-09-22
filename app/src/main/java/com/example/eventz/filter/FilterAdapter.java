package com.example.eventz.filter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventz.R;

import java.util.ArrayList;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.filterViewHolder> {
    private Context mcontext;
    private ArrayList<FilterItem> mFillterList;
    private static FilterAdapter.onItemClickListener mlistener;

    public static void setOnItemClickListener(FilterAdapter.onItemClickListener listener) {
        mlistener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public FilterAdapter(Context mcontext, ArrayList<FilterItem> mFillterList) {
        this.mcontext = mcontext;
        this.mFillterList = mFillterList;
    }

    @Override
    public FilterAdapter.filterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.item_filter, parent, false);
        return new FilterAdapter.filterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FilterAdapter.filterViewHolder holder, int position) {
        FilterItem currentItem = mFillterList.get(position);

        String fName = currentItem.getfName();
        String fEmail = currentItem.getuEmail();
        String location = currentItem.getLocation();
        String rateCat = currentItem.getRateCategory();

        Log.i("FILTERADAP ", location);

        holder.fNameTv.setText(fName);
        holder.femailTv.setText(fEmail);
        holder.locationTv.setText(location);

        if (rateCat.equals("Platinum")) {
            holder.rateIconIV.setImageResource(R.drawable.plattinum_icon);
        } else if (rateCat.equals("Gold")) {
            holder.rateIconIV.setImageResource(R.drawable.gold_icon);
        } else {
            holder.rateIconIV.setImageResource(R.drawable.silver_icon);
        }
    }

    @Override
    public int getItemCount() {
        return mFillterList.size();
    }

    public class filterViewHolder extends RecyclerView.ViewHolder {

        public TextView fNameTv;
        public TextView femailTv;
        public TextView locationTv;
        public ImageView rateIconIV;

        public filterViewHolder(View itemView) {
            super(itemView);

            fNameTv = itemView.findViewById(R.id.text_filter_first_name);
            femailTv = itemView.findViewById(R.id.text_filter_email);
            locationTv = itemView.findViewById(R.id.text_filter_location);
            rateIconIV = itemView.findViewById(R.id.image_rate_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mlistener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

}
