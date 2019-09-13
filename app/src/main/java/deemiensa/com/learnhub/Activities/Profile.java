package deemiensa.com.learnhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.Classes.StudentUsers;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.SharedPref;


public class Profile extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;

    EditText tvProgramme, mPhone, tvUserName, mInstitute;
    TextView tvEmail, mName;
    CircleImageView avatar;
    ImageView editBtn;
    LovelyProgressDialog progressDialog;

    private DatabaseReference userDB;
    private FirebaseAuth mAuth;
    private StorageReference storage;
    private StudentUsers myAccount;
    private Context context;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mName = findViewById(R.id.input_first_name);
        tvUserName =  findViewById(R.id.username);
        tvProgramme = findViewById(R.id.input_prog);
        mPhone = findViewById(R.id.input_ref);
        tvEmail = findViewById(R.id.input_user_email);
        avatar = findViewById(R.id.profile_photo);
        avatar.setEnabled(false);
        mInstitute = findViewById(R.id.institution);

        progressDialog = new LovelyProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        storage = FirebaseStorage.getInstance().getReference().child("Profile pics");

        editBtn = findViewById(R.id.edit_btn);
        AtomicBoolean state = new AtomicBoolean(true);
        editBtn.setOnClickListener(v -> {
            if (state.get()){
                disableEditText(state.get());
                editBtn.setImageResource(R.drawable.ic_check_white_24);
                avatar.setEnabled(state.get());
                state.set(false);
            } else if (!state.get()){
                disableEditText(state.get());
                editBtn.setImageResource(R.drawable.ic_edit);
                avatar.setEnabled(state.get());
                uploadProfile();
                state.set(true);
            }
        });

        avatar.setOnClickListener(v -> openGallery());
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

        mName.setText(SharedPref.getProfileName());
        tvUserName.setText(SharedPref.getUsername());
        tvProgramme.setText(SharedPref.getProgramme());
        tvEmail.setText(SharedPref.getEmail());
        mInstitute.setText(SharedPref.getInstitute());
        String phone = SharedPref.getPhone();
        if (phone == null || phone.isEmpty())
            mPhone.setText("Not set");
        else
            mPhone.setText(phone);

        disableEditText(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(Profile.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //realPath = ImageFilePath.getPath(Cook4CashPostActivity.this, resultUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    avatar.setImageBitmap(bitmap);
                    /*Glide.with(Profile.this)
                            .load(bitmap)
                            .asBitmap()
                            .into(avatar);*/
                    uploadImage(resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("TAG", "onActivityResult: " + error.toString());
            }
        }

    }

    private void disableEditText(boolean bool){
        tvUserName.setEnabled(bool);
        mPhone.setEnabled(bool);
        tvProgramme.setEnabled(bool);
        mInstitute.setEnabled(bool);
    }

    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*"); //, video/*
        if (galleryIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), PICK_IMAGE);
        }
    }

    private void uploadImage(Uri uri) {
        //String downloadUri;
        final StorageReference mImageFile = storage.child("cook4cash")
                .child(uri.getLastPathSegment());

        mImageFile.putFile(uri).continueWithTask(task -> {
            // Forward any exceptions
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            Log.d("TAG", "then: upload success");

            // Request the public download URL
            return mImageFile.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String downloadUri = task.getResult().toString();
                SharedPref.saveProfileImage(downloadUri);

                Glide.with(Profile.this)
                        .setDefaultRequestOptions(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .placeholder(R.drawable.ic_avatar)
                                .fitCenter()
                        ).load(downloadUri).into(avatar);

                FirebaseUser user = mAuth.getCurrentUser();
                DatabaseReference current_user_db = userDB.child(user.getUid());
                current_user_db.child("Profile Image").setValue(downloadUri);
            }
        }).addOnSuccessListener(uri1 -> {
            //downloadUri = uri1.toString();
            Log.d("TAG", "onSuccess: " + "upload success");

        }).addOnFailureListener(exception -> {
            // Upload failed
            Toast.makeText(Profile.this, "An error occurred", Toast.LENGTH_LONG).show();
            Log.w("TAG", "uploadFromUri:onFailure", exception);
        });

    }

    private void uploadProfile(){
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        String username = tvUserName.getText().toString();
        String phone = mPhone.getText().toString();
        String institute = mInstitute.getText().toString();
        String discipline = tvProgramme.getText().toString();

        SharedPref.savePhone(phone);
        SharedPref.saveUsername(username);
        SharedPref.saveDept(discipline);
        SharedPref.saveInstitute(institute);

        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference current_user_db = userDB.child(user.getUid());
        current_user_db.child("Username").setValue(username);
        current_user_db.child("Phone Number").setValue(phone);
        current_user_db.child("Institution").setValue(institute);
        current_user_db.child("Discipline").setValue(discipline);

        Toast.makeText(Profile.this, "Profile updated", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }
}
