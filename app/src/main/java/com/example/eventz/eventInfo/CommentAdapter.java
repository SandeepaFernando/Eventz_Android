package com.example.eventz.eventInfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventz.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.commentViewHolder> {
    private Context mcontext;
    private ArrayList<CommentInfo> mCommentList;
    private static CommentAdapter.onItemClickListener mlistener;
    String userSP;

    public static void setOnItemClickListener(CommentAdapter.onItemClickListener listener) {
        mlistener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);

        void onItemClickEdit(int position);
    }

    public CommentAdapter(Context mcontext, ArrayList<CommentInfo> mCommentsList) {
        this.mcontext = mcontext;
        this.mCommentList = mCommentsList;
    }

    @Override
    public CommentAdapter.commentViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(mcontext).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.commentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.commentViewHolder holder, int position) {
        CommentInfo currentItem = mCommentList.get(position);

        String comment_name = currentItem.getName_comment();
        String comment_date = currentItem.getDate_comment();
        String comment_str = currentItem.getComment_st();
        userSP = currentItem.getUserNameSP();
        String comment_user = currentItem.getUserNameComment();

        if (userSP.equals(comment_user)) {
            holder.comment_edit_delete_layout.setVisibility(View.VISIBLE);
        }

        holder.commentName.setText(comment_name + " -");
        holder.commentDate.setText(comment_date);
        holder.commentStr.setText(comment_str);
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class commentViewHolder extends RecyclerView.ViewHolder {

        public TextView commentName;
        public TextView commentDate;
        public TextView commentStr;
        public TextView commentDelete;
        public TextView commentEdit;
        LinearLayout comment_edit_delete_layout;

        public commentViewHolder(View itemView) {
            super(itemView);

            commentName = itemView.findViewById(R.id.text_name_comment);
            commentDate = itemView.findViewById(R.id.text_date_comment);
            commentStr = itemView.findViewById(R.id.text_comment_st_Comment);
            commentDelete = itemView.findViewById(R.id.text_delete_comment);
            commentEdit = itemView.findViewById(R.id.text_edit_comment);
            comment_edit_delete_layout = itemView.findViewById(R.id.comment_edit_delete_layout);

            commentDelete.setOnClickListener(new View.OnClickListener() {
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

            commentEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mlistener.onItemClickEdit(position);
                        }
                    }
                }
            });


        }
    }

}
