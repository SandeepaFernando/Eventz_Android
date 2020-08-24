package com.example.eventz.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.eventz.R;

import java.util.ArrayList;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    private Context mcontext;
    private ArrayList<VendorInfo> mvendorlist;
    private static VendorAdapter.onItemClickListener mlistener;

    public static void setOnItemClickListener(onItemClickListener listener) {
        mlistener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public VendorAdapter(Context mcontext, ArrayList<VendorInfo> mvendorlist) {
        this.mcontext = mcontext;
        this.mvendorlist = mvendorlist;
    }


    @Override
    public VendorViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.vendor_home_item, parent, false);
        return new VendorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(VendorViewHolder holder, int position) {
        VendorInfo currentItem = mvendorlist.get(position);

        String title = currentItem.getTitle();
        String venue = currentItem.getVenue();
        String date = currentItem.getDate();
        String num_people = currentItem.getNum_people();

        holder.titleTV.setText(title);
        holder.venueTV.setText(venue);
        holder.dateTV.setText(date);
        holder.num_peopleTV.setText(num_people);

    }


    @Override
    public int getItemCount() {
        return mvendorlist.size();
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTV;
        public TextView venueTV;
        public TextView dateTV;
        public TextView num_peopleTV;

        public VendorViewHolder(View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.txtview_title);
            venueTV = itemView.findViewById(R.id.txtview_venue);
            dateTV = itemView.findViewById(R.id.txtview_date);
            num_peopleTV = itemView.findViewById(R.id.txtview_numofpeople);


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
