package deemiensa.com.learnhub.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.App.MainActivity;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.SharedPref;
import deemiensa.com.learnhub.Utils.Util;

import static android.Manifest.permission.READ_CONTACTS;
import static deemiensa.com.learnhub.Activities.Login1.prof_image;
import static deemiensa.com.learnhub.Activities.Login1.stud_name;
import static deemiensa.com.learnhub.Utils.SharedPref.saveProfile;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button signUp;
    private TextView mName;
    Dialog alertDialog1 = null;
    private Thread mTimerThread;
    private CircleImageView profileImage;

    // firebase variables
    private static final String TAG = "TAG";
    private SignInButton googleBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private final static int RC_SIGN_IN = 2;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;

    private boolean notInDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        populateAutoComplete();

        //profileImage = findViewById(R.id.profileImage);
        alertDialog1 = new Dialog(LoginActivity.this);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        //mName = findViewById(R.id.name_view);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        signUp = findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUp.class)));

        googleBtn = findViewById(R.id.googleBtn);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        googleBtn.setOnClickListener(v -> {
            if (!isNetworkConnected()) {
                Snackbar.make(v, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            } else {

                if (isNetworkConnected()) {
                    signIn();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

       /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionResult -> Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //check this if doesn't work
        mAuthListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()){
                Intent profile = new Intent(LoginActivity.this, MainActivity.class);
                //profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(profile);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        String saved_email = SharedPref.getmInstance(this).getEmail();
        if (saved_email != null){
            mEmailView.setText(saved_email);
        }

        /*String user_id = mAuth.getCurrentUser().getUid();
        databaseReference.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileImg = String.valueOf(dataSnapshot.child("Profile Image url").getValue());
                Glide.with(LoginActivity.this).load(profileImg).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        /*Glide.with(LoginActivity.this)
                .setDefaultRequestOptions(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.ic_avatar)
                        .fitCenter()
                ).load(prof_image).into(profileImage);*/

//        mName.setText(stud_name);

        setGoogleBtnText(googleBtn, "SIGN IN WITH GOOGLE");
        //googleBtn.setBackgroundResource(R.drawable.white_rounded_button);

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

    protected void setGoogleBtnText(SignInButton googleBtn, String buttonText){
        // setting text for google sign in button
        for (int i = 0; i < googleBtn.getChildCount(); i++){
            View v = googleBtn.getChildAt(i);
            if(v instanceof TextView){
                TextView textView = (TextView) v;
                textView.setText(buttonText);
                return;
            }
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.d(TAG, "onActivityResult: sign in successful");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot data: dataSnapshot.getChildren()) {
                                    String ref = String.valueOf(data.getKey());
                                    Log.d("user_ref", ref);
                                    Log.d(TAG, "onDataChange: " + mAuth.getCurrentUser().getUid());
                                    if (ref.equals(mAuth.getCurrentUser().getUid())){
                                        String name = String.valueOf(data.child("Name").getValue());
                                        String username = String.valueOf(data.child("Username").getValue());
                                        String email = String.valueOf(data.child("Email").getValue());
                                        String discipline = String.valueOf(data.child("Discipline").getValue());
                                        String institution = String.valueOf(data.child("Institution").getValue());
                                        String profile_pic = String.valueOf(data.child("Profile Image").getValue());
                                        String phone = String.valueOf(data.child("Phone Number").getValue());
                                        saveProfile(name, username, profile_pic, discipline, email, phone, institution);

                                        //startActivity(new Intent(Login1.this, LoginActivity.class));
                                    } else {
                                        notInDB = true;
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

                        if (notInDB){
                            FirebaseUser user = mAuth.getCurrentUser();
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            String phone = user.getPhoneNumber();
                            String profile_pic = user.getPhotoUrl().toString();

                            SharedPref.saveProfile(name, name, profile_pic, "", email, phone, "");

                            DatabaseReference current_user_db = databaseReference.child(user.getUid());
                            current_user_db.child("Name").setValue(name);
                            current_user_db.child("Email").setValue(email);
                            current_user_db.child("Username").setValue(name.toLowerCase());
                            current_user_db.child("Discipline").setValue("");
                            current_user_db.child("Institution").setValue("");
                            current_user_db.child("Phone Number").setValue(phone);
                            current_user_db.child("Profile Image").setValue(profile_pic);

                            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.email_sign_in_button), "Authentication Failed.", Snackbar.LENGTH_LONG).show();
                        //updateUI(null);
                    }

                    // ...
                });
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
       /* if (mAuthTask != null) {
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        SharedPref.saveEmail(email);

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

                        Toast.makeText(LoginActivity.this, "Validating failed", Toast.LENGTH_LONG).show();
                    }

                    // finish();
                    //SharedPref.getmInstance(context).saveSendBirdInfo();
                    alertDialog1.dismiss();

                }
            };

            mTimerThread.start();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    checkUserExists();
                }else {
                    alertDialog1.dismiss();
                    Snackbar.make(findViewById(R.id.email_sign_in_button), "Error: Wrong Credentials!", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * check if the user exists in the database.
     */
    public void checkUserExists(){
        final String user_id = mAuth.getCurrentUser().getUid();
        FirebaseUser user = mAuth.getCurrentUser();
        user.reload();
        if (user.isEmailVerified()){

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)){
                        Log.d(TAG, "USER ID: " + user_id);

                        //for(DataSnapshot data: dataSnapshot.getChildren()) {
                            String name = dataSnapshot.child(user_id).child("First Name").getValue() + " " + dataSnapshot.child(user_id).child("Last Name").getValue();
                            String username = String.valueOf(dataSnapshot.child(user_id).child("Username").getValue());
                            String email = String.valueOf(dataSnapshot.child(user_id).child("Email").getValue());
                            String discipline = String.valueOf(dataSnapshot.child(user_id).child("Discipline").getValue());
                            String institution = String.valueOf(dataSnapshot.child(user_id).child("Institution").getValue());
                            String profile_pic = String.valueOf(dataSnapshot.child(user_id).child("Profile Image").getValue());
                            String phone = String.valueOf(dataSnapshot.child(user_id).child("Phone Number").getValue());
                            //String deviceToken = FirebaseInstanceId.getInstance().getId();

                            SharedPref.saveProfile(name, username, profile_pic, discipline, email, phone, institution);
                        //}

                        alertDialog1.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Toast.makeText(LoginActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
        } else {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title("Verify Email")
                    .content("Please your email is not verified. Verify your email before signing in.")
                    .positiveText("OK")
                    .onPositive((dialog1, which) -> dialog1.dismiss())
                    .build();

            dialog.show();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }

    /*@Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to exit LearnHub?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }*/
}

