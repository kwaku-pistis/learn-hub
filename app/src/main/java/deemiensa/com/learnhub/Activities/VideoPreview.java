package deemiensa.com.learnhub.Activities;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import deemiensa.com.learnhub.R;

import static deemiensa.com.learnhub.BaseFragments.Upload.selectedImageUri;

public class VideoPreview extends AppCompatActivity {

    // variables
    private VideoView videoView;
    private Button uploadBtn;
    private Thread mTimerThread;
    Dialog alertDialog1 = null;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        // binding xml views by ids
        videoView = findViewById(R.id.videoView);
        uploadBtn = findViewById(R.id.confirmBtn);

        alertDialog1 = new Dialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Video Posts");

        // getting the video url passed from the upload fragment.
        //String videoPath = getIntent().getStringExtra("path");

        // media controller for playing video
        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);
        videoView.setVideoURI(selectedImageUri);
        // setting video to videoView
        //videoView.setVideoPath(videoPath);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //actualResolution.setTitle("Actual resolution");
                //actualResolution.setMessage(mp.getVideoWidth() + " x " + mp.getVideoHeight());
                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();
                float viewWidth = videoView.getWidth();
                lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                videoView.setLayoutParams(lp);
                playVideo();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // function to upload the video to firebase
                uploadVideo();
                //Toast.makeText(VideoPreview.this, "HomeVideo Uploaded Succesfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * function to play video
     */
    void playVideo() {
        if (videoView.isPlaying()) return;
        videoView.start();
    }

    public void uploadVideo(){
        final String user_id = mAuth.getCurrentUser().getUid();
        final StorageReference filepath = storageReference.child("PostVideo").child(selectedImageUri.getLastPathSegment());
        filepath.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

                    DatabaseReference current_user_db = databaseReference.child(user_id);
                    current_user_db.child("Title").setValue("First Video");
                    current_user_db.child("Desc").setValue("This is going to work out fine");
                    current_user_db.child("Image url").setValue(downUri);

                /*databaseReference.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DatabaseReference current_user_db = databaseReference.child(user_id);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/
                    Toast.makeText(VideoPreview.this, "Video Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });








              /*  .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = filepath.getDownloadUrl().toString();

                DatabaseReference current_user_db = databaseReference.child(user_id);
                current_user_db.child("Title").setValue("First HomeVideo");
                current_user_db.child("Desc").setValue("This is going to work out fine");
                current_user_db.child("Image url").setValue(downloadUrl);

                *//*databaseReference.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DatabaseReference current_user_db = databaseReference.child(user_id);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*//*
                Toast.makeText(VideoPreview.this, "HomeVideo Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                finish();

               *//* alertDialog1.setContentView(R.layout.layout_delay_progress);
                alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog1.show();
                alertDialog1.setCanceledOnTouchOutside(false);
                alertDialog1.setCancelable(true);

                mTimerThread =  new Thread(){
                    @Override
                    public void run(){
                        try {
                            synchronized(this){

                                wait(5000);
                            }
                        }
                        catch(InterruptedException ex){

                            Toast.makeText(VideoPreview.this, "Error: " + ex, Toast.LENGTH_LONG).show();
                        }

                        finish();
                        alertDialog1.dismiss();

//                            Toast.makeText(getActivity(), "Profile Update Successfully", Toast.LENGTH_SHORT).show();

                    }
                };
                mTimerThread.start();*//*
            }
        });*/
    }
}
