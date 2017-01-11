package com.example.windzlord.brainfuck.screens.tabs;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.windzlord.brainfuck.R;
import com.example.windzlord.brainfuck.managers.FileManager;
import com.example.windzlord.brainfuck.managers.ManagerPreference;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends Fragment {

    @BindView(R.id.textView_user_name)
    TextView user_name;

    @BindView(R.id.image_user)
    ImageView image_user;

    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout1 for this fragment
        View view = inflater.inflate(R.layout.tab_fragment_profile, container, false);
        settingThingsUp(view);
        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);

        setupUI();
    }

    public void setupUI() {
        user_name.setText(ManagerPreference.getInstance().getUserName());
        String userID = ManagerPreference.getInstance().getUserID();
        if(!userID.equals("")){
            File file = FileManager.getInstance().loadImage(userID);
            ImageLoader.getInstance().displayImage(
                    Uri.fromFile(file).toString(),
                    image_user
            );
        } else {
            image_user.setImageResource(R.drawable.z_character_guest);
        }
    }
}
