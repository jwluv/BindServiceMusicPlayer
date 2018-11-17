package com.example.edu.bindservicemusicplayer;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.MyViewHolder> {

    MusicPlayer musicPlayer;
    ArrayList<HashMap<String,Object>> arrayList = null;
    public PlayListAdapter(MusicPlayer musicPlayer, ArrayList<HashMap<String,Object>> arrayList) {
        this.musicPlayer = musicPlayer;
        this.arrayList = new ArrayList<HashMap<String, Object>>();
        this.arrayList = arrayList;
    }
//    public void addItem(int position, HashMap<String,Object> hashMap){
    public void addItem(HashMap<String,Object> hashMap){
        this.arrayList.add(hashMap);
        notifyItemInserted(this.arrayList.size());
    }
    public void updateItem(int position, HashMap<String,Object> hashMap){
        this.arrayList.remove(position);
        notifyDataSetChanged();
        this.arrayList.add(position, hashMap);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflate = LayoutInflater.from(viewGroup.getContext());
        View view = inflate.inflate(R.layout.playlist_layout, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        HashMap<String,Object> hashMap = arrayList.get(i);
        myViewHolder.textViewTitle.setText((String)hashMap.get("title"));
        myViewHolder.textViewDetail.setText((String)hashMap.get("artist"));
        myViewHolder.itemImage.setImageResource((int) hashMap.get("image"));


    }

    public void loadPlayingImage(int position) {
        for(int i=0; i<arrayList.size(); i++) {

            HashMap<String, Object> hashMap = arrayList.get(i);
            HashMap<String, Object> hashMapTemp = new HashMap<String, Object>();
            hashMapTemp.put("title", (String) hashMap.get("title"));
            hashMapTemp.put("artist", (String) hashMap.get("artist"));

            if(position != -1) {
                if (position == i)
                    hashMapTemp.put("image", R.drawable.playing);
                else
                    hashMapTemp.put("image", R.drawable.not_playing);
            }
            else
                hashMapTemp.put("image", R.drawable.not_playing);

            updateItem(i, hashMapTemp);
        }
    }

    public void loadPauseImage(int position) {

            HashMap<String, Object> hashMap = arrayList.get(position);
            HashMap<String, Object> hashMapTemp = new HashMap<String, Object>();
            hashMapTemp.put("title", (String) hashMap.get("title"));
            hashMapTemp.put("artist", (String) hashMap.get("artist"));
            hashMapTemp.put("image", R.drawable.play);

            updateItem(position, hashMapTemp);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void removeItem(int position){
        this.arrayList.remove(position);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ConstraintLayout item_playlist;
        ImageView itemImage;
        TextView textViewTitle, textViewDetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_playlist = itemView.findViewById(R.id.item_playlist);
            itemImage = itemView.findViewById(R.id.item_image);
            textViewTitle = itemView.findViewById(R.id.item_title);
            textViewDetail = itemView.findViewById(R.id.item_detail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            musicPlayer.play(position, 0);
//            item_playlist.setBackgroundColor(Color.parseColor("#e6ffff"));

            loadPlayingImage(position);
        }
    }
}


