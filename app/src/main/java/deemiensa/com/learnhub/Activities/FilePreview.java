package deemiensa.com.learnhub.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Tasks.MyDocsUploadService;

import static deemiensa.com.learnhub.Utils.SharedPref.getProfileName;
import static deemiensa.com.learnhub.Utils.SharedPref.getProfilePic;
import static deemiensa.com.learnhub.Utils.SharedPref.getProgramme;
import static deemiensa.com.learnhub.Utils.SharedPref.getSavedDept;
import static deemiensa.com.learnhub.Utils.SharedPref.getSpeciality;
import static deemiensa.com.learnhub.Utils.SharedPref.isLecturer;

public class FilePreview extends AppCompatActivity {
    private TextView mTextView, mCount, mName, mProg, mHelp, mDept;
    private Button mUpload;
    private String filePath, fileUri, title, description, category, mimetype;
    private EditText mTitle, mDesc;
    private CircleImageView mProfilePic;
    private RelativeLayout mDisciplineBtn;
    private ProgressDialog mProgressDialog;

    private Uri mFileUri = null;
    private Uri mDownloadUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_preview);

        mTextView = findViewById(R.id.textView);
        mUpload = findViewById(R.id.file_upload);
        mTitle = findViewById(R.id.title);
        mDesc = findViewById(R.id.description);
        mCount = findViewById(R.id.title_counter);
        mName = findViewById(R.id.name_text);
        mProg = findViewById(R.id.programme);
        mProfilePic = findViewById(R.id.profile_pic);
        mHelp = findViewById(R.id.help);
        mDept = findViewById(R.id.category);
        mDisciplineBtn = findViewById(R.id.discipline_btn);

        filePath = getIntent().getStringExtra("path");
        fileUri = getIntent().getStringExtra("fileUri");
        mimetype = getIntent().getStringExtra("mimetype");

        mTextView.setText(filePath);

        mDisciplineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FilePreview.this, Departments.class));
            }
        });

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = mTitle.getText().toString();
                mCount.setText(text.length() + "/100");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mTitle.getText().toString();
                mCount.setText(text.length() + "/100");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mTitle.getText().toString();
                mCount.setText(text.length() + "/100");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // initialize views with data from database
        if (!isLecturer()){
            mName.setText(getProfileName());
            mProg.setText(getProgramme());
        } else {
            mName.setText(getProfileName());
            mProg.setText(getSpeciality());
        }

        Glide.with(this).setDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.ic_avatar)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .fitCenter()
        ).load(getProfilePic()).into(mProfilePic);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getSavedDept() == null){
            mDept.setText(getProgramme());
        } else {
            mDept.setText(getSavedDept());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video, menu);
        return true; //super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_post) {
            boolean cancel = false;
            View focusView = null;

            title = mTitle.getText().toString().trim();
            description = mDesc.getText().toString().trim();
            category = mDept.getText().toString().trim();
            //filePrefix = title;

            if (TextUtils.isEmpty(title)){
                mTitle.setError(getString(R.string.error_field_required));
                focusView = mTitle;
                cancel = true;
            }

            if (TextUtils.isEmpty(category)){
                mDept.setError(getString(R.string.error_field_required));
                focusView = mDept;
                cancel = true;
            }

            if (cancel){
                focusView.requestFocus();
            } else {
                File file = new File(fileUri);
                uploadFileToDatabase(Uri.parse(fileUri), title, description, category);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void uploadFileToDatabase(Uri uri, final String title, final String desc, final String category){
        //showProgress();

        // Save the File URI
        mFileUri = uri;

        // Clear the last download, if any
        //updateUI(mAuth.getCurrentUser());
        mDownloadUrl = null;

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyDocsUploadService.class)
                .putExtra(MyDocsUploadService.EXTRA_FILE_URI, uri)
                .putExtra("title", title)
                .putExtra("desc", desc)
                .putExtra("category", category)
                .putExtra("mimetype", mimetype)
                .setAction(MyDocsUploadService.ACTION_UPLOAD));

        // Show loading spinner
//        showProgressDialog(getString(R.string.progress_uploading));

        finish();
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
