package deemiensa.com.learnhub.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.App.MainActivity;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.Util;

import static deemiensa.com.learnhub.Utils.SharedPref.saveLecturerProfile;

public class LecturerSignIn extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, mStaffId;
    private View mProgressView;
    private View mLoginFormView;
    private Button signUp;
    Dialog alertDialog1 = null;
    private Thread mTimerThread;
    private CircleImageView profileImage;

    // firebase variables
    private static final String TAG = "TAG";
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_sign_in);

        alertDialog1 = new Dialog(LecturerSignIn.this);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mStaffId = findViewById(R.id.staff_id);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Lecturers");

        //check this if doesn't work
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent profile = new Intent(LecturerSignIn.this, MainActivity.class);
                    //profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(profile);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        if (!isNetworkConnected()) {
            Snackbar.make(findViewById(R.id.email_sign_in_button), "No Internet Connection", Snackbar.LENGTH_LONG).show();

        }
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null;
    }

    private void attemptLogin() {
       /* if (mAuthTask != null) {
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString()  + "@lt.knust.edu.gh";
        final String password = mPasswordView.getText().toString();
        final String staff_id = mStaffId.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        //Check if password is empty
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !Util.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        //Check if staff id is empty
        if (TextUtils.isEmpty(staff_id)) {
            mStaffId.setError(getString(R.string.error_field_required));
            focusView = mStaffId;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            alertDialog1.setContentView(R.layout.layout_initializing_progress);
            alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog1.show();
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.setCancelable(true);

            mTimerThread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {

                            wait(3000);
                        }
                    } catch (InterruptedException ex) {

                        Toast.makeText(LecturerSignIn.this, "Initializing failed", Toast.LENGTH_LONG).show();
                    }

                    //alertDialog1.dismiss();

                }
            };

            mTimerThread.start();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(DataSnapshot data: dataSnapshot.getChildren()) {
                        String ref = String.valueOf(data.child("Staff ID").getValue());
                        Log.d("HEY", ref);
                        if (ref.equals(staff_id)){
                            String name = String.valueOf(data.child("Name").getValue()).toUpperCase();
                            String prof_image = String.valueOf(data.child("Profile Image url").getValue());
                            String dept = String.valueOf(data.child("Department").getValue());
                            String specialty = String.valueOf(data.child("Specialisation").getValue());
                            //Log.d("HEY1", stud_name);
                            saveLecturerProfile(name, prof_image, dept, specialty);
                            //startActivity(new Intent(Login1.this, LoginActivity.class));

                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        checkUserExists();
                                    }else {
                                        alertDialog1.dismiss();
                                        Snackbar.make(findViewById(R.id.email_sign_in_button), "Error: Wrong Credentials!", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(LecturerSignIn.this, "User for this reference number does not exist", Toast.LENGTH_LONG).show();
                            alertDialog1.dismiss();
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

    /**
     * check if the user exists in the database.
     */
    public void checkUserExists(){
        final String user_id = mAuth.getCurrentUser().getUid();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)){
                    Log.d(TAG, "USER ID: " + user_id);
                    alertDialog1.dismiss();
                    Intent intent = new Intent(LecturerSignIn.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(LoginActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
