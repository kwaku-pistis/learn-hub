package deemiensa.com.learnhub.BaseFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.Activities.PlayVideo;
import deemiensa.com.learnhub.App.BaseFragment;
import deemiensa.com.learnhub.Classes.HomeVideo;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class Video extends BaseFragment {

    private RecyclerView mVideoList;
    private SwipeRefreshLayout refreshLayout;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference reference;
    private DatabaseReference mDatabaseVideo;
    private TextView likeViews;
    private String last_key;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private boolean mProcessLike = false;
    public static int count = 0;
    private long number;
    private LinearLayoutManager linearLayoutManager;
    List<HomeVideo> videoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        mVideoList = view.findViewById(R.id.video_list);
        videoList = new ArrayList<>();

        mVideoList.setHasFixedSize(true);
        mVideoList.setLayoutManager(linearLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Video Posts");
       /* mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("PicLikes");
        reference = FirebaseDatabase.getInstance().getReference().child("Trends");
        mDatabaseVideo = FirebaseDatabase.getInstance().getReference().child("Video Posts");*/

        //reference.keepSynced(true);
        mDatabase.keepSynced(true);
        //mDatabaseLike.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        //loadVideo();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//		subsDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();

        //loadVideo();

        FirebaseRecyclerOptions<HomeVideo> options =
                new FirebaseRecyclerOptions.Builder<HomeVideo>()
                        .setQuery(mDatabase, HomeVideo.class)
                        .build();

        FirebaseRecyclerAdapter<HomeVideo, VideoViewHolder> FBRA = new FirebaseRecyclerAdapter<HomeVideo, Video.VideoViewHolder>(
               options
        ) {
            @NonNull
            @Override
            public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.video_cell, parent, false);

                return new VideoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull VideoViewHolder viewHolder, int position, @NonNull final HomeVideo model) {
                final String post_key = getRef(position).getKey();
                count = getItemCount();
                //last_key = post_key;
                // setting values to the viewholder of the recyclerview
                viewHolder.setTitle(model.getTitle());
                viewHolder.setUsername(model.getName());
                viewHolder.setDuration(model.getDuration());
                viewHolder.setCategory(model.getCategory());
                viewHolder.setUsername(model.getName());
                viewHolder.setThumbnail(getActivity().getApplicationContext(), model.getThumbnail());
                viewHolder.setProfileImg(getActivity().getApplicationContext(), model.getProfileImage());
                viewHolder.setTime(model.getTime(), model.getPostTime());

                viewHolder.view.setOnClickListener(v -> startActivity(new Intent(getContext(), PlayVideo.class)
                        .putExtra("video_url", model.getVideoUrl())
                        .putExtra("title", model.getTitle())
                        .putExtra("desc", model.getDesc())
                        .putExtra("profile_pic", model.getProfileImage())
                        .putExtra("category", model.getCategory())
                        .putExtra("name", model.getName())
                        .putExtra("institution", model.getInstitution())
                        .putExtra("published", model.getPublishedDate())
                        .putExtra("post_key", post_key)
                ));
            }
        };
        FBRA.startListening();
        mVideoList.setAdapter(FBRA);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

   /* public void loadVideo(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    videoList.add(new HomeVideo(
                            String.valueOf(data.child("Title").getValue()),
                            String.valueOf(data.child("Desc").getValue()),
                            String.valueOf(data.child("Category").getValue()),
                            String.valueOf(data.child("Video url").getValue()),
                            String.valueOf(data.child("Profile image").getValue()),
                            String.valueOf(data.child("Name").getValue())
                    ));
                }

                HomeVideoAdapter adapter = new HomeVideoAdapter(getContext(), videoList);
                mVideoList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        //View mView;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;
        ConstraintLayout view;

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

        /*public void setLikes(int number){
            TextView post_like = itemView.findViewById(R.id.likesView);
            post_like.setText(number);
        }*/
    }
}
