package deemiensa.com.learnhub.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import deemiensa.com.learnhub.App.MainActivity;
import deemiensa.com.learnhub.R;

import static deemiensa.com.learnhub.Utils.SharedPref.saveProfile;

public class Login1 extends AppCompatActivity {

    private EditText mRefNum;
    private Button mNxtBtn;
    private TextView mLecturerSignIn;
    public static String stud_name, prof_image;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Students");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent profile = new Intent(Login1.this, LoginActivity.class);
                    //profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(profile);
                }
            }
        };

        mRefNum = findViewById(R.id.ref_num);
        mNxtBtn = findViewById(R.id.next_btn);
        mLecturerSignIn = findViewById(R.id.lecturer_sign_in);

        mNxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefNum.setError(null);
                boolean cancel = false;
                View focusView = null;
                final String ref_num = mRefNum.getText().toString().trim();

                if (TextUtils.isEmpty(ref_num)){
                    mRefNum.setError(getString(R.string.error_field_required));
                    focusView = mRefNum;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot data: dataSnapshot.getChildren()) {
                                String ref = String.valueOf(data.child("Ref").getValue());
                                String deviceToken = FirebaseInstanceId.getInstance().getId();
                                Log.d("user_ref", ref);
                                if (ref.equals(ref_num)){
                                    stud_name = String.valueOf(data.child("Name").getValue()).toUpperCase();
                                    prof_image = String.valueOf(data.child("Profile Image url").getValue());
                                    String programme = String.valueOf(data.child("Programme").getValue());
                                    String email =  String.valueOf(data.child("Email").getValue());
                                    Log.d("HEY1", stud_name);
                                    saveProfile(stud_name, prof_image , programme, email, ref);

                                    /*databaseReference.child(mAuth.getCurrentUser().getUid()).setValue("DeviceToken", deviceToken)
                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        databaseReference.child("online").setValue(true);

                                                        startActivity(new Intent(Login1.this, LoginActivity.class));
                                                    }
                                                }
                                            });*/
                                    startActivity(new Intent(Login1.this, LoginActivity.class));
                                } else {
                                    Snackbar.make(findViewById(R.id.next_btn), "User for this reference number does not exist", Snackbar.LENGTH_LONG).show();
                                    //alertDialog1.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("DbERROR", String.valueOf(databaseError));
                            //alertDialog1.dismiss();
                            Snackbar.make(findViewById(R.id.next_btn), "Error: " + databaseError, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        mLecturerSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login1.this, LecturerSignIn.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        if (!isNetworkConnected()) {
            Snackbar.make(findViewById(R.id.next_btn), "No Internet Connection", Snackbar.LENGTH_LONG).show();

        }
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null;
    }
}
