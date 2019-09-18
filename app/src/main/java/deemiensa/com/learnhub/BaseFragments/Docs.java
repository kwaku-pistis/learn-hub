package deemiensa.com.learnhub.BaseFragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import deemiensa.com.learnhub.App.BaseFragment;
import deemiensa.com.learnhub.Classes.Documents;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class Docs extends BaseFragment {


    public Docs() {
        // Required empty public constructor
    }

    private RecyclerView mDocList;
    private SwipeRefreshLayout refreshLayout;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private LinearLayoutManager linearLayoutManager;
    List<Documents> docList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_docs, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        mDocList = view.findViewById(R.id.doc_list);
        docList = new ArrayList<>();

        mDocList.setHasFixedSize(true);
        mDocList.setLayoutManager(linearLayoutManager);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("File Posts");
        mDatabase.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //loadVideo();

        FirebaseRecyclerOptions<Documents> options =
                new FirebaseRecyclerOptions.Builder<Documents>()
                        .setQuery(mDatabase, Documents.class)
                        .build();

        FirebaseRecyclerAdapter<Documents, DocsViewHolder> FBRA = new FirebaseRecyclerAdapter<Documents, DocsViewHolder>(
                options
        ) {
            @NonNull
            @Override
            public DocsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.doc_cell, parent, false);

                return new DocsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull DocsViewHolder viewHolder, int position, @NonNull final Documents model) {
                // setting values to the viewholder of the recyclerview
                viewHolder.setTitle(model.getTitle());
                viewHolder.setUsername(model.getName());
                viewHolder.setTime(model.getTime(), model.getPostTime());
                viewHolder.setThumbnail(getActivity().getApplicationContext(), model.getMimetype());

                viewHolder.view.setOnClickListener(v -> {
                    // another method to open link in a browser
                    // openWebPage(model.getDownloadUrl());
                    openDocument(model.getDownloadUrl(), model.getMimetype());
                });
            }
        };
        FBRA.startListening();
        mDocList.setAdapter(FBRA);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static class DocsViewHolder extends RecyclerView.ViewHolder {
        FirebaseAuth mAuth;
        RelativeLayout view;

        public DocsViewHolder(View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            view = itemView.findViewById(R.id.main);
        }

        public void setTitle(String title) {
            TextView postTitle = itemView.findViewById(R.id.title);
            postTitle.setText(title);
        }

        public void setDuration(int desc) {
            TextView post_desc = itemView.findViewById(R.id.video_duration_text_view);
            post_desc.setText(Util.getTime(desc));
        }

        public void setThumbnail(Context context, String mimetype) {
            ImageView post_image = itemView.findViewById(R.id.preview_pic);

            switch (mimetype){
                case "application/pdf":
                    Glide.with(context)
                            .setDefaultRequestOptions(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .placeholder(R.drawable.document)
                                    .fitCenter()
                            ).load(R.drawable.pdf).into(post_image);
                    break;

                case "application/msword":
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                    Glide.with(context)
                            .setDefaultRequestOptions(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .placeholder(R.drawable.document)
                                    .fitCenter()
                            ).load(R.drawable.document).into(post_image);
                    break;

                case "application/vnd.ms-excel":
                case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                    Glide.with(context)
                            .setDefaultRequestOptions(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .placeholder(R.drawable.document)
                                    .fitCenter()
                            ).load(R.drawable.excel).into(post_image);
                    break;

                case "application/vnd.ms-powerpoint":
                case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                    Glide.with(context)
                            .setDefaultRequestOptions(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .placeholder(R.drawable.document)
                                    .fitCenter()
                            ).load(R.drawable.pptx).into(post_image);
                    break;

                    default:
                        Glide.with(context)
                                .setDefaultRequestOptions(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .placeholder(R.drawable.document)
                                        .fitCenter()
                                ).load(R.drawable.icon_doc).into(post_image);
                        break;
            }

            /*Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .placeholder(R.drawable.thumbnail_default)
                            .fitCenter()
                    ).load(image).into(post_image);*/
        }

        public void setUsername(String username) {
            TextView postUsername = itemView.findViewById(R.id.name);
            postUsername.setText(username);
        }

        public void setCategory(String category) {
            TextView postCategory = itemView.findViewById(R.id.views_text_view);
            postCategory.setText(category);
        }

        public void setTime(String time, String model_time) {

            String timestampDiff = Util.getTimestampDifference(time);

            TextView post_time = itemView.findViewById(R.id.time);
            if(!timestampDiff.equals("0")){
                post_time.setText(timestampDiff + " DAYS AGO");
            }else{
                post_time.setText(model_time);
            }
        }
    }

    public void openDocument(String name, String m_type) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            //intent.setDataAndType(Uri.fromFile(file), "text/*");
            /*Uri apkURI = FileProvider.getUriForFile(
                    getContext(),
                    getActivity().getApplicationContext()
                            .getPackageName() + ".provider", file);*/
            intent.setDataAndType(Uri.parse(name), m_type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            //intent.setDataAndType(Uri.fromFile(file), mimetype);

           /* Uri apkURI = FileProvider.getUriForFile(
                    getContext(),
                    getActivity().getApplicationContext()
                            .getPackageName() + ".provider", file);*/
            //intent.setDataAndType(apkURI, mimetype);
            intent.setDataAndType(Uri.parse(name), m_type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        // custom message for the intent
        startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
