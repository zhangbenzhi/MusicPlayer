package com.rdc.musicplayer.musicplayer.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import com.rdc.musicplayer.musicplayer.R;
import com.rdc.musicplayer.musicplayer.base.BaseActivity;
import com.rdc.musicplayer.musicplayer.bean.Lyrics;
import com.rdc.musicplayer.musicplayer.bean.Music;
import com.rdc.musicplayer.musicplayer.contract.IGetMusicLrcContract;
import com.rdc.musicplayer.musicplayer.contract.ILoadLrcContract;
import com.rdc.musicplayer.musicplayer.decorator.FastBulrBitmapTransformation;
import com.rdc.musicplayer.musicplayer.listener.OnStartPlayListener;
import com.rdc.musicplayer.musicplayer.listener.RotateAnimatorUpdateListener;
import com.rdc.musicplayer.musicplayer.presenter.GetMusicLrcPresenterImpl;
import com.rdc.musicplayer.musicplayer.presenter.LoadLrcPresenterImpl;
import com.rdc.musicplayer.musicplayer.service.MusicPlayService;
import com.rdc.musicplayer.musicplayer.utils.FastBulr;
import com.rdc.musicplayer.musicplayer.utils.GetCurLrcUtil;
import com.rdc.musicplayer.musicplayer.utils.GetThumbnailUtil;
import com.rdc.musicplayer.musicplayer.utils.LrcUtil;
import com.rdc.musicplayer.musicplayer.utils.PlayMusicUtil;
import com.rdc.musicplayer.musicplayer.utils.TimeFormatUtil;
import com.rdc.musicplayer.musicplayer.view.LrcView;
import com.rdc.musicplayer.musicplayer.view.MarqueeTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayMusicActivity extends BaseActivity implements ILoadLrcContract.View, IGetMusicLrcContract.View, OnStartPlayListener {

    @BindView(R.id.rl_play_music)
    LinearLayout mRlPlayMusic;
    @BindView(R.id.mtv_music_name)
    MarqueeTextView mMtvMusicName;
    @BindView(R.id.tb_title)
    Toolbar mTbTitle;
    @BindView(R.id.tv_singer_name)
    TextView mTvSingerName;
    @BindView(R.id.civ_music_album)
    CircleImageView mCivMusicAlbum;
    @BindView(R.id.lv_music_lrc)
    LrcView mLvMusicLrc;
    @BindView(R.id.iv_action_play)
    ImageView mIvActionPlay;
    @BindView(R.id.iv_action_play_pre)
    ImageView mIvActionPlayPre;
    @BindView(R.id.iv_action_play_mode)
    ImageView mIvActionPlayMode;
    @BindView(R.id.iv_action_play_next)
    ImageView mIvActionPlayNext;
    @BindView(R.id.tv_current_time)
    TextView mTvCurrentTime;
    @BindView(R.id.tv_total_time)
    TextView mTvTotalTime;
    @BindView(R.id.sb_music_progress)
    SeekBar mSbMusicProgress;

    private List<Lyrics> mLyricsList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (PlayMusicUtil.CURRENT_STATE == PlayMusicUtil.PLAY) {
                int currentTime = MusicPlayService.sMediaPlayer.getCurrentPosition();
                mTvCurrentTime.setText(TimeFormatUtil.format(currentTime / 1000));
                mSbMusicProgress.setProgress(currentTime);
                if (mLyricsList != null) {
                    String curLrc = GetCurLrcUtil.getCurLrc(MusicPlayService.sMediaPlayer, mLyricsList);
                }
                mHandler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };

    private ObjectAnimator mObjectAnimator;
    private RotateAnimatorUpdateListener mRotateAnimatorUpdateListener;
    private LoadLrcPresenterImpl mLoadLrcPresenter;
    private GetMusicLrcPresenterImpl mGetMusicLrcPresenter;

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_play_music;
    }

    @Override
    protected void initData() {
        mObjectAnimator = ObjectAnimator.ofFloat(mCivMusicAlbum, "rotation", 0f, 360f);
        mObjectAnimator.setDuration(15000);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mObjectAnimator.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimator.setRepeatCount(-1);
        mRotateAnimatorUpdateListener = new RotateAnimatorUpdateListener(mObjectAnimator);
        mObjectAnimator.addUpdateListener(mRotateAnimatorUpdateListener);
        mObjectAnimator.start();
        mLoadLrcPresenter = new LoadLrcPresenterImpl(this);
        mGetMusicLrcPresenter = new GetMusicLrcPresenterImpl(this);
        fetchLrc();
    }

    private void fetchLrc() {
        Music music = PlayMusicUtil.CURRENT_MUSIC;
        if (music.getSongId() > 0) {
            mGetMusicLrcPresenter.getLyrics(music.getSongId());
        } else {
            mLoadLrcPresenter.getLyrics();
        }
    }

    @Override
    protected void initView() {
        mTbTitle.setTitle("");
        setSupportActionBar(mTbTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTbTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateAlbum();
        update();
    }

    private void updateAlbum() {
        Music music = PlayMusicUtil.CURRENT_MUSIC;
        if (music.getAlbumBig() != null) {
            if (!this.isFinishing()) {
                Glide.with(this).load(music.getAlbumBig()).into(mCivMusicAlbum);
                Glide.with(this).load(music.getAlbumBig())
                        .apply(new RequestOptions().transform(new FastBulrBitmapTransformation()))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                mRlPlayMusic.setBackground(resource);
                            }
                        });
            }
        } else {
            Bitmap bit = GetThumbnailUtil.createAlbumThumbnail(music.getUrl());
            if (bit == null) {
                bit = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_album);
            } else {
                mCivMusicAlbum.setImageDrawable(new BitmapDrawable(bit));
            }
            Matrix matrix = new Matrix();
            matrix.postScale(0.1f, 0.1f);
            Bitmap overlay = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(), bit.getHeight(), matrix, true);
            Bitmap bitmap = FastBulr.doBlur(overlay, 5, true);
            Drawable drawable = new BitmapDrawable(bitmap);
            mRlPlayMusic.setBackground(drawable);
        }
    }

    @Override
    protected void initListener() {
        PlayMusicUtil.getInstance().setOnStartPlayListener(this);
        mSbMusicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicPlayService.sMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicPlayService.sMediaPlayer.pause();
                PlayMusicUtil.CURRENT_STATE = PlayMusicUtil.PAUSE;
                update();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayMusicUtil.getInstance().pause();
                update();
            }
        });
    }

    @OnClick({R.id.iv_action_play, R.id.iv_action_play_pre, R.id.iv_action_play_mode, R.id.iv_action_play_next, R.id.iv_action_evaluate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_action_play:
                PlayMusicUtil.getInstance().pause();
                update();
                break;
            case R.id.iv_action_play_pre:
                PlayMusicUtil.getInstance().playPre();
                fetchLrc();
                updateAlbum();
                update();
                break;
            case R.id.iv_action_play_mode:
                PlayMusicUtil.getInstance().changeMode();
                switch (PlayMusicUtil.CURRENT_MODE) {
                    case PlayMusicUtil.MODE_LOOP:
                        mIvActionPlayMode.setImageResource(R.drawable.iv_play_loop);
                        break;
                    case PlayMusicUtil.MODE_RANDOM:
                        mIvActionPlayMode.setImageResource(R.drawable.iv_play_random);
                        break;
                    case PlayMusicUtil.MODE_REPEAT:
                        mIvActionPlayMode.setImageResource(R.drawable.iv_play_repeat);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.iv_action_play_next:
                PlayMusicUtil.getInstance().playNext();
                updateAlbum();
                fetchLrc();
                update();
                break;
            case R.id.iv_action_evaluate:
                //评论：
                Intent intent = new Intent(this, EvaluateActivity.class);
                intent.putExtra("key", songName);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onGetLyricsSuccess(String lrc) {
        if (lrc != null) {
            mLvMusicLrc.setLrc(lrc);
            mLvMusicLrc.setPlayer(MusicPlayService.sMediaPlayer);
            mLvMusicLrc.init();
        }
    }

    @Override
    public void onGetLyricsFailed(String response) {

    }

    private String songName;

    private void update() {
        if (PlayMusicUtil.CURRENT_STATE == PlayMusicUtil.PLAY) {
            mIvActionPlay.setImageResource(R.drawable.ic_action_pause);
        } else {
            mIvActionPlay.setImageResource(R.drawable.ic_action_playing);
        }
        Music music = PlayMusicUtil.CURRENT_MUSIC;
        try {
            this.songName = music.getSongName().replace(".", "_");
        } catch (Exception e) {
        }
        mMtvMusicName.setText(music.getSongName());
        mTvSingerName.setText(music.getSingerName());
        mHandler.sendEmptyMessage(0);
        if (music.getSeconds() < 0) {
            mTvTotalTime.setText("");
            mSbMusicProgress.setMax(220 * 1000);
        } else {
            mTvTotalTime.setText(TimeFormatUtil.format(music.getSeconds()));
            mSbMusicProgress.setMax(music.getSeconds() * 1000);
        }
    }

    @Override
    public void onGetOnlineLyricsSuccess(String lrc) {
        mLyricsList = LrcUtil.parseStr2List(lrc);
        mLvMusicLrc.setLrc(lrc);
        mLvMusicLrc.setPlayer(MusicPlayService.sMediaPlayer);
        mLvMusicLrc.init();
    }

    @Override
    public void onGetOnlineLyricsFailed(String response) {

    }

    @Override
    public void onCompletedStartPlay() {
        update();
        updateAlbum();
        fetchLrc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Util.isOnMainThread() && !this.isFinishing()) {
            Glide.with(this).pauseRequests();
        }
    }
}
