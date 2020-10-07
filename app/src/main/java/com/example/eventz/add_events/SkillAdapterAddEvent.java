package com.example.eventz.add_events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventz.R;

import java.util.ArrayList;

public class SkillAdapterAddEvent extends RecyclerView.Adapter<SkillAdapterAddEvent.skillViewHolder> {
    private Context mcontext;
    private ArrayList<SkillItemAddEvent> mSkillList;
    private static onItemClickListener mlistener;

    public static void setOnItemClickListener(onItemClickListener listener) {
        mlistener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public SkillAdapterAddEvent(Context mcontext, ArrayList<SkillItemAddEvent> mSkillList) {
        this.mcontext = mcontext;
        this.mSkillList = mSkillList;
    }

    @Override
    public skillViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.skill_item, parent, false);
        return new skillViewHolder(v);
    }

    @Override
    public void onBindViewHolder(skillViewHolder holder, int position) {
        SkillItemAddEvent currentItem = mSkillList.get(position);

        String skill_name = currentItem.getSkillName();
        holder.skillName.setText(skill_name);
    }

    @Override
    public int getItemCount() {
        return mSkillList.size();
    }

    public class skillViewHolder extends RecyclerView.ViewHolder {

        public TextView skillName;
        public ImageView deleteImg;

        public skillViewHolder(View itemView) {
            super(itemView);

            skillName = itemView.findViewById(R.id.text_skill_name);
            deleteImg = itemView.findViewById(R.id.image_delete);

            deleteImg.setOnClickListener(new View.OnClickListener() {
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
