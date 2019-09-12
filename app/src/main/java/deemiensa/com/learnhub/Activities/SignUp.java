package deemiensa.com.learnhub.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.Util;

public class SignUp extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmail;
    private EditText mPassword, confirmPassword, firstName, lastName, mUsername, mDiscipline;
    private AppCompatSpinner mInstitution;
    private Button signUpBtn;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox lectureCheckBox;
    private ProgressDialog progressDialog;

    // firebase reference
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String pwd, cpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmail = findViewById(R.id.email);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        firstName = findViewById(R.id.input_first_name);
        lastName = findViewById(R.id.input_last_name);
        mInstitution = findViewById(R.id.institution);
        mDiscipline = findViewById(R.id.discipline);
        lectureCheckBox = findViewById(R.id.lecturer_checkbox);
        signUpBtn = findViewById(R.id.sign_up_button);

        mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pwd = mPassword.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                pwd = mPassword.getText().toString();
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean cancel = false;
                View focusView = null;

                cpwd = confirmPassword.getText().toString();

                if (!TextUtils.isEmpty(cpwd) && !pwd.equals(cpwd)){
                    //Toast.makeText(this, "Please make sure your passwords match", Toast.LENGTH_SHORT).show();
                    confirmPassword.setError(getString(R.string.error_confirm_password));
                    focusView = confirmPassword;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signUpBtn.setOnClickListener(v -> registerUser(v));
    }

    /**
     * a function to register the user using email and password
     */
    private void registerUser(View view){
        final String first_name, last_name, email, password, cPassword, username, discipline, institution;
        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (!isNetworkConnected()) {
            Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {

            if (isNetworkConnected()) {
                // setting inputs of editTexts to local variables
                first_name = firstName.getText().toString().trim();
                last_name = lastName.getText().toString().trim();
                email = mEmail.getText().toString().trim();
                username = mUsername.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                cPassword = confirmPassword.getText().toString().trim();
                discipline = mDiscipline.getText().toString().trim();
                institution = String.valueOf(mInstitution.getSelectedItem());

                final String lecturer;
                if (lectureCheckBox.isChecked()){
                    lecturer = "yes";
                } else{
                    lecturer = "no";
                }

                // validating the various fields of registering a user
                if (TextUtils.isEmpty(first_name)){
                    //Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    firstName.setError(getString(R.string.error_field_required));
                    focusView = firstName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(last_name)){
                    //Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    lastName.setError(getString(R.string.error_field_required));
                    focusView = lastName;
                    cancel = true;
                }
                // Check for a valid email address.
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError(getString(R.string.error_field_required));
                    focusView = mEmail;
                    cancel = true;
                } else if (!Util.isValidEmailAddress(email)){
                    mEmail.setError(getString(R.string.error_invalid_email));
                    focusView = mEmail;
                    cancel = true;
                }
                if (TextUtils.isEmpty(username)){
                    //Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    mUsername.setError(getString(R.string.error_field_required));
                    focusView = lastName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(password)){
                    //Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    mPassword.setError(getString(R.string.error_field_required));
                    focusView = mPassword;
                    cancel = true;
                } else if (!TextUtils.isEmpty(password) && !Util.isPasswordValid(password)) {
                    mPassword.setError(getString(R.string.error_invalid_password));
                    focusView = mPassword;
                    cancel = true;
                }
                if (TextUtils.isEmpty(cPassword)){
                    //Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    confirmPassword.setError(getString(R.string.error_field_required));
                    focusView = confirmPassword;
                    cancel = true;
                } else if (!TextUtils.isEmpty(cPassword) && !password.equals(cPassword)){
                    //Toast.makeText(this, "Please make sure your passwords match", Toast.LENGTH_SHORT).show();
                    confirmPassword.setError(getString(R.string.error_confirm_password));
                    focusView = confirmPassword;
                    cancel = true;
                }
                if (TextUtils.isEmpty(discipline)){
                    mDiscipline.setError(getString(R.string.error_field_required));
                    focusView = mDiscipline;
                    cancel = true;
                }
                if (institution.equalsIgnoreCase(getString(R.string.prompt))){
                    Toast.makeText(SignUp.this, "Please select an institution", Toast.LENGTH_SHORT).show();
                    //mInstitution.setError(getString(R.string.error_field_required));
                    focusView = mInstitution;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    //showProgress(true);
                    progressDialog.setMessage("Signing up... ");
                    progressDialog.show();


                    /*mAuthTask = new UserLoginTask(email, password);
                    mAuthTask.execute((Void) null);*/

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            // Generating unique id for user
                            String user_id = mAuth.getCurrentUser().getUid();
                            FirebaseUser user = mAuth.getCurrentUser();
                            // having a reference to the database
                            DatabaseReference current_user_db = databaseReference.child(user_id);//.push();
                                    //current_user_db.child(user_id);
                            current_user_db.child("First Name").setValue(first_name);
                            current_user_db.child("Last Name").setValue(last_name);
                            current_user_db.child("Email").setValue(email);
                            current_user_db.child("Username").setValue(username);
                            current_user_db.child("Discipline").setValue(discipline);
                            current_user_db.child("Institution").setValue(institution);
                            current_user_db.child("Lecturer").setValue(lecturer);
                            //mAuth.signOut();
                            //showProgress(false);
                            progressDialog.dismiss();

                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task.isSuccessful()){
                                    Toast.makeText(SignUp.this, "An email has been sent to " + email
                                            + ". Please verify your email before signing in", Toast.LENGTH_LONG).show();

                                    // show a snack bar telling the user to verify email address
                                    Snackbar.make(mLoginFormView, "An email has been sent to " + email
                                            + ". Please verify your email before signing in", Snackbar.LENGTH_LONG).show();

                                    Intent intent = new Intent(SignUp.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/

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
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
