package com.bignerdranch.android.youtubedataapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoFragment extends Fragment {

    public static final String ARGS_VIDEO_ITEM_ID = "args_video_item_id";

    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mLikesTextView;
    private TextView mGoalTextView;
    private Button mGoalButton;
    private Button mOriginButton;
    private VideoItem mVideoItem;

    public static VideoFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(ARGS_VIDEO_ITEM_ID, id);
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        long id = getArguments().getLong(ARGS_VIDEO_ITEM_ID);
        mVideoItem = DataLab.get(getActivity()).getVideoById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.video_item_fragment, container, false);

        mImageView = v.findViewById(R.id.list_item_likes_goal_text_view);

        mTitleTextView = v.findViewById(R.id.list_item_title_text_view);

        mLikesTextView = v.findViewById(R.id.list_item_likes_text_view);

        mGoalTextView = v.findViewById(R.id.list_item_likes_goal_text_view);

        mGoalButton = v.findViewById(R.id.video_fragment_goal_button);
        mGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mOriginButton = v.findViewById(R.id.video_fragment_origin_button);
        mOriginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return v;
    }

}
