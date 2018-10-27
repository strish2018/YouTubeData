package com.bignerdranch.android.youtubedataapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class ListFragment extends Fragment {

    public static final int DIALOG_REQUEST_CODE = 0;

    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;

    private DataLab mDataLab;

    public static ListFragment newInstance(){
        ListFragment fragment = new ListFragment();
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        mDataLab = DataLab.get(getActivity());

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateVideosInfo();
        updateUI();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_video:
                openDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openDialog(){
        Log.i("CHECK", "check");
        VideoDialog dialog = VideoDialog.newInstance();
        dialog.setTargetFragment(this, DIALOG_REQUEST_CODE);
        dialog.show(getActivity().getSupportFragmentManager(), "dialog");
    }

    private void checkVideoData(String s, VideoItem v){
        new LongOperation(s, v).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == DIALOG_REQUEST_CODE){
            String link = data.getStringExtra(VideoDialog.LINK);
            checkVideoData(link, null);
        }
    }

    private void updateVideosInfo(){
        List<VideoItem> videos = mDataLab.getVideos();
        if(videos.size() > 0){
            for (int i = 0; i < videos.size(); i++){
                Log.i("DEBUG", videos.get(i).getTitle());
                Log.i("DEBUG", videos.get(i).getLink() + "   LINK");
                checkVideoData(videos.get(i).getLink(), videos.get(i));
            }
        }
    }

    private void updateUI(){
        List<VideoItem> videos = mDataLab.getVideos();
        if (mAdapter == null) {
            mAdapter = new VideoAdapter(videos);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setVideos(videos);
//            mAdapter.notifyItemChanged(mSelectedCrimePosition);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private TextView mTitleTextView;
        private TextView mLikesTextView;
        private TextView mGoalTextView;
        private VideoItem mVideoItem;

        public VideoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mImageView = itemView.findViewById(R.id.image_view_list);
            mTitleTextView = itemView.findViewById(R.id.list_item_title_text_view);
            mLikesTextView = itemView.findViewById(R.id.list_item_likes_text_view);
            mGoalTextView = itemView.findViewById(R.id.list_item_likes_goal_text_view);
        }

        public void bindVideo(VideoItem video) {
            mVideoItem = video;
            Picasso.get().load(mVideoItem.getThumbnailUrl()).fit().centerCrop().into(mImageView);
            mTitleTextView.setText(mVideoItem.getTitle());
            mLikesTextView.setText("Likes count: " +String.valueOf(mVideoItem.getLikesCount()));
        }

        @Override
        public void onClick(View v) {
        }
    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoHolder> {
        private List<VideoItem> mVideos;

        public VideoAdapter(List<VideoItem> videos) {
            mVideos = videos;
        }

        @Override
        public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_2, parent, false);
            return new VideoHolder(view);
        }
        @Override
        public void onBindViewHolder(VideoHolder holder, int position) {
            VideoItem video = mVideos.get(position);
            holder.bindVideo(video);
        }

        @Override
        public int getItemCount() {
            return mVideos.size();
        }

        public void setVideos(List<VideoItem> videos) {
            mVideos = videos;
        }

    }

    private class LongOperation extends AsyncTask<Void, Void, VideoItem> {

        String mString;
        VideoItem mItem;

        public LongOperation(String s, VideoItem item){
            mString = s;
            mItem = item;
        }

        @Override
        protected VideoItem doInBackground(Void... params) {
            try {
                return new AsyncTest().getVideoByLink(mString);
            } catch (IOException ioe) {
                Log.e("DEBUG", "Failed to fetch items", ioe);
            }
            return null;
        }

        @Override
        protected void onPostExecute(VideoItem videoItem) {
            if(mItem == null){
                mDataLab.addVideo(videoItem);
                mAdapter.notifyItemInserted(mDataLab.getVideos().size() - 1);
                updateUI();
            } else{
                mItem.updateItem(videoItem);
                mDataLab.updateVideo(mItem);
                updateUI();
            }
//            Log.i("DEBUG", videoItem.getTitle() + "     " + videoItem.getLikesCount());
        }
    }

}
