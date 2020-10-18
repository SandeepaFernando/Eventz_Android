package com.example.eventz.eventInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventz.R;

import java.util.ArrayList;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.VendorViewHolder> {
    private Context mcontext;
    private ArrayList<BidInfo> mvendorlist;
    private static BidAdapter.onItemClickListener mlistener;

    public static void setOnItemClickListener(BidAdapter.onItemClickListener listener) {
        mlistener = listener;
    }

    public interface onItemClickListener {
        void onItemVendorClick(int position);
    }

    public BidAdapter(Context mcontext, ArrayList<BidInfo> mvendorlist) {
        this.mcontext = mcontext;
        this.mvendorlist = mvendorlist;
    }


    @Override
    public BidAdapter.VendorViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.event_bids_item, parent, false);
        return new BidAdapter.VendorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BidAdapter.VendorViewHolder holder, int position) {
        BidInfo currentItem = mvendorlist.get(position);

        String name = currentItem.getName();
        String email = currentItem.getEmail();

        holder.nameTv.setText(name);
        holder.emailTv.setText(email);

    }


    @Override
    public int getItemCount() {
        return mvendorlist.size();
    }

    public class VendorViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTv;
        public TextView emailTv;

        public VendorViewHolder(View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.txtview_name_acc_vendor);
            emailTv = itemView.findViewById(R.id.txtview_email_acc_vendor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mlistener.onItemVendorClick(position);
                        }
                    }
                }
            });

        }
    }

}
