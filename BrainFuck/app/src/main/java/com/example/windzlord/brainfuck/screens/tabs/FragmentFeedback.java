package com.example.windzlord.brainfuck.screens.tabs;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.windzlord.brainfuck.MainActivity;
import com.example.windzlord.brainfuck.R;
import com.example.windzlord.brainfuck.managers.ManagerFile;
import com.example.windzlord.brainfuck.managers.ManagerPreference;
import com.example.windzlord.brainfuck.managers.ManagerServer;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFeedback extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.button_feedback_facebook)
    LoginButton buttonFacebook;

    public FragmentFeedback() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab_fragment_feedback, container, false);
        settingThingsUp(view);

        return view;
    }

    private void settingThingsUp(View view) {
        ButterKnife.bind(this, view);

        settingFacebook();
    }


    public void settingFacebook() {
        AppEventsLogger.activateApp(getContext());
        buttonFacebook.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        buttonFacebook.registerCallback(
                ((MainActivity) getActivity()).getCallbackManager(),
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "onSuccess");
                        new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                if (currentProfile != null) {
                                    ManagerPreference.getInstance().putUserID(currentProfile.getId());
                                    ManagerPreference.getInstance().putUserName("N'" + currentProfile.getName() + "'");

                                    ManagerServer.getInstance().checkExistedUser(currentProfile.getId());
                                    //load Image
                                    String url = currentProfile.getProfilePictureUri(300, 300).toString();
                                    new DownloadImage().execute(url);
                                } else {
                                    ManagerServer.getInstance().uploadLocalToServer(
                                            ManagerPreference.getInstance().getUserID());
                                    ManagerPreference.getInstance().putUserID("");
                                    ManagerPreference.getInstance().putUserName("N'Guest'");
                                }
                            }
                        }.startTracking();
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "onError");
                    }
                });
    }

    private class DownloadImage extends AsyncTask<Object, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Object... object) {
            String sURL = (String) object[0];
            try {
                InputStream in = (InputStream) new URL(sURL).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();
                return bitmap;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ManagerFile.getInstance().createImage(result, ManagerPreference.getInstance().getUserID());
        }
    }
}

