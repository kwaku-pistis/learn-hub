package deemiensa.com.learnhub.BaseFragments;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import deemiensa.com.learnhub.Activities.Feedback;
import deemiensa.com.learnhub.Activities.MyChannel;
import deemiensa.com.learnhub.Activities.Terms;
import deemiensa.com.learnhub.App.BaseFragment;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends BaseFragment {

    private RelativeLayout mHeader;
    private LinearLayout mTerms, mHelp, myChannel;
    private CircleImageView mProfileImg;
    private TextView mName;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mHeader = view.findViewById(R.id.header);
        mHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), deemiensa.com.learnhub.Activities.Profile.class));
            }
        });

        mProfileImg = view.findViewById(R.id.profile_image);
        mName = view.findViewById(R.id.profile_name);
        mTerms = view.findViewById(R.id.terms);
        mHelp = view.findViewById(R.id.help);
        myChannel = view.findViewById(R.id.my_channel);

        myChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyChannel.class));
            }
        });

        mTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Terms.class));
            }
        });

        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Feedback.class));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        mName.setText(SharedPref.getProfileName());
        Glide.with(getActivity().getApplicationContext())
                .setDefaultRequestOptions(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.ic_avatar)
                        .fitCenter()
                ).load(SharedPref.getProfilePic()).into(mProfileImg);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();


    }
}
