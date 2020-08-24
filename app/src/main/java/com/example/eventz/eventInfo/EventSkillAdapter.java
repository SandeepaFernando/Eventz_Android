package com.example.eventz.eventInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventz.R;

import java.util.ArrayList;

public class EventSkillAdapter extends RecyclerView.Adapter<EventSkillAdapter.TagViewHolder> {
    private Context mcontext;
    private ArrayList<EventSkillInfo> mTaglList;

    public EventSkillAdapter(Context mcontext, ArrayList<EventSkillInfo> mTaglList) {
        this.mcontext = mcontext;
        this.mTaglList = mTaglList;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.skilltag_set_item, parent, false);
        return new TagViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventSkillAdapter.TagViewHolder holder, int position) {
        EventSkillInfo currentItem = mTaglList.get(position);

        String skill_name = currentItem.getTagName();
        holder.skillName.setText(skill_name);
    }

    @Override
    public int getItemCount() {
        return mTaglList.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        public TextView skillName;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            skillName = itemView.findViewById(R.id.text_skill_name);
        }
    }
}
