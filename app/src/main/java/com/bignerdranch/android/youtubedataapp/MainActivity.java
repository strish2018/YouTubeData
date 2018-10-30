package com.bignerdranch.android.youtubedataapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ListFragment.Callbacks, VideoFragment.Callbacks {

    ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFragment = ListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFragment).commit();
    }

    @Override
    public void onVideoSelected(VideoItem videoItem) {
        Intent i = VideoActivity.newIntent(this, videoItem.getId());
        startActivity(i);
    }

    @Override
    public void onVideoItemUpdated(VideoItem videoItem) {
        listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
