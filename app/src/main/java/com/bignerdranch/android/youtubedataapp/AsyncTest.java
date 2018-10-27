package com.bignerdranch.android.youtubedataapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AsyncTest {

    public static final String API_KEY = "AIzaSyDNOu8gOihV-AHN28qniLqp7YQ9UTvoTz0";
    String videoUrl = "https://www.youtube.com/watch?v=YHo8jMVvQVo&t=891s";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public VideoItem getVideoByLink(String s) throws IOException {
        String id = getIdFromUrl(videoUrl);
        String queryStats = "https://www.googleapis.com/youtube/v3/videos?part=statistics&id="+id+"&key="+API_KEY;
        String queryInfo = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id="+id+"&key="+API_KEY;
        Log.i("DEBUG", getUrlString(queryInfo));
        VideoItem videoItem = new VideoItem(s);
        videoItem.setGoal(0);
        try {
            JSONObject jsonBody = new JSONObject(getUrlString(queryStats));
            parseItemLikes(videoItem, jsonBody);
            jsonBody = new JSONObject(getUrlString(queryInfo));
            parseItemTitle(videoItem, jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videoItem;
    }

    private String getIdFromUrl(String s){
        String link = s;
        Log.i("DEBUG", link.substring(link.lastIndexOf("v=") + 2));
        return link.substring(link.lastIndexOf("v=") + 2);
    }

    private void parseItemLikes(VideoItem item, JSONObject jsonBody) throws IOException, JSONException {
        JSONArray items = jsonBody.getJSONArray("items");
        JSONObject statistics = items.getJSONObject(0).getJSONObject("statistics");
        item.setLikesCount(statistics.getInt("likeCount"));
    }

    private void parseItemTitle(VideoItem item, JSONObject jsonBody) throws IOException, JSONException {
        JSONArray items = jsonBody.getJSONArray("items");
        JSONObject videoJsonObject = items.getJSONObject(0).getJSONObject("snippet");
        item.setTitle(videoJsonObject.getString("title"));
        videoJsonObject = videoJsonObject.getJSONObject("thumbnails").getJSONObject("high");
        item.setThumbnailUrl(videoJsonObject.getString("url"));
        Log.i("DEBUG", item.getThumbnailUrl());
    }

}
