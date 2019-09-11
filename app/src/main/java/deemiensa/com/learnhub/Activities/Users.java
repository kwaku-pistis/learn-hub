package deemiensa.com.learnhub.Activities;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.Objects;

import deemiensa.com.learnhub.Classes.SimpleDividerItemDecoration;
import deemiensa.com.learnhub.Classes.StudentUsers;
import deemiensa.com.learnhub.OtherFragments.FriendsFragment;
import deemiensa.com.learnhub.R;

public class Users extends AppCompatActivity {

    private RecyclerView mUsersList;
    private Thread mThread;
    private LovelyProgressDialog progressDialog;
    private DatabaseReference mAllUsersDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mAllUsersDataBase = FirebaseDatabase.getInstance().getReference().child("Students");

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new LovelyProgressDialog(this);
        progressDialog.setCancelable(false)
                .setTitle("Fetching all users")
                .show();
        mThread = new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this) {

                        wait(5000);
                    }
                } catch (InterruptedException ex) {

                    //Toast.makeText(HomePostActivity.this, "Initializing failed", Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(Users.this, "Something went wrong. Failed to fetch all users", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Snackbar.make(findViewById(R.id.users_list), "Something went wrong. Failed to fetch all users", Snackbar.LENGTH_LONG).show();
            }
        };
        mThread.start();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<StudentUsers> options =
                new FirebaseRecyclerOptions.Builder<StudentUsers>()
                        .setQuery(mAllUsersDataBase, StudentUsers.class)
                        .build();

        FirebaseRecyclerAdapter<StudentUsers, FriendsFragment.FriendViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<StudentUsers, FriendsFragment.FriendViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsFragment.FriendViewHolder friendViewHolder, int position, @NonNull final StudentUsers friend)
            {
                final String userId = getRef(position).getKey();

                assert userId != null;
                mAllUsersDataBase.child(userId).addValueEventListener(new ValueEventListener()
                {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        final String userName = Objects.requireNonNull(dataSnapshot.child("Name").getValue()).toString();
                        String programme = Objects.requireNonNull(dataSnapshot.child("Programme").getValue()).toString();
                        String userThumbImage = Objects.requireNonNull(dataSnapshot.child("Profile Image url").getValue()).toString();

                        if (dataSnapshot.hasChild("online"))
                        {
                            String userConnectedStatus = dataSnapshot.child("online").getValue().toString();
                            friendViewHolder.setUserConnectedStatus(userConnectedStatus);
                        }

                        friendViewHolder.setName(userName);
                        friendViewHolder.setStatus(programme);
                        friendViewHolder.setUserThumbImage(Users.this, userThumbImage);

                        ImageView statusIV = (ImageView) friendViewHolder.mView.findViewById(R.id.friends_connected_icon);
                        statusIV.setVisibility(View.INVISIBLE);

                        friendViewHolder.mView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent userProfileIntent = new Intent(Users.this, Profile.class);
                                userProfileIntent.putExtra("user_id", userId);
                                startActivity(userProfileIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            @NonNull
            @Override
            public FriendsFragment.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_cell, parent, false);

                return new FriendsFragment.FriendViewHolder(view);
            }
        };

        friendsRecyclerViewAdapter.startListening();
        mUsersList.setAdapter(friendsRecyclerViewAdapter);
        mUsersList.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
    }
}
