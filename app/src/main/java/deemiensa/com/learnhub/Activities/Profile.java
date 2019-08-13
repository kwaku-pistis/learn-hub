package deemiensa.com.learnhub.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.Classes.StudentUsers;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.SharedPref;


public class Profile extends AppCompatActivity {

    TextView tvUserName, tvProgramme, tvID, tvEmail;
    CircleImageView avatar;

    private DatabaseReference userDB;
    private FirebaseAuth mAuth;
    private StudentUsers myAccount;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUserName =  findViewById(R.id.input_first_name);
        tvProgramme = findViewById(R.id.input_prog);
        tvID = findViewById(R.id.input_ref);
        tvEmail = findViewById(R.id.input_user_email);
        avatar = findViewById(R.id.profile_photo);

        mAuth = FirebaseAuth.getInstance();
        userDB = FirebaseDatabase.getInstance().getReference().child("Students");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Glide.with(Profile.this)
                .setDefaultRequestOptions(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.ic_avatar)
                        .fitCenter()
                ).load(SharedPref.getProfilePic()).into(avatar);

        tvUserName.setText(SharedPref.getProfileName());
        tvProgramme.setText(SharedPref.getProgramme());
        tvEmail.setText(SharedPref.getEmail());
        tvID.setText(SharedPref.getRef());
    }
}
