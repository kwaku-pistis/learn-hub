package deemiensa.com.learnhub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.List;

import deemiensa.com.learnhub.Adapters.HomeVideoAdapter;
import deemiensa.com.learnhub.Classes.HomeVideo;
import deemiensa.com.learnhub.R;

public class MyChannel extends AppCompatActivity {

    private List<HomeVideo> myVideo;
    private RecyclerView recyclerView;
    private HomeVideoAdapter myAdapter;
    private DatabaseReference database;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_channel);

        recyclerView = findViewById(R.id.video_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myVideo = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Video Posts");
        database.keepSynced(true);

        /*myAdapter = new HomeVideoAdapter(MyChannel.this, myVideo);
        recyclerView.setAdapter(myAdapter);*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        String current_user_id = mAuth.getCurrentUser().getUid();
                        String uid = String.valueOf(data.child("UserID").getValue());

                        Log.d("CHANNEL", "UserID: " + current_user_id);
                        Log.d("CHANNEL", "Firebase UserID: " + uid);

                        if (current_user_id.equals(uid)){
                            HomeVideo video_list = data.getValue(HomeVideo.class);
                            myVideo.add(video_list);

                            myAdapter = new HomeVideoAdapter(MyChannel.this, myVideo);

                        }
                    }
                    recyclerView.setAdapter(myAdapter);
                    /*myAdapter = new HomeVideoAdapter(MyChannel.this, myVideo);
                    recyclerView.setAdapter(myAdapter);*/
                }
                recyclerView.setAdapter(myAdapter);
                /*myAdapter = new HomeVideoAdapter(MyChannel.this, myVideo);
                recyclerView.setAdapter(myAdapter);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myAdapter = new HomeVideoAdapter(MyChannel.this, myVideo);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //

        if (item.getItemId() == R.id.delete) {
            LovelyProgressDialog progressDialog = new LovelyProgressDialog(this);
            progressDialog.setCancelable(true)
                .setTitle("Deleting Video")
                .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
