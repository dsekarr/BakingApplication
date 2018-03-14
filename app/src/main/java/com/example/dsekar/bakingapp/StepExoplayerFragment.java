package com.example.dsekar.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.dsekar.bakingapp.Data.Step;
import com.example.dsekar.bakingapp.Utils.RecipeUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepExoplayerFragment extends android.support.v4.app.Fragment implements ExoPlayer.EventListener {

    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;

    @BindView(R.id.step_description)
    TextView stepDescription;

    @BindView(R.id.step_description_scroll_view)
    ScrollView scrollView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private SimpleExoPlayer mPlayer;
    private static final String Step = "clicked_step";
    private static final String SELECTED_POSITION = "selected_position";

    private static final String SELECTED_STEP = "step_value";

    long position = C.TIME_UNSET;
    private Step mStep;

    private onConnectivityChangeReceiver connectivityChangeReceiver;
    private IntentFilter intentFilter;

    public StepExoplayerFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_exoplayer, container, false);
        ButterKnife.bind(this, rootView);
        if (savedInstanceState != null) {
            position = savedInstanceState.getLong(SELECTED_POSITION, C.TIME_UNSET);
            mStep = savedInstanceState.getParcelable(SELECTED_STEP);
        } else {
            getIntentData();
        }
        connectivityChangeReceiver = new onConnectivityChangeReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().setTitle(mStep.getShortDescription());
        if (mStep != null && mStep.getDescription() != null) {
            stepDescription.setVisibility(View.VISIBLE);
            stepDescription.setText(mStep.getDescription());
        }
        loadData();
        return rootView;
    }

    private void loadData() {
        if (mStep != null && !mStep.getVideoURL().isEmpty()) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(mStep);
        } else {
            mPlayerView.setVisibility(View.GONE);
        }
    }


    private void initializePlayer(Step step) {
        Uri uri = Uri.parse(step.getVideoURL());
        if (mPlayer == null) {
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, new DefaultLoadControl());

            mPlayerView.setPlayer(mPlayer);


            mPlayer.setPlayWhenReady(true);
            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                    Util.getUserAgent(getActivity(),
                            "mediaPlayerSample"), bandwidthMeter);

            MediaSource mediaSource = new ExtractorMediaSource(uri,
                    mediaDataSourceFactory, extractorsFactory, null, null);
            if (position != C.TIME_UNSET) {
                mPlayer.seekTo(position);
            }
            mPlayer.prepare(mediaSource);
            mPlayer.addListener(this);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL | AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                stepDescription.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
            } else {
                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                stepDescription.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            position = mPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    private void getIntentData() {
        if (getActivity().getIntent() != null) {
            if (getActivity().getIntent().hasExtra(Step)) {
                mStep = getActivity().getIntent().getParcelableExtra(Step);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        getActivity().unregisterReceiver(connectivityChangeReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SELECTED_POSITION, position);
        outState.putParcelable(SELECTED_STEP, mStep);
    }

    public void getStepData(Step step) {
        this.mStep = step;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_BUFFERING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(connectivityChangeReceiver, intentFilter);
    }

    public class onConnectivityChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (RecipeUtils.isConnectedToInternet() && mPlayer == null && !mStep.getVideoURL().isEmpty()) {
                RecipeUtils.setSnackBar(getActivity().findViewById(R.id.exoplayer_layout), getResources().getString(R.string.online), Snackbar.LENGTH_SHORT, getResources().getColor(R.color.green));
                loadData();
            } else if(!RecipeUtils.isConnectedToInternet() && mPlayer!= null){
                releasePlayer();
                RecipeUtils.setSnackBar(getActivity().findViewById(R.id.exoplayer_layout), getResources().getString(R.string.no_connection),
                        Snackbar.LENGTH_INDEFINITE, getResources().getColor(R.color.black));
            }
        }
    }
}
