package com.androidmediacode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        gridView = (GridView) findViewById(R.id.video_grid);
        videoAdapter = new VideoAdapter(VideoActivity.this, list);
        gridView.setAdapter(videoAdapter);
        getVideoFile(list, new File(videoDrict));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = list.get(position).getPath();
                Intent openVideo = new Intent(Intent.ACTION_VIEW);
                openVideo.setDataAndType(Uri.parse(path), "video/*");
                startActivity(openVideo);
            }
        });
    }

    private GridView gridView;

    private VideoAdapter videoAdapter;

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();

    private String videoDrict = Environment.getExternalStorageDirectory() + "/MediaCodecVideo";

    private ArrayList<VideoInfo> list = new ArrayList<>();

    private void getVideoFile(final List<VideoInfo> list, File file) {// 获得视频文件

        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")) {
                        VideoInfo vi = new VideoInfo();
                        vi.setDisplayName(file.getName());
                        vi.setPath(file.getAbsolutePath());
                        list.add(vi);
                        Log.e("zhengheng", "" + file.getAbsolutePath() + ":" + vi.toString());
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });
        videoAdapter.notifyDataSetChanged();
//        for (VideoInfo videoInfo : list) {
//            Bitmap bitmap = BitmapUtil.getVideoThumbNail(videoInfo.getPath());
//            bitmaps.add(bitmap);
//        }
    }

    class VideoAdapter extends BaseAdapter {

        private ArrayList<VideoInfo> bitmaps = new ArrayList<>();
        private Context ctx;

        public VideoAdapter(Context ctx, ArrayList<VideoInfo> list) {
            this.ctx = ctx;
            this.bitmaps = list;
        }

        @Override
        public int getCount() {
            Log.e("VideoActivity", "getCount" + bitmaps.size());
            return bitmaps.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = null;
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ctx).inflate(R.layout.item, null);
                holder.img = (ImageView) convertView.findViewById(R.id.video_bitmap);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Log.e("videoPath",""+bitmaps.get(position).getPath());
            Glide.with(ctx).load(bitmaps.get(position).getPath()).diskCacheStrategy(DiskCacheStrategy.RESULT).crossFade()
                    .centerCrop().override(150, 150).into(holder.img);
//            holder.img.setImageBitmap(bitmaps.get(position));
            Log.e("VideoActivity", "getView");
            return convertView;
        }


        class ViewHolder {
            ImageView img;
        }
    }
}
