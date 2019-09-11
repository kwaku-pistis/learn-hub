package deemiensa.com.learnhub.BaseFragments;


import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import deemiensa.com.learnhub.Activities.Users;
import deemiensa.com.learnhub.App.BaseFragment;
import deemiensa.com.learnhub.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Channels extends BaseFragment {

    private FloatingActionButton mFab;

    public Channels() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channels, container, false);

        mFab = view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Users.class));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
