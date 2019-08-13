package deemiensa.com.learnhub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.App.SkyTubeApp;
import deemiensa.com.learnhub.Classes.HomeVideo;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.businessobjects.db.PlaybackStatusDb;
import deemiensa.com.learnhub.businessobjects.db.Tasks.IsVideoBookmarkedTask;
import deemiensa.com.learnhub.businessobjects.db.Tasks.IsVideoWatchedTask;
import deemiensa.com.learnhub.gui.activities.ThumbnailViewerActivity;
import deemiensa.com.learnhub.gui.businessobjects.MobileNetworkWarningDialog;

public class HomeVideoAdapter extends RecyclerView.Adapter<HomeVideoAdapter.ViewHolder> {

    private Context context;
    private List<HomeVideo> videoList;

    public HomeVideoAdapter(Context context, List<HomeVideo> videoList){
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public HomeVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.channel_video_cell, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeVideoAdapter.ViewHolder viewHolder, int i) {
        final HomeVideo mVideo = videoList.get(i);

        viewHolder.setTitle(mVideo.getTitle());
        viewHolder.setUsername(mVideo.getName());
        viewHolder.setImage(context.getApplicationContext(), mVideo.getThumbnail());
        viewHolder.setTime(mVideo.getTime());

        viewHolder.myMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsButtonClick(v);
            }
        });
    }



    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

       /* CircleImageView image;
        TextView imageName;
        RelativeLayout parentLayout;*/
       FirebaseAuth mAuth;
       ImageView myMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            myMenu = itemView.findViewById(R.id.menu);
        }

        public void setTitle(String title) {
            TextView postTitle = itemView.findViewById(R.id.title);
            postTitle.setText(title);
        }

        /*public void setDesc(String desc) {
            TextView post_desc = itemView.findViewById(R.id.textDesc);
            post_desc.setText(desc);
        }*/

        public void setImage(Context context, String image) {
            ImageView post_image = itemView.findViewById(R.id.preview_pic);
            Glide.with(context).load(image).into(post_image);
        }

        public void setUsername(String username) {
            TextView postUsername = itemView.findViewById(R.id.name);
            postUsername.setText(username);
        }

       /* public void setCategory(String category) {
            TextView postCategory = itemView.findViewById(R.id.category_text_view);
            postCategory.setText(category);
        }*/

        public void setProfileImg(Context context, String profileImg) {
            CircleImageView post_profile = itemView.findViewById(R.id.profile_image);
            Glide.with(context).load(profileImg).into(post_profile);
        }

        public void setTime(String time) {
            TextView post_time = itemView.findViewById(R.id.time);
            post_time.setText(time);
        }

        /*public void setLikes(int number){
            TextView post_like = itemView.findViewById(R.id.likesView);
            post_like.setText(number);
        }*/
    }

    private void onOptionsButtonClick(final View view) {
        final android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.video_options_menu, popupMenu.getMenu());
        //Menu menu = popupMenu.getMenu();

        popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.delete) {
                    LovelyProgressDialog progressDialog = new LovelyProgressDialog(context);
                    progressDialog.setCancelable(true)
                            .setTitle("Deleting Video")
                            .show();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
