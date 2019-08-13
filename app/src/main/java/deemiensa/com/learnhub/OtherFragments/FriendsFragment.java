package deemiensa.com.learnhub.OtherFragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.Activities.Profile;
import deemiensa.com.learnhub.Classes.SimpleDividerItemDecoration;
import deemiensa.com.learnhub.Classes.StudentUsers;
import deemiensa.com.learnhub.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment
{
    private RecyclerView mFriendList;

    private DatabaseReference mFriendsDataBase;
    private DatabaseReference mUserDataBase;
    private FirebaseAuth mAuth;

    public FriendsFragment() { /* Required empty public constructor */ }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendList = (RecyclerView) mMainView.findViewById(R.id.friends_list_recycler_view);

        mAuth = FirebaseAuth.getInstance();

        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Students");
        mUserDataBase.keepSynced(true);

        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        mFriendsDataBase = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserId);
        mFriendsDataBase.keepSynced(true);

        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<StudentUsers> options =
                new FirebaseRecyclerOptions.Builder<StudentUsers>()
                        .setQuery(mFriendsDataBase, StudentUsers.class)
                        .build();

        FirebaseRecyclerAdapter<StudentUsers, FriendViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<StudentUsers, FriendViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder friendViewHolder, int position, @NonNull final StudentUsers friend)
            {
                final String friendUserId = getRef(position).getKey();

                assert friendUserId != null;
                mUserDataBase.child(friendUserId).addValueEventListener(new ValueEventListener()
                {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        final String userName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                        String status = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                        String userThumbImage = Objects.requireNonNull(dataSnapshot.child("thumb_image").getValue()).toString();

                        if (dataSnapshot.hasChild("online"))
                        {
                            String userConnectedStatus = dataSnapshot.child("online").getValue().toString();
                            friendViewHolder.setUserConnectedStatus(userConnectedStatus);
                        }

                        friendViewHolder.setName(userName);
                        friendViewHolder.setStatus(status);
                        friendViewHolder.setUserThumbImage(getActivity().getApplicationContext(), userThumbImage);

                        friendViewHolder.mView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                CharSequence options[] = new CharSequence[]{getString(R.string.open_profile), getString(R.string.send_message)};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        switch(which)
                                        {
                                            case 0:
                                                Intent userProfileIntent = new Intent(getContext(), Profile.class);
                                                userProfileIntent.putExtra("user_id", friendUserId);
                                                startActivity(userProfileIntent);
                                                break;

                                            case 1:
                                                Intent chatIntent = new Intent(getContext(), null);
                                                chatIntent.putExtra("user_id", friendUserId);
                                                chatIntent.putExtra("user_name", userName);
                                                startActivity(chatIntent);
                                                break;
                                        }
                                    }
                                });

                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_cell, parent, false);

                return new FriendViewHolder(view);
            }
        };

        friendsRecyclerViewAdapter.startListening();
        mFriendList.setAdapter(friendsRecyclerViewAdapter);
        mFriendList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder
    {
        public View mView;

        public FriendViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setStatus(String status)
        {
            TextView userStatusView = (TextView) mView.findViewById(R.id.friends_status);
            userStatusView.setText(status);
        }

        public void setName(String name)
        {
            TextView userNameTv = mView.findViewById(R.id.friends_user_name);
            userNameTv.setText(name);
        }

        public void setUserThumbImage(Context context, String thumbImage)
        {
            CircleImageView userThumbImage = (CircleImageView) mView.findViewById(R.id.friends_profile_image);
            //Picasso.get().load(thumbImage).placeholder(R.drawable.blank_contact).into(userThumbImage);
            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .placeholder(R.drawable.ic_avatar)
                            .fitCenter()
                    ).load(thumbImage).into(userThumbImage);
        }

        public void setUserConnectedStatus(String connectedStatus)
        {
            ImageView connectedStatusImage = (ImageView) mView.findViewById(R.id.friends_connected_icon);
            connectedStatusImage.setVisibility(View.VISIBLE);

            if(connectedStatus.equals("true"))
            {
                connectedStatusImage.setImageResource(R.drawable.online_icon);
            }
            else
            {
                connectedStatusImage.setImageResource(R.drawable.offline_icon);
            }
        }
    }
}
