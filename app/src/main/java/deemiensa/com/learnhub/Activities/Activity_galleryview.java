package deemiensa.com.learnhub.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.warnyul.android.widget.FastVideoView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Tasks.MyUploadService;
import deemiensa.com.learnhub.Utils.SharedPref;

import static deemiensa.com.learnhub.Utils.SharedPref.getProfileName;
import static deemiensa.com.learnhub.Utils.SharedPref.getProfilePic;
import static deemiensa.com.learnhub.Utils.SharedPref.getProgramme;
import static deemiensa.com.learnhub.Utils.SharedPref.getSavedDept;
import static deemiensa.com.learnhub.Utils.SharedPref.isLecturer;

public class Activity_galleryview extends AppCompatActivity {

    String str_video, title, description, category;
    FastVideoView videoView = null;
    FFmpeg ffmpeg;
    Dialog alertDialog1 = null;
    private Thread mTimerThread;
    boolean trimEnabled = false;
    private EditText mTitle, mDesc;
    private TextView mCount, mName, mProg, mTvLeft, mTvRight, mHelp, mDept, mTrim;
    private CircleImageView mProfilePic;
    private RangeSeekBar mSeekbar;
    private static final String TAG = "Video Details";
    private RelativeLayout mDisciplineBtn, mTrimRel;
    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;

    int duration, stopPosition;
    String filePrefix, original_path;
    String[] command;
    File destination;
    //Uri uri;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galleryview);

        mTitle = findViewById(R.id.title);
        mDesc = findViewById(R.id.description);
        mDept = findViewById(R.id.category);
        mDisciplineBtn = findViewById(R.id.discipline_btn);
        mCount = findViewById(R.id.title_counter);
        mName = findViewById(R.id.name_text);
        mProg = findViewById(R.id.programme);
        mProfilePic = findViewById(R.id.profile_pic);
        mTvLeft = findViewById(R.id.tvvleft);
        mTvRight = findViewById(R.id.tvvRight);
        mSeekbar = findViewById(R.id.seekbar);
        mHelp = findViewById(R.id.help);
        mTrim = findViewById(R.id.trimBtn);
        mTrimRel = findViewById(R.id.trim_rel);

        storageReference = FirebaseStorage.getInstance().getReference().child("Thumbnails");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Video Posts");

        alertDialog1 = new Dialog(Activity_galleryview.this);

        init();

        loadFFMpegBinary();

        mDisciplineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Activity_galleryview.this, Departments.class));
            }
        });

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = mTitle.getText().toString();
                mCount.setText(text.length() + "/100");
                /*if (text.length() > 100){
                    //mTitle.setFocusable(false);
                    mTitle.setError("Title cannot exceed 100 characters");
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mTitle.getText().toString();
                mCount.setText(text.length() + "/100");
                /*if (text.length() > 100){
                    //mTitle.setFocusable(false);
                    //mTitle.setEnabled(false);
                    mTitle.setError("Title cannot exceed 100 characters");
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mTitle.getText().toString();
                mCount.setText(text.length() + "/100");
                /*if (text.length() > 100){
                    //mTitle.setFocusable(false);
                    mTitle.setError("Title cannot exceed 100 characters");
                }*/
            }
        });

        mTrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrimRel.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // initialize views with data from database
        if (!isLecturer()){
            mName.setText(getProfileName());
            mProg.setText(getProgramme());
        } else

        Glide.with(this).setDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_avatar)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fitCenter()
        ).load(getProfilePic()).into(mProfilePic);
    }

    private void init(){

       /* vv_video = findViewById(R.id.vv_video);

        str_video = getIntent().getStringExtra("video");
        vv_video.setVideoPath(str_video);
        vv_video.start();*/

        final MediaController mCon = new MediaController(this);

        videoView = (FastVideoView)findViewById(R.id.video);
        //ScalableVideoView videoView = (ScalableVideoView) findViewById(R.id.video_view);

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.TOP;
        //lp.width = post_video.getWidth();
        mCon.setLayoutParams(lp);
        mCon.setAnchorView(videoView);
        mCon.setMediaPlayer(videoView);

        videoView.setMediaController(mCon);
        str_video = getIntent().getStringExtra("video");
        //videoView.setDataSource(str_video);
        //videoView.setRawData();
        videoView.setVideoPath(str_video);
        videoView.setAlpha(1.0f);       // Set transparency.
        videoView.setRotation(0.0f);   // Set rotation.

        //videoView.start();

        //videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                duration = mp.getDuration() / 1000;
                mTvLeft.setText("00:00:00");
                mTvRight.setText(getTime(duration));
                //mp.setLooping(true);
                mSeekbar.setRangeValues(0, duration);
                mSeekbar.setSelectedMaxValue(duration);
                mSeekbar.setSelectedMinValue(0);
                mSeekbar.setEnabled(true);

                mSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                        trimEnabled = true;
                        videoView.seekTo((int)minValue * 1000);
                        mTvLeft.setText(getTime((int)bar.getSelectedMinValue()));
                        mTvRight.setText(getTime((int)bar.getSelectedMaxValue()));
                        duration = (int)bar.getSelectedMaxValue();
                    }
                });

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (videoView.getCurrentPosition() >= mSeekbar.getSelectedMaxValue().intValue() * 1000){
                            videoView.seekTo(mSeekbar.getSelectedMinValue().intValue() * 1000);
                        }
                    }
                }, 1000);
            }
        });

        /*videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });*/
        //post_video.start();
       /* if (videoView.isPlaying()) return;
        videoView.start();*/

        /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();
                float viewWidth = videoView.getWidth();
                float viewHeight = videoView.getHeight();
                lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                lp.height = (int) viewHeight;
                //videoView.setLayoutParams(lp);
                if (videoView.isPlaying()) return;
                videoView.start();

            }
        });*/

        /*((ViewGroup) mCon.getParent()).removeView(mCon);

        ((FrameLayout) findViewById(R.id.frameLayout)).addView(mCon);*/

        // check codes
        //ViewGroup.LayoutParams lp = post_video.getLayoutParams();



        /*float videoWidth = mp.getVideoWidth();
        float videoHeight = mp.getVideoHeight();
        float viewWidth = post_video.getWidth();
        float viewHeight = post_video.getHeight();
        lp.height = (int) ((viewWidth - videoWidth) * (videoHeight / videoWidth) + viewHeight);*/

        //String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", inputFileAbsolutePath, "-t", "" + (endMs - startMs) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", outputFileAbsolutePath};
    }

    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;

        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return true; //super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_post) {
            boolean cancel = false;
            View focusView = null;

            title = mTitle.getText().toString().trim();
            description = mDesc.getText().toString().trim();
            category = mDept.getText().toString().trim();
            filePrefix = title;

            if (TextUtils.isEmpty(title)){
                mTitle.setError(getString(R.string.error_field_required));
                focusView = mTitle;
                cancel = true;
            }

            if (TextUtils.isEmpty(category)){
                mDept.setError(getString(R.string.error_field_required));
                focusView = mDept;
                cancel = true;
            }

            if (cancel){
                focusView.requestFocus();
            } else {
                if (trimEnabled){
                    // codes to trim video
                    executeCutVideoCommand(mSeekbar.getSelectedMinValue().intValue() * 1000,
                            mSeekbar.getSelectedMaxValue().intValue() * 1000, filePrefix);
                } else {
                    File main_vid = new File(str_video);
                    uploadVideo(Uri.fromFile(main_vid), title, description, category, duration);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * this is a function to trim the video before uploading
     * @param startMs
     * @param endMs
     * @param fileName
     */
    private void trimVideo(int startMs, int endMs, String fileName) {
        filePrefix = fileName;
        String fileExt = ".mp4";
        destination = new File(" ", filePrefix + fileExt);
        original_path = getRealPathFromUri(getApplicationContext(), Uri.parse(str_video));

        duration = (endMs - startMs) / 1000;
        command = new String[]{"-ss", "" + startMs / 1000, "-y","-i", original_path, "-t","" + (endMs - startMs) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", destination.getAbsolutePath()};
    }

    /**
     * Command for cutting video
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void executeCutVideoCommand(int startMs, int endMs, String file_prefix) {
        File moviesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
        );

        //String filePrefix = file_prefix; //"cut_video";
        String fileExtn = ".mp4";
        File old_vid_path = new File(str_video);
        String yourRealPath = getPath(Activity_galleryview.this, Uri.fromFile(old_vid_path));
        File dest = new File(moviesDir, file_prefix + fileExtn);
        int fileNo = 0;
        while (dest.exists()) {
            fileNo++;
            dest = new File(moviesDir, file_prefix + fileNo + fileExtn);
        }

        Log.d(TAG, "startTrim: src: " + yourRealPath);
        Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
        Log.d(TAG, "startTrim: startMs: " + startMs);
        Log.d(TAG, "startTrim: endMs: " + endMs);
        original_path = dest.getAbsolutePath();
        //String[] complexCommand = {"-i", yourRealPath, "-ss", "" + startMs / 1000, "-t", "" + endMs / 1000, dest.getAbsolutePath()};
        String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", original_path};

        execFFmpegBinary(complexCommand, original_path);
    }

    /**
     * a function to get the real path of the video
     * @param contentUri
     * @param context
     * @return
     */
    private String getRealPathFromUri(Context context, Uri contentUri) {

        Cursor cursor = null;

        try{
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } catch (Exception e){
            e.printStackTrace();
            return "";
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
    }

    public void setUpFFmpeg(){
        ffmpeg = FFmpeg.getInstance(this);

        try{
            ffmpeg.loadBinary(new LoadBinaryResponseHandler(){

                @Override
                public void onStart() {
                    Log.d("Event ", "onStart");
                }

                @Override
                public void onFailure() {
                    Log.d("Event ", "onFailure");
                }

                @Override
                public void onSuccess() {
                    Log.d("Event ", "onSuccess");
                }

                @Override
                public void onFinish() {
                    Log.d("Event ", "onFinish");

                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void execFFmpegBinary(String[] command, final String file_path){
        try {

            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d("Event ", "onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.e("Event ", "onProgress - " + message);
                    //showProgress();
                    showProgressDialog("Trimming Video");
                }

                @Override
                public void onFailure(String message) {
                    Log.e("Event ", "onFailure - " + message);
                    //alertDialog1.dismiss();
                    hideProgressDialog();
                }

                @Override
                public void onSuccess(String message) {
                    Log.e("Event ", "onSuccess - " + message);
                    Toast.makeText(Activity_galleryview.this, "Video successfully trimmed. Uploading...", Toast.LENGTH_LONG).show();
                    //alertDialog1.dismiss();
                    hideProgressDialog();
                }

                @Override
                public void onFinish() {
                    Log.e("Event ", "onFinish");
                    File file_to_upload = new File(file_path);
                    uploadVideo(Uri.fromFile(file_to_upload), title, description, category, duration);
                    //finish();
                    //alertDialog1.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }


    private void loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                Log.d(TAG, "ffmpeg : null");
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    //showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "ffmpeg : correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            //showUnsupportedExceptionDialog();
        } catch (Exception e) {
            Log.d(TAG, "EXception not supported : " + e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPosition = videoView.getCurrentPosition(); //stopPosition is an int
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(stopPosition);
        videoView.start();

        if (getSavedDept() == null){
            mDept.setText(getProgramme());
        } else {
            mDept.setText(getSavedDept());
        }
    }

    public void uploadVideo(Uri uri, final String title, final String desc, final String category, final int duration){
        //showProgress();

        // Save the File URI
        mFileUri = uri;

        // Clear the last download, if any
        //updateUI(mAuth.getCurrentUser());
        mDownloadUrl = null;
        final String thumbnailUri;
        final String thumbUri = null;

        // Generate thumbnail from video
        final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(str_video, MediaStore.Video.Thumbnails.MINI_KIND);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumb = baos.toByteArray();

        // Upload thumbnail to firebase
        final StorageReference mStorage = storageReference.child(String.valueOf(new Date()));
        mStorage.putBytes(thumb)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        // Forward any exceptions
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        Log.d(TAG, "uploadFromUri: upload success");

                        // Request the public download URL
                        //SharedPref.saveThumbnail(String.valueOf(storageReference.getDownloadUrl()));
                        return mStorage.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    String downUri = task.getResult().toString();
                    Log.d("THUMB_TAG", "onComplete: Url: "+ downUri);

                    SharedPref.saveThumbnail(downUri);
                    //thumbnailUri = downUri;
                }
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri downloadUri) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri: getDownloadUri success");
                        //SharedPref.saveThumbnail(downloadUri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                    }
                });

        //thumbnailUri = thumbUri;

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, uri)
                .putExtra("title", title)
                .putExtra("desc", desc)
                .putExtra("category", category)
                .putExtra("duration", duration)
                .setAction(MyUploadService.ACTION_UPLOAD));

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));

        finish();

        /*final String user_id = mAuth.getCurrentUser().getUid();
        final StorageReference filepath = storageReference.child("PostVideo").child(uri.getLastPathSegment());
        filepath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    String downUri = task.getResult().toString();
                    Log.d("VIDEO PREVIEW", "onComplete: Url: "+ downUri);

                    //String downloadUrl = filepath.getDownloadUrl().toString();

                    DatabaseReference current_user_db = databaseReference.child(user_id).push();
                    current_user_db.child("Title").setValue(title);
                    current_user_db.child("Desc").setValue(desc);
                    current_user_db.child("Category").setValue(category);
                    current_user_db.child("Image url").setValue(downUri);

                *//*databaseReference.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DatabaseReference current_user_db = databaseReference.child(user_id);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*//*
                    Toast.makeText(Activity_galleryview.this, "HomeVideo Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                    alertDialog1.dismiss();
                    finish();
                }
            }
        });*/
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri.
     */
    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public void showProgress(){
        alertDialog1.setContentView(R.layout.layout_trimming_progress);
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog1.show();
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog1.setCancelable(false);
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // Save the File URI
        mFileUri = fileUri;

        // Clear the last download, if any
        //updateUI(mAuth.getCurrentUser());
        mDownloadUrl = null;

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .setAction(MyUploadService.ACTION_UPLOAD));

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));
    }
}
