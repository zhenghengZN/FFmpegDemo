package com.androidmediacode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.luck.picture.lib.adapter.GridImageAdapter;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.luck.picture.lib.util.FullyGridLayoutManager;
import com.yalantis.ucrop.dialog.SweetAlertDialog;
import com.yalantis.ucrop.entity.LocalMedia;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

/**
 * Created by zhengheng on 18/3/31.
 */
public class SelectActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private String floderPath;
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private EditText time;
    private CheckBox one_min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity);
//        TypefaceUtil.replaceSystemDefaultFont(this,"fonts/iconfont.ttf");
        loadFFMpegBinary();
        time = (EditText) findViewById(R.id.duraction);
        floderPath = StorageUtils.getOwnCacheDirectory(this, "/AAAA").toString();
        StorageUtils.getOwnCacheDirectory(this, "/MediaCodecVideo");
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(SelectActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        //设置item间距
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 20;
                outRect.bottom = 20;
            }
        });
        adapter = new GridImageAdapter(SelectActivity.this, onAddPicClickListener);
        adapter.setList(selectMedia);
        adapter.setSelectMax(maxSelect);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectType) {
                    case FunctionConfig.TYPE_IMAGE:
                        // 预览图片
                        PictureConfig.getInstance().externalPicturePreview(SelectActivity.this, position, selectMedia);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // 预览视频
                        if (selectMedia.size() > 0) {
                            PictureConfig.getInstance().externalPictureVideo(SelectActivity.this, selectMedia.get(position).getCompressPath());
                        }
                        break;
                }

            }
        });
        recyclerView.setAdapter(adapter);
        one_min = (CheckBox) findViewById(R.id.one_min);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        Log.e("typeface", "onCreate " + typeface);
        one_min.setTypeface(typeface);
        one_min.setOnCheckedChangeListener(this);

        final RadioGroup video_bg = (RadioGroup) findViewById(R.id.video_bg);
        video_bg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkId = video_bg.getCheckedRadioButtonId();
                switch (checkId) {
                    case R.id.white:
                        background = StorageUtils.getImageBackground(SelectActivity.this, StorageUtils.BACKGROUND_WITHE).getAbsolutePath();
                        break;
                    case R.id.black:
                        background = StorageUtils.getImageBackground(SelectActivity.this, StorageUtils.BACKGROUND_BLACK).getAbsolutePath();
                        break;
                }
            }
        });

//        final RadioGroup x_location = (RadioGroup) findViewById(R.id.x_loaction);
//        x_location.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
////                int checkID = x_location.getCheckedRadioButtonId();
//                switch (checkedId) {
//                    case R.id.left:
//                        align_offset = "(main_w)";
//                        finalLocation = "0";
//                        speed = "(w)";
//                        break;
//                    case R.id.center:
//                        align_offset = "(main_w)";
//                        finalLocation = "(main_w-w)/2";
//                        speed = "(main_w)";
//                        break;
//                    case R.id.right:
//                        align_offset = "(main_w)";
//                        finalLocation = "(main_w-w)";
//                        speed = "(w)";
//                        break;
//                }
//            }
//        });

        final RadioGroup y_location = (RadioGroup) findViewById(R.id.y_location);
        y_location.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                int checkID = y_location.getCheckedRadioButtonId();
                switch (checkedId) {
                    case R.id.top:
                        y_offset = "0";
                        speed = "(w)";
                        break;
                    case R.id.y_center:
                        y_offset = "(main_h-h)/2";
                        speed = "(main_w)";
                        break;
                    case R.id.bottom:
                        y_offset = "(main_h)";
                        speed = "(w)";
                        break;
                }
            }
        });

        final RadioGroup chooseType = (RadioGroup) findViewById(R.id.choose_type);
        chooseType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioButtonId = chooseType.getCheckedRadioButtonId();
                switch (checkedId) {
                    case R.id.type_image:
                        selectType = FunctionConfig.TYPE_IMAGE;
                        break;
                    case R.id.type_video:
                        selectType = FunctionConfig.TYPE_VIDEO;
                        break;
                }

            }
        });

        Button btn = (Button) findViewById(R.id.start);
        btn.setOnClickListener(SelectActivity.this);
    }

    private String y_offset = "0";
    private String align_offset = "(main_w)";
    private String finalLocation = "0";
    private String speed = "w";
    private String background;
    private int maxSelect = 100;
    private int selectType = FunctionConfig.TYPE_IMAGE;

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    FunctionOptions options = new FunctionOptions.Builder()
                            .setType(selectType)
                            .setCropMode(FunctionConfig.CROP_MODEL_DEFAULT)
                            .setCompress(false)
                            .setEnablePixelCompress(false)
                            .setEnableQualityCompress(false)
                            .setMaxSelectNum(maxSelect)
                            .setMinSelectNum(0)
                            .setSelectMode(FunctionConfig.MODE_MULTIPLE)
                            .setShowCamera(false)
                            .setEnablePreview(true)
                            .setEnableCrop(true)
                            .setCircularCut(false)
                            .setPreviewVideo(true)
                            .setCheckedBoxDrawable(R.drawable.select_cb)
                            .setCustomQQ_theme(0)
                            .setGif(false)
                            .setMaxB(102400)
                            .setPreviewColor(Color.WHITE)
                            .setCompleteColor(Color.GREEN)
                            .setPreviewBottomBgColor(Color.BLACK)
                            .setPreviewTopBgColor(Color.BLACK)
                            .setBottomBgColor(Color.BLACK)
                            .setGrade(Luban.CUSTOM_GEAR)
                            .setCheckNumMode(true)
                            .setCompressQuality(100)
                            .setImageSpanCount(4)
                            .setCompressFlag(2)
//                            .setCompressW(720)
//                            .setCompressH(1280)
                            .setThemeStyle(ContextCompat.getColor(SelectActivity.this, R.color.bar_grey))
                            .create();
                    PictureConfig.getInstance().init(options).openPhoto(SelectActivity.this, resultCallback);
                    break;
                case 1:
                    selectMedia.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia.addAll(resultList);
            Log.i("callBack_result", selectMedia.size() + "");
            for (LocalMedia media : resultList) {
                if (media.isCut() && !media.isCompressed()) {
                    String path = media.getCutPath();

                    try {
                        File file = new File(path);
                        StorageUtils.saveFile(file, floderPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    String path = media.getCompressPath();

                    try {
                        File file = new File(path);
                        StorageUtils.saveFile(file, floderPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String path = media.getPath();

                    try {
                        File file = new File(path);
                        StorageUtils.saveFile(file, floderPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();
            }
            background = StorageUtils.getImageBackground(SelectActivity.this, StorageUtils.BACKGROUND_WITHE).getAbsolutePath();
        }
    };


    private String videourl = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Screenshots/" + "e.mp4";
    private String videourl1 = Environment.getExternalStorageDirectory() + "/MediaCodecVideo/" + System.currentTimeMillis() + ".mp4";


    private boolean isChecked = false;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                isChecked = one_min.isChecked();
                if (time.getText() == null || time.getText().toString().isEmpty()) {
                    druation = 15;
                } else {
                    String s = time.getText().toString();
                    druation = Integer.valueOf(s);
                }
                sweetAlertDialog = new SweetAlertDialog(SelectActivity.this);
                sweetAlertDialog.show();
                imagToVideo();
                break;
        }
    }

    private double druation;
    private double sec_offset;

    private boolean getVideoNum() {
        boolean hasVideo = false;
        for (int i = 0; i < selectMedia.size(); i++) {
            LocalMedia localMedia = selectMedia.get(i);
            String Path = localMedia.getPath();
            if (Path.endsWith(".mp4")) {
                selectMedia.remove(i);
                selectMedia.add(localMedia);
                hasVideo = true;
            }
            if (Path.endsWith(".png")) {
                String fileName = new File(Path).getName();
                String toPath = StorageUtils.getBitMap(SelectActivity.this, fileName).getAbsolutePath();
                toPath = toPath.substring(0, toPath.length() - 4);
                Log.e("zhengheng", "" + toPath);
                toPath = toPath + ".jpg";
                Log.e("zhengheng1", "" + toPath);
                BitmapUtil.convertToJpg(Path, toPath);
                localMedia.setPath(toPath);
            }
        }
        return hasVideo;
    }

    private void imagToVideo() {
        if (selectMedia.size() <= 0) {
            return;
        }
        boolean hasVideo = getVideoNum();
        int pictureCount = 0;
        if (hasVideo) {
            pictureCount = selectMedia.size() - 1;
        } else {
            pictureCount = selectMedia.size();
        }

        String testImage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Screenshots/" + "ping20s.mp4";
        double stopTime = (druation - (pictureCount - 1) * 0.2) / pictureCount;
        final StringBuffer filter = new StringBuffer();
//        Bitmap bitmap = BitmapUtil.setImgSize(background, 1080, 1920);
//        StorageUtils.saveBitmapFile(bitmap);

        filter.append("-r 20 -loop 1 -i " + background + " -vf ");
        for (int i = 0; i < selectMedia.size(); i++) {
            filter.append("movie=" + selectMedia.get(i).getPath() + "[layer" + i + "];");
        }

        for (int i = 0; i < selectMedia.size(); i++) {
            filter.append("[layer" + i + "]scale=720:-1[pic" + i + "];");
        }

        if (isChecked) {
            filter.append("[in][pic0]overlay=x='if(gte(t," + 1 + "),(-(t-" + 1 + ")*(" + speed + "/0.2))," + finalLocation + ")':y=" + y_offset + "[movie0];");//",scale=1080:trunc(" + sizes.get(0) + "/2)*2[movie0];");//trunc(iw/2)*2:trunc(ih/2)*2
            stopTime = (druation - 1 - (pictureCount - 1) * 0.2) / (pictureCount - 1);
            sec_offset = 1;
            Log.e("zhengheng stopTime", stopTime + "");
        } else {
            filter.append("[in][pic0]overlay=x='if(gte(t," + stopTime + "),(-(t-" + stopTime + ")*(" + speed + "/0.2))," + finalLocation + ")':y=" + y_offset + "[movie0];");//",scale=1080:trunc(" + sizes.get(0) + "/2)*2[movie0];");
            sec_offset = stopTime;
        }
        for (int i = 1; i < selectMedia.size(); i++) {

            double startM = sec_offset + (i - 1) * stopTime + (i - 1) * 0.2;
            double sildeM = startM + 0.2;
            double endM = sildeM + stopTime;

            Log.e("zhengheng stopTime", i + " " + startM + " " + sildeM + " " + endM);

            if (i == selectMedia.size() - 1) {
                if (getVideoNum()) {
                    filter.append("[movie" + (i - 1) + "][pic" + i + "]overlay=x=0:y=0");
                } else {
                    filter.append("[movie" + (i - 1) + "][pic" + i + "]overlay=x='if(gte(t," + startM + "),if(lte(t," + sildeM + ")," + align_offset + "-(t-" + startM + ")*(" + speed + "/0.2)," + finalLocation + "),main_w+" + finalLocation + ")':y=" + y_offset);
                }
            } else {
                filter.append("[movie" + (i - 1) + "][pic" + i + "]overlay=x='if(gte(t," + startM + "),if(lte(t," + endM + "),if(gte(t," + sildeM + ")," + finalLocation + "," + align_offset + "-(t-" + startM + ")*(" + speed + "/0.2)),-(t-" + endM + ")*(" + speed + "/0.2)),main_w+" + finalLocation + ")':y=" + y_offset + "[movie" + i + "];");
            }
        }
        filter.append(" -threads 12 -pix_fmt yuv420p -vcodec libx264 -profile baseline -preset ultrafast -b:v 12000k -maxrate 6000k -minrate 6000k -bufsize 6000k -crf 34 -r:v 20 -s 720x1280 -aspect 9:16 -r 20 -t " + (int) druation + " -vframes " + ((int) druation * 20) + " -y " + videourl1);
        q = System.currentTimeMillis();

        Log.e("zhengheng filter", " " + filter.toString());

        String filter1 = "-r 15 -loop 1 -i " + background + " -vf [0:v]scale=720:-1[v0];movie=" + selectMedia.get(0).getPath() + "[pic0];movie=" + selectMedia.get(1).getPath() + "[pic1];" +
                "[pic0]scale=720:-1[scale0];[pic1]scale=720:-1[scale1];" +
                "[in][scale0]overlay=x=0:y=0[overlay0];[overlay0][scale1]overlay=x=0:y=400[overlay1];[v0][overlay1]overlay=x=0:y=0" +
                " -pix_fmt yuv420p -vcodec libx264 -profile baseline -b:v 12000k -maxrate 6000k -minrate 6000k -bufsize 6000k -crf 34 -r:v 15 -preset ultrafast -s 720x1280 -aspect 9:16 -r 15 -t 15 -vframes 225 -y " + videourl;

        EpEditor.execCmd(filter.toString(), 10, new FFmpegHandle() {
            @Override
            public void onSuccess() {
                super.onSuccess();
                e = System.currentTimeMillis();
                Log.e("FFmpeg_EpMedia", "time " + (e - q));
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(videourl1))));
                Looper.prepare();
                Toast.makeText(SelectActivity.this, "time:" + (e - q), Toast.LENGTH_SHORT).show();
                sweetAlertDialog.dismiss();
//                Intent openVideo = new Intent(Intent.ACTION_VIEW);
//                openVideo.setDataAndType(Uri.parse(videourl), "video/*");
//                startActivity(openVideo);
                Intent intent = new Intent(SelectActivity.this, VideoActivity.class);
                startActivity(intent);
                Looper.loop();
                Log.e("zhengheng filter", " " + filter.toString());

            }

            @Override
            public void onFailure() {
                super.onFailure();
                sweetAlertDialog.dismiss();
                Looper.prepare();
                Toast.makeText(SelectActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });

        Log.e("zhengheng filter", " " + filter.toString());
//        try {
//            ffmpeg.execute(filter.toString().split(" "), new FFmpegHandle() {
//                @Override
//                public void onSuccess(String s) {
//                    super.onSuccess(s);
//                    e = System.currentTimeMillis();
//                    Log.e("zhengheng", "time " + (e - q));
//                    Toast.makeText(SelectActivity.this, "time:" + (e - q), Toast.LENGTH_SHORT).show();
//                    sweetAlertDialog.dismiss();
//                    Intent openVideo = new Intent(Intent.ACTION_VIEW);
//                    openVideo.setDataAndType(Uri.parse(videourl), "video/*");
//                    startActivity(openVideo);
//                }
//
//                @Override
//                public void onFailure(String s) {
//                    super.onFailure(s);
//                    sweetAlertDialog.dismiss();
//                    Toast.makeText(SelectActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            e.printStackTrace();
//        }
    }

    long q, e;
    private FFmpeg ffmpeg;

    private void loadFFMpegBinary() {
        ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        one_min.setChecked(isChecked);
    }
}
