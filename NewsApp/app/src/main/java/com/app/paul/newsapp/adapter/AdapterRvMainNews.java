package com.app.paul.newsapp.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.paul.newsapp.News;
import com.app.paul.newsapp.R;

import java.io.InputStream;
import java.util.List;

/**
 * adapter for recycler view
 */
public class AdapterRvMainNews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener listener;
    private OnItemReadLaterClickListener readLaterListener;
    private List<News> adapterList;


    //constructor
    public AdapterRvMainNews(List<News> list, OnItemClickListener listener, OnItemReadLaterClickListener readLaterListener) {
        adapterList = list;
        this.listener = listener;
        this.readLaterListener = readLaterListener;
    }

    //return viewtype depending on position, 0 for loading progress view; 1 for news
    @Override
    public int getItemViewType(int position) {

        if (position == adapterList.size() - 1) {
            return 1;
        } else {
            return 0;
        }
    }

    //creates holder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if(viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load, parent, false);
        }
        return new ViewHolder(v);
    }

    //on binding holder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0) {
            final ViewHolder h = (ViewHolder) holder;
            h.headline.setText(adapterList.get(position).getHeadline());
            h.section.setText(adapterList.get(position).getSection());
            if(adapterList.get(position).isReadLater() == 1) {
                h.button_watch_later.setColorFilter(Color.parseColor("red"));
            }
            else {
                h.button_watch_later.setColorFilter(Color.parseColor("gray"));
            }
            h.button_watch_later.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(adapterList.get(h.getAdapterPosition()).isReadLater() == 1) {
                        h.button_watch_later.setColorFilter(Color.parseColor("gray"));
                        adapterList.get(h.getAdapterPosition()).setReadLater(0);
                    }
                    else {
                        h.button_watch_later.setColorFilter(Color.parseColor("red"));
                        adapterList.get(h.getAdapterPosition()).setReadLater(1);
                    }
                    readLaterListener.onItemReadLaterClick(h.getAdapterPosition(), adapterList.get(h.getAdapterPosition()).isReadLater());
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
        ImageButton button_watch_later;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.item_headline);
            section = itemView.findViewById(R.id.item_section);
            button_watch_later = itemView.findViewById(R.id.button_watch_later);
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

    public interface OnItemReadLaterClickListener {
        void onItemReadLaterClick(Integer position, int isReadLater);
    }


    /**
     * method for downloading and showing image from url
     */
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
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
            bmImage.setImageBitmap(result);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}
