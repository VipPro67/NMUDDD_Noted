package com.picassos.noted.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.picassos.noted.R;
import com.picassos.noted.utils.Helper;

import java.util.Objects;

public class ViewAttachedVideoActivity extends AppCompatActivity {

    PlayerView playerView;
    ProgressBar progressBar;
    ImageView fullScreen;
    SimpleExoPlayer simpleExoPlayer;
    boolean flag = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_view_attached_video);

        // make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // player view
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progress_bar);
        fullScreen = playerView.findViewById(R.id.bt_fullscreen);

        // load control
        LoadControl loadControl = new DefaultLoadControl();
        // bandwidth meter
        BandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        // track selector
        TrackSelector defaultTrackSelector = new DefaultTrackSelector(
                new AdaptiveTrackSelection.Factory(defaultBandwidthMeter)
        );
        // simple exo player
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                ViewAttachedVideoActivity.this, defaultTrackSelector, loadControl
        );
        // create data spec
        DataSpec dataSpec = new DataSpec(Uri.parse(getIntent().getStringExtra("video_path")));
        // data source
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }
        // data source factory
        DataSource.Factory factory = () -> fileDataSource;
        // media source
        MediaSource media_source = new ExtractorMediaSource(
                fileDataSource.getUri(), factory, new DefaultExtractorsFactory(), null, null
        );
        // set player
        playerView.setPlayer(simpleExoPlayer);
        // keep screen on
        playerView.setKeepScreenOn(true);
        // prepare media
        simpleExoPlayer.prepare(media_source);
        // play video when ready
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // check condition
                if (playbackState == Player.STATE_BUFFERING) {
                    // when buffering
                    // show progress bar
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        // full screen button
        fullScreen.setOnClickListener(v -> {
            // check condition
            if (flag) {
                // when flag is true
                // set enter full screen image
                fullScreen.setImageDrawable(getResources().getDrawable(R.drawable.icon_full_screen));
                // set portrait orientation
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                // show toolbar
                findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
                // set flag value to false
                flag = false;
            } else {
                // when flag is false
                // set exit full screen image
                fullScreen.setImageDrawable(getResources().getDrawable(R.drawable.icon_normal_screen));
                // set landscape orientation
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // hide toolbar
                findViewById(R.id.toolbar).setVisibility(View.GONE);
                // set flag value to true
                flag = true;
            }
        });

        // request remove video
        findViewById(R.id.note_video_remove).setOnClickListener(v -> {
            if (getIntent().getStringExtra("video_type") == null) {
                Intent intent = new Intent();
                intent.putExtra("request", "remove_video");
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        });

        // check video request type
        if (getIntent().getStringExtra("view_type") != null) {
            if (Objects.equals(getIntent().getStringExtra("view_type"), "view")) {
                findViewById(R.id.note_video_remove).setVisibility(View.GONE);
            }
        }

        // remove video tooltip
        findViewById(R.id.note_video_remove).setOnLongClickListener(v -> {
            Toast.makeText(this, getString(R.string.remove_video), Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop video when ready
        simpleExoPlayer.setPlayWhenReady(false);
        // get playback state
        simpleExoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // play video when ready
        simpleExoPlayer.setPlayWhenReady(true);
        // get playback state
        simpleExoPlayer.getPlaybackState();
    }
}