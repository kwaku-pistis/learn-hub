package deemiensa.com.learnhub.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.warnyul.android.widget.FastVideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.BaseFragments.Video;
import deemiensa.com.learnhub.Classes.HomeVideo;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.Util;

public class PlayVideo extends AppCompatActivity {

    FastVideoView videoView = null;
    ProgressBar mProgress;
    private String str_video, title, desc, profile_pic, category, name, institution, post_key, currentUserID;
    private TextView mTitle, mDesc, mCategory, mName, mInstitute, mThumbUpTv, mThumbDownTv;
    private ImageView mPlay, mForward, mRewind, mFullView, mVideoCall, mThumbUp, mThumbDown;
    private CircleImageView mProfilePic;
    private DatabaseReference mDatabase, likeRef, dislikeRef;
    private DatabaseReference mRootDataBase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String mCurrentUserId;
    private String mChatUserId;
    List<HomeVideo> videoList;

    boolean liked = false;
    boolean disliked = false;
    boolean likeChecker = false;

    int countLikes, countDislikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        str_video = getIntent().getStringExtra("video_url");
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        profile_pic = getIntent().getStringExtra("profile_pic");
        category = getIntent().getStringExtra("category");
        name = getIntent().getStringExtra("name");
        post_key = getIntent().getStringExtra("post_key");
        mChatUserId = getIntent().getStringExtra("post_key");
        institution = getIntent().getStringExtra("institution");

        if(mChatUserId == null) {
            mChatUserId = getIntent().getExtras().getString("sender_id");
        }

        //binding views
        mTitle = findViewById(R.id.title);
        mDesc = findViewById(R.id.desc_textView);
        mProgress= findViewById(R.id.progress_bar);
        mName = findViewById(R.id.name_text);
        mInstitute = findViewById(R.id.institution);
        mCategory = findViewById(R.id.cat_textView);
        mProfilePic = findViewById(R.id.profile_pic);
        mPlay = findViewById(R.id.play_btn);
        mForward = findViewById(R.id.forward_btn);
        mRewind = findViewById(R.id.rewind_btn);
        mFullView = findViewById(R.id.full_view);
        mVideoCall = findViewById(R.id.video_call_btn);
        mThumbUp = findViewById(R.id.thumbs_up);
        mThumbDown = findViewById(R.id.thumbs_down);
        mThumbUpTv = findViewById(R.id.thumbs_up_tv);
        mThumbDownTv = findViewById(R.id.thumbs_down_tv);

        // initializing firebase variables
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Video Posts");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Liked Videos");
        dislikeRef = FirebaseDatabase.getInstance().getReference().child("Disliked Videos");
        mRootDataBase = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = mAuth.getCurrentUser();
        currentUserID = mCurrentUser.getUid();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView = findViewById(R.id.video_list);
        videoList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mDatabase.keepSynced(true);

        init();

        if(mAuth.getCurrentUser() != null) {
            mCurrentUserId = mAuth.getCurrentUser().getUid();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(currentUserID)){
                    countLikes = (int) dataSnapshot.child(post_key).getChildrenCount();
                    mThumbUp.setImageResource(R.drawable.ic_thumb_up_blue);
                    mThumbUpTv.setText(String.valueOf(countLikes));
                } else {
                    countLikes = (int) dataSnapshot.child(post_key).getChildrenCount();
                    mThumbUp.setImageResource(R.drawable.ic_thumb_up_white);
                    mThumbUpTv.setText(String.valueOf(countLikes));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dislikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(currentUserID)){
                    countDislikes = (int) dataSnapshot.child(post_key).getChildrenCount();
                    mThumbDown.setImageResource(R.drawable.ic_thumb_down_blue);
                    mThumbDownTv.setText(String.valueOf(countDislikes));
                } else {
                    countDislikes = (int) dataSnapshot.child(post_key).getChildrenCount();
                    mThumbDown.setImageResource(R.drawable.ic_thumb_down_white);
                    mThumbDownTv.setText(String.valueOf(countDislikes));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query = mDatabase.orderByChild("Category").equalTo(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    String key = data.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<HomeVideo> options =
                new FirebaseRecyclerOptions.Builder<HomeVideo>()
                        .setQuery(query, HomeVideo.class)
                        .build();

        FirebaseRecyclerAdapter<HomeVideo, PlayVideo.VideoViewHolder> FBRA = new FirebaseRecyclerAdapter<HomeVideo, PlayVideo.VideoViewHolder>(
                options
        ) {
            @NonNull
            @Override
            public PlayVideo.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.more_videos_cell, parent, false);

                return new PlayVideo.VideoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PlayVideo.VideoViewHolder viewHolder, int position, @NonNull final HomeVideo model) {
                final String post_key = getRef(position).getKey();
                //last_key = post_key;
                // setting values to the viewholder of the recyclerview
                viewHolder.setTitle(model.getTitle());
                viewHolder.setUsername(model.getName());
                viewHolder.setThumbnail(PlayVideo.this, model.getThumbnail());
                viewHolder.setDuration(model.getDuration());
//                viewHolder.setCategory(model.getCategory());
//                viewHolder.setUsername(model.getName());
//                viewHolder.setProfileImg(PlayVideo.this, model.getProfileImage());
//                viewHolder.setTime(model.getTime(), model.getPostTime());

                viewHolder.view.setOnClickListener(v -> startActivity(new Intent(PlayVideo.this, PlayVideo.class)
                        .putExtra("video_url", model.getVideoUrl())
                        .putExtra("title", model.getTitle())
                        .putExtra("desc", model.getDesc())
                        .putExtra("profile_pic", model.getProfileImage())
                        .putExtra("category", model.getCategory())
                        .putExtra("name", model.getName())
                        .putExtra("institution", model.getInstitution())
                        .putExtra("user_id", post_key)
                ));
            }

            /*//@Override
            protected void populateViewHolder(PlayVideo.VideoViewHolder viewHolder, final HomeVideo model, int position) {
                final String post_key = getRef(position).getKey();
                //last_key = post_key;
                // setting values to the viewholder of the recyclerview
                viewHolder.setTitle(model.getTitle());
                viewHolder.setUsername(model.getName());
                viewHolder.setDuration(model.getDuration());
                viewHolder.setCategory(model.getCategory());
                viewHolder.setUsername(model.getName());
                viewHolder.setThumbnail(PlayVideo.this, model.getThumbnail());
                viewHolder.setProfileImg(PlayVideo.this, model.getProfileImage());
                viewHolder.setTime(model.getTime(), model.getPostTime());

                viewHolder.view.setOnClickListener(v -> startActivity(new Intent(PlayVideo.this, PlayVideo.class)
                        .putExtra("video_url", model.getVideoUrl())
                        .putExtra("title", model.getTitle())
                        .putExtra("desc", model.getDesc())
                        .putExtra("profile_pic", model.getProfileImage())
                        .putExtra("category", model.getCategory())
                        .putExtra("name", model.getName())
                        .putExtra("user_id", post_key)
                ));
            }*/
        };
        FBRA.startListening();
        recyclerView.setAdapter(FBRA);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(){

        final MediaController mCon = new MediaController(this);

        videoView = (FastVideoView)findViewById(R.id.video);

        mTitle.setText(title);
        mDesc.setMovementMethod(LinkMovementMethod.getInstance());
        mDesc.setText(desc);
        mName.setText(name);
        mInstitute.setText(institution);
        mCategory.setText(category);
        Glide.with(this)
                .setDefaultRequestOptions(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.ic_avatar)
                        .fitCenter()
                ).load(profile_pic).into(mProfilePic);

        videoView.setMediaController(mCon);
        videoView.setVideoPath(str_video);
        videoView.setAlpha(1.0f);       // Set transparency.
        videoView.setRotation(0.0f);   // Set rotation.
        videoView.setOnPreparedListener(mp -> {
            videoView.start();
            mProgress.setVisibility(View.GONE);
            mPlay.setImageResource(R.drawable.ic_pause);
            mPlay.setVisibility(View.GONE);
            mForward.setVisibility(View.GONE);
            mRewind.setVisibility(View.GONE);
            mFullView.setVisibility(View.GONE);
        });

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPlay.setVisibility(View.VISIBLE);
                mForward.setVisibility(View.VISIBLE);
                mRewind.setVisibility(View.VISIBLE);
                mFullView.setVisibility(View.VISIBLE);

                Timer t = new Timer(false);
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            mPlay.setVisibility(View.GONE);
                            mForward.setVisibility(View.GONE);
                            mRewind.setVisibility(View.GONE);
                            mFullView.setVisibility(View.GONE);
                        });
                    }
                }, 2500);

                return true;
            }
        });

        videoView.setOnCompletionListener(mp -> {
            mPlay.setImageResource(R.drawable.ic_play);
            mPlay.setVisibility(View.VISIBLE);
            mRewind.setVisibility(View.VISIBLE);
        });

        mPlay.setOnClickListener(v -> {
            if (videoView.isPlaying()){
                videoView.pause();
                mPlay.setImageResource(R.drawable.ic_play);
            } else {
                videoView.start();
                mPlay.setImageResource(R.drawable.ic_pause);
            }
        });

        mForward.setOnClickListener(v -> videoView.seekTo(videoView.getCurrentPosition() + 3000));

        mRewind.setOnClickListener(v -> videoView.seekTo(videoView.getCurrentPosition() - 3000));

        mFullView.setOnClickListener(v -> {
           /* requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoView.getLayoutParams();
            params.width = metrics.widthPixels;
            params.height = metrics.heightPixels;
            params.leftMargin = 0;
            videoView.setLayoutParams(params);
        });

        //boolean liked;
        // when a video is liked
        mThumbUp.setOnClickListener(view -> {
            likeChecker = true;

            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (likeChecker){
                        if (dataSnapshot.child(post_key).hasChild(currentUserID)){
                            // user has already like the video so remove it from the database
                            likeRef.child(post_key).child(currentUserID).removeValue();
                            likeChecker = false;
                        } else {
                            // user hasn't liked this video yet
                            likeRef.child(post_key).child(currentUserID).setValue(true);
                            likeChecker = false;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            disliked = true;

            dislikeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (disliked){
                        if (dataSnapshot.child(post_key).hasChild(currentUserID)){
                            dislikeRef.child(post_key).child(currentUserID).removeValue();
                            disliked = false;
                        }/* else {
                            dislikeRef.child(post_key).child(currentUserID).setValue(true);
                            disliked = false;
                        }*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        // when a video is disliked
        mThumbDown.setOnClickListener(v -> {
           disliked = true;

           dislikeRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if (disliked){
                       if (dataSnapshot.child(post_key).hasChild(currentUserID)){
                           dislikeRef.child(post_key).child(currentUserID).removeValue();
                           disliked = false;
                       } else {
                           dislikeRef.child(post_key).child(currentUserID).setValue(true);
                           disliked = false;
                       }
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });

           likeChecker = true;

            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (likeChecker){
                        if (dataSnapshot.child(post_key).hasChild(currentUserID)){
                            // user has already like the video so remove it from the database
                            likeRef.child(post_key).child(currentUserID).removeValue();
                            likeChecker = false;
                        }/* else {
                            // user hasn't liked this video yet
                            likeRef.child(post_key).child(currentUserID).setValue(true);
                            likeChecker = false;
                        }*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

       mVideoCall.setOnClickListener(v -> {
           final AlertDialog.Builder builder;
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               builder = new AlertDialog.Builder(PlayVideo.this, android.R.style.Theme_Material_Dialog_Alert);
           } else {
               builder = new AlertDialog.Builder(PlayVideo.this);
           }
           builder.setTitle("Start Video Call")
                   .setMessage("Do you really want to start a video call with " + name + "?")
                   .setPositiveButton("YES", (dialog, which) -> {
                       //startActivity(new Intent(PlayVideo.this, VideoChat.class));

                       DatabaseReference userMessagePush = mRootDataBase.child("video_sessions").child(mCurrentUserId).child(mChatUserId).push();
                       final String newVideoSessionPushId = userMessagePush.getKey();

                       DatabaseReference newNotificationRef = mRootDataBase.child("notifications").child(mChatUserId).push();
                       final String newNotificationPushId = newNotificationRef.getKey();

                       HashMap<String, String> notificationData = new HashMap<>();
                       notificationData.put("from", mCurrentUserId);
                       notificationData.put("type", "video_call");

                       String currentUserRef = "video_sessions/" + mCurrentUserId + "/" + mChatUserId;
                       String chatUserRef = "video_sessions/" + mChatUserId + "/" + mCurrentUserId;

                       Map videoCallRequestMap = new HashMap();
                       videoCallRequestMap.put(currentUserRef + "/" + newVideoSessionPushId, "calling");
                       videoCallRequestMap.put(chatUserRef + "/" + newVideoSessionPushId, "getting_call");
                       videoCallRequestMap.put("notifications/" + mChatUserId + "/" + newNotificationPushId, notificationData);

                       mRootDataBase.updateChildren(videoCallRequestMap, (databaseError, databaseReference) -> {
                           if(databaseError == null)
                           {
                               Intent videoSessionIntent = new Intent(PlayVideo.this, VideoChat.class);
                               videoSessionIntent.putExtra("session_id", newVideoSessionPushId);
                               videoSessionIntent.putExtra("current_user_id", mCurrentUserId);
                               videoSessionIntent.putExtra("friend_user_id", mChatUserId);
                               videoSessionIntent.putExtra("friend_name", name);
                               videoSessionIntent.putExtra("call_status", "calling");

                               startActivity(videoSessionIntent);
                           }
                           else
                           {
                               Log.d("CHAT_LOG", databaseError.getMessage());
                           }
                       });
                   })
                   .setNegativeButton("NO", (dialog, which) -> {
                       //finish();
                   })
                   .setIcon(android.R.drawable.ic_dialog_alert)
                   .show();
       });
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        FirebaseAuth mAuth;
        LinearLayout view;

        public VideoViewHolder(View itemView) {
            super(itemView);
            //mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();
            view = itemView.findViewById(R.id.top_layout);
            //mDatabaseLike.keepSynced(true);
        }

        public void setTitle(String title) {
            TextView postTitle = itemView.findViewById(R.id.title_text_view);
            postTitle.setText(title);
        }

        public void setDuration(int desc) {
            TextView post_desc = itemView.findViewById(R.id.video_duration_text_view);
            post_desc.setText(Util.getTime(desc));
        }

        public void setThumbnail(Context context, String image) {
            ImageView post_image = itemView.findViewById(R.id.thumbnail_image_view);
            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .placeholder(R.drawable.thumbnail_default)
                            .fitCenter()
                    ).load(image).into(post_image);
        }

        public void setUsername(String username) {
            TextView postUsername = itemView.findViewById(R.id.category_text_view);
            postUsername.setText(username);
        }

        public void setCategory(String category) {
            TextView postCategory = itemView.findViewById(R.id.views_text_view);
            postCategory.setText(category);
        }

        public void setProfileImg(Context context, String profileImg) {
            CircleImageView post_profile = itemView.findViewById(R.id.profile_image);
            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .placeholder(R.drawable.ic_avatar)
                            .fitCenter()
                    ).load(profileImg).into(post_profile);
        }

        public void setTime(String time, String model_time) {

            String timestampDiff = Util.getTimestampDifference(time);

            TextView post_time = itemView.findViewById(R.id.publish_date_text_view);
            if(!timestampDiff.equals("0")){
                post_time.setText(timestampDiff + " DAYS AGO");
            }else{
                post_time.setText(model_time);
            }
            //post_time.setText(Util.getTimestampDifference(time));
        }
    }
}
