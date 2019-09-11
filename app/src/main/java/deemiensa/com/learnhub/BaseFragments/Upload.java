package deemiensa.com.learnhub.BaseFragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

import deemiensa.com.learnhub.Activities.FilePreview;
import deemiensa.com.learnhub.Activities.VideoPreview;
import deemiensa.com.learnhub.Adapters.Adapter_VideoFolder;
import deemiensa.com.learnhub.App.BaseFragment;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.Model_Video;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Upload extends BaseFragment {
    // global and static variables
    private Button uploadVideoBtn, uploadDocsBtn;
    private TextView mTextView;
    private View mVideo, mDocs;
    private RecyclerView recyclerView;
    private static final String TAG = "Upload Activity";
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 100;
    private static final int PICKFILE_REQUEST_CODE = 6384;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    private static final int REQUEST_PERMISSIONS = 100;
    Adapter_VideoFolder obj_adapter;
    ArrayList al_video = new ArrayList<>();

    // other global variables
    public static Uri selectedImageUri;
    String realPath;
    String fileManagerString, selectedVideoPath;

    public Upload() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        // Binding views in xml
        uploadDocsBtn = view.findViewById(R.id.docsBtn);
        uploadVideoBtn = view.findViewById(R.id.videoBtn);
        mVideo = view.findViewById(R.id.video_view);
        mDocs = view.findViewById(R.id.doc_view);
        //mTextView = view.findViewById(R.id.textView);

        // setting up the recycler view
        recyclerView =  view.findViewById(R.id.recycler_view1);
        recyclerViewLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        fn_checkpermission();


        // setting onClickListener for uploading videos
        uploadVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadVideoFromGallery();
                mVideo.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLogin));
                mDocs.setBackgroundColor(getResources().getColor(R.color.white));
                fn_checkpermission();
            }
        });

        uploadDocsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFileIntent();
                /*mDocs.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLogin));
                mVideo.setBackgroundColor(getResources().getColor(R.color.white));*/
            }
        });

        return view;
    }


    private void fn_checkpermission(){
        /*RUN TIME PERMISSIONS*/

        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {
                fn_video();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            fn_video();
        }
    }


    public void fn_video() {

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name,column_id,thum;

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media._ID,MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            Log.e("column_id", cursor.getString(column_id));
            Log.e("thum", cursor.getString(thum));

            Model_Video obj_model = new Model_Video();
            obj_model.setBoolean_selected(false);
            obj_model.setStr_path(absolutePathOfImage);
            obj_model.setStr_thumb(cursor.getString(thum));

            al_video.add(obj_model);

        }


        obj_adapter = new Adapter_VideoFolder(getContext(),al_video, getActivity());
        recyclerView.setAdapter(obj_adapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    fn_video();
                } else {
                    Toast.makeText(getContext(), "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                }
            }
        }
    }

    /**
     *
     * a function to open the galleryIntent on a phone to choose the video
     */
    public void uploadVideoFromGallery(){
        openGallery();
    }

    private void openGallery(){

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("video/*");
        startActivityForResult(pickIntent, REQUEST_TAKE_GALLERY_VIDEO);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                selectedImageUri = data.getData();

                // OI FILE Manager
                fileManagerString = selectedImageUri.getPath();

                // MEDIA GALLERY
                selectedVideoPath = getPath(selectedImageUri);
                if (selectedImageUri != null) {
                    Log.d(TAG, selectedVideoPath);
                    // send the selected video path to the next activity for previewing
                    Intent intent = new Intent(getActivity(), VideoPreview.class);
                    intent.putExtra("path", selectedImageUri);
                    startActivity(intent);
                }
            }

            if (requestCode == PICKFILE_REQUEST_CODE){
                Uri uri = data.getData();
                /*if (uri != null){
                    String pathFile = uri.toString();
                    if (pathFile.substring(0, 1).equals("c")){
                        pathFile = "file://" + pathFile.substring(28, pathFile.length());
                    }
                    if (pathFile.toLowerCase().startsWith("file://")){
                        pathFile = (new File(URI.create(pathFile))).getAbsolutePath();
                    }

                    realPath = pathFile;
                }*/

                /*String doctype = getMimeType(uri.toString());
                Log.d("DOCTYPE", "type is: "+doctype);*/

                File file = new File(String.valueOf(uri));

                //try {
                    //URLConnection connection = file.toURL().openConnection();
                    String mimetype = URLConnection.guessContentTypeFromName(file.getName());
                    Log.d(TAG, "onActivityResult: " + mimetype);
                /*} catch (IOException e) {
                    e.printStackTrace();
                }*/

                //String src = getPath(uri);
                String src = uri.getPath();

                //mTextView.setText(src);
                //mTextView.setVisibility(View.VISIBLE);

                Intent intent = new Intent(getActivity(), FilePreview.class);
                intent.putExtra("path", src);
                intent.putExtra("fileUri", /*realPath*/ uri.toString());
                intent.putExtra("mimetype", mimetype);
                startActivity(intent);
            }
        }
    }

    // UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public String getFilePath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void uploadFileIntent(){
        //openFileIntent();
        browseDocuments();
    }

    public void openFileIntent(){
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICKFILE_REQUEST_CODE);
    }


    private void browseDocuments(){

        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
                //Log.d(TAG, "browseDocuments: " + mimeType);
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), PICKFILE_REQUEST_CODE); //REQUEST_CODE_DOC
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
