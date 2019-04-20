package com.rdc.musicplayer.musicplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rdc.musicplayer.musicplayer.R;
import com.rdc.musicplayer.musicplayer.bean.SearchBean;

import java.util.ArrayList;

/**
 * 歌曲列表适配器
 */
public class SongAdapter extends RecyclerView.Adapter {

    private ArrayList<SearchBean.SearchResult.SearchSong> searchSongs = new ArrayList<>();
    private OnSongItemClickListener onSongItemClickListener;

    public SongAdapter(ArrayList<SearchBean.SearchResult.SearchSong> searchSongs) {
        this.searchSongs = searchSongs;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_song, null);
        return new SearchSongsViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            SearchSongsViewHolder searchSongsViewHolder = (SearchSongsViewHolder) holder;
            Glide.with(holder.itemView.getContext()).load(searchSongs.get(position).al.picUrl).into(((SearchSongsViewHolder) holder).iv);
            searchSongsViewHolder.tvSingerName.setText(searchSongs.get(position).ar.get(0).name);
            searchSongsViewHolder.tvSongName.setText(searchSongs.get(position).name);
            searchSongsViewHolder.btn_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSongItemClickListener != null) {
                        onSongItemClickListener.onClickItem(searchSongs.get(position).id,searchSongs.get(position).ar.get(0).name);
                    }
                }
            });
            searchSongsViewHolder.btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onSongItemClickListener != null) {
                        onSongItemClickListener.onDownLoad(searchSongs.get(position).id,searchSongs.get(position).ar.get(0).name);
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return searchSongs.size();
    }

    public static class SearchSongsViewHolder extends RecyclerView.ViewHolder {

        TextView tvSongName;
        TextView tvSingerName;
        ImageView iv;
        Button btn_play;
        Button btn_download;

        public SearchSongsViewHolder(View itemView) {
            super(itemView);

            iv = (ImageView) itemView.findViewById(R.id.iv);
            tvSingerName = (TextView) itemView.findViewById(R.id.singer_name);
            tvSongName = (TextView) itemView.findViewById(R.id.song_name);
            btn_play = (Button) itemView.findViewById(R.id.bt_play);
            btn_download = (Button) itemView.findViewById(R.id.bt_download);
        }
    }

    public interface OnSongItemClickListener {
        void onClickItem(String songId,String songName);
        void onDownLoad(String songId,String songName);
    }

    public void setOnSongItemClickListener(OnSongItemClickListener onSongItemClickListener) {
        this.onSongItemClickListener = onSongItemClickListener;
    }
}
