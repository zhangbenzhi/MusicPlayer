package com.rdc.musicplayer.musicplayer.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rdc.musicplayer.musicplayer.R;
import com.rdc.musicplayer.musicplayer.adapter.SongAdapter;
import com.rdc.musicplayer.musicplayer.adapter.SongDetailBean;
import com.rdc.musicplayer.musicplayer.base.BaseActivity;
import com.rdc.musicplayer.musicplayer.bean.Music;
import com.rdc.musicplayer.musicplayer.bean.SearchBean;
import com.rdc.musicplayer.musicplayer.utils.GsonUtil;
import com.rdc.musicplayer.musicplayer.utils.OkHttpResultCallback;
import com.rdc.musicplayer.musicplayer.utils.OkHttpUtil;
import com.rdc.musicplayer.musicplayer.utils.PlayMusicUtil;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;

public class SearchMusicActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et)
    EditText et;
    @BindView(R.id.search)
    ImageView iv;
    @BindView(R.id.loading)
    View loading;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    SongAdapter songAdapter;
    ArrayList<SearchBean.SearchResult.SearchSong> searchSongs = new ArrayList<>();

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_search_music;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        songAdapter = new SongAdapter(searchSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songAdapter);
    }

    @Override
    protected void initListener() {
        iv.setOnClickListener(this);
        songAdapter.setOnSongItemClickListener(new SongAdapter.OnSongItemClickListener() {
            @Override
            public void onClickItem(final String songId, final String name) {
                loading.setVisibility(View.VISIBLE);
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                detail(name, songId, false);
                            }
                        }
                ).start();
            }

            @Override
            public void onDownLoad(final String songId, final String name) {
                loading.setVisibility(View.VISIBLE);
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                detail(name, songId, true);
                            }
                        }
                ).start();
            }
        });
    }

    private void detail(String songName, String songId, final boolean isDownload) {
        final String url = "https://api.imjad.cn/cloudmusic/?type=song&id=" + songId;
        OkHttpUtil.getInstance().getAsync(url, new OkHttpResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(final byte[] bytes) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                            String s = OkHttpUtil.getInstance().streamToString(new ByteArrayInputStream(bytes));
                            SongDetailBean songDetailBean = GsonUtil.getGson().fromJson(s, SongDetailBean.class);
                            String songUrl = songDetailBean.data.get(0).url;
                            if (isDownload) {
                                downLoad(songUrl, songDetailBean.data.get(0).name);
                            } else {
                                PlayMusicUtil.CURRENT_MUSIC_POSITION = 0;
                                PlayMusicUtil.CURRENT_MUSIC_LIST = new ArrayList<>();
                                Music music = new Music();
                                music.setDownUrl(songUrl);
                                music.setUrl(songUrl);
                                PlayMusicUtil.CURRENT_MUSIC_LIST.add(music);
                                PlayMusicUtil.CURRENT_MUSIC = PlayMusicUtil.CURRENT_MUSIC_LIST.get(PlayMusicUtil.CURRENT_MUSIC_POSITION);
                                PlayMusicUtil.getInstance().play();
                            }
                            Log.e("TAG", songUrl);
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", e.getMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(et.getText().toString())) {
            loading.setVisibility(View.VISIBLE);
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            search(et.getText().toString());
                        }
                    }
            ).start();
        } else {
            Toast.makeText(this, "请输入要搜索的歌曲", Toast.LENGTH_SHORT).show();
        }
    }

    //搜索:
    private void search(String text) {
        String url = "https://api.imjad.cn/cloudmusic/?type=search&search_type=1&s=" + text;
        OkHttpUtil.getInstance().getAsync(url, new OkHttpResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
            }

            @Override
            public void onResponse(final byte[] bytes) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                            String s = OkHttpUtil.getInstance().streamToString(new ByteArrayInputStream(bytes));
                            SearchBean searchBean = GsonUtil.getGson().fromJson(s, SearchBean.class);
                            searchSongs.clear();
                            searchSongs.addAll(searchBean.result.songs);
                            songAdapter.notifyDataSetChanged();
                            Log.e("TAG", searchBean.toString());
                        }
                    });
                } catch (Exception e) {
                    Log.e("TAG", e.getMessage());
                }
            }
        });
    }

    public void downLoad(String downLoadUrl, String name) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downLoadUrl));
        //设置在什么网络情况下进行下载
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //设置通知栏标题
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle("下载");
        request.setDescription("音乐正在下载");
        request.setAllowedOverRoaming(false);
        //设置文件存放目录
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MUSIC, name + ".mp3");
        DownloadManager systemService = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        systemService.enqueue(request);
    }
}
