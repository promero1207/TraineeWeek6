package com.app.paul.newsapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.paul.newsapp.News;
import com.app.paul.newsapp.R;

import java.util.List;

/**
 * adapter for recycler view
 */
public class AdapterReadLater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener listener;
    private OnItemDeleteListener deleteListenr;
    private List<News> adapterList;


    //constructor
    public AdapterReadLater(List<News> list, OnItemClickListener listener, OnItemDeleteListener delteListener) {
        adapterList = list;
        this.listener = listener;
        this.deleteListenr = delteListener;
    }

    //return viewtype depending on position, 0 for loading progress view; 1 for news


    //creates holder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_read_later, parent, false);

        return new ViewHolder(v);
    }

    //on binding holder
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0) {
            final ViewHolder h = (ViewHolder) holder;
            h.headline.setText(adapterList.get(position).getHeadline());
            h.section.setText(adapterList.get(position).getSection());
            h.deleteReadLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteListenr.onItemDeleteClick(adapterList.get(holder.getAdapterPosition()).getNewsId());
                    adapterList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyItemRangeChanged(holder.getAdapterPosition(), adapterList.size());
                }
            });
        }
    }

    //geting list size count
    @Override
    public int getItemCount() {
        return adapterList.size();
    }

    //View holder
    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView headline;
        TextView section;
        ImageButton deleteReadLater;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.item_headline);
            section = itemView.findViewById(R.id.item_section);
            deleteReadLater = itemView.findViewById(R.id.button_delete_read_later);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    //on click interface
    public interface OnItemClickListener {
        void onItemClick(Integer position);
    }

    public interface OnItemDeleteListener {
        void onItemDeleteClick(String id);
    }



}
