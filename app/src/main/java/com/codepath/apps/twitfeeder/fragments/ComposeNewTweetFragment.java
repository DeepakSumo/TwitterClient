package com.codepath.apps.twitfeeder.fragments;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.codepath.apps.twitfeeder.R;
import com.codepath.apps.twitfeeder.models.Tweet;
import com.codepath.apps.twitfeeder.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ComposeNewTweetFragment extends DialogFragment implements View.OnClickListener{


    final int COMPOSE = 0;
    final int REPLY = 1;

    Tweet tweet = new Tweet();
    User self = new User();
    String title = "";
    int operation;


    public interface NewTweetDialogListener {
        void onFinishPostingTweet(Tweet newTweet);
    }

    private NewTweetDialogListener listener;

    @Bind(R.id.etTweetText) EditText mtweetText;
    @Bind(R.id.saveTweet) Button mSendTweet;
    @Bind(R.id.ivUserProfile) ImageView mProfileImage;
    @Bind(R.id.tvUserName) TextView mUserName;
    @Bind(R.id.tvScreenName) TextView mScreenName;
    @Bind(R.id.ivCancel) ImageView mCancel;
    @Bind(R.id.tvNumCharacters) TextView mNumCharacters;

    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.ivCancel:
                dismiss();
                break;

            case R.id.saveTweet:
                saveTweet();
                break;
        }

    }

    private void saveTweet() {
        tweet.setTweetText(mtweetText.getText().toString());
        listener.onFinishPostingTweet(tweet);
        dismiss();
    }

    public ComposeNewTweetFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeNewTweetFragment newInstance(Tweet tweet, String title, int operation, User self) {
        ComposeNewTweetFragment frag = new ComposeNewTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable("tweet", tweet);
        args.putParcelable("self", self);
        args.putString("title", title);
        args.putInt("operation", operation);
        frag.setArguments(args);
        return frag;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setCustomObjectListener(NewTweetDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        //dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.Dialog);

        View view = inflater.inflate(R.layout.compose_new_tweet, container);

        tweet = getArguments().getParcelable("tweet");

        self = getArguments().getParcelable("self");

        title = getArguments().getString("title");

        operation = getArguments().getInt("operation");

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String profileUrl = self.getProfile_image_url();
        mUserName.setText(self.getName());
        mScreenName.setText("@" + self.getScreenName());

        if(operation == REPLY){
            title = title + " "+tweet.getUser().getName();
            mtweetText.setText("@" + tweet.getUser().getScreenName() + " ");
        }

        getDialog().setTitle(title);

        Glide.with(getActivity())
                .load(profileUrl)
                .asBitmap()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(new BitmapImageViewTarget(mProfileImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        //circularBitmapDrawable.setCircular(true);
                        circularBitmapDrawable.setCornerRadius(10);
                        //circularBitmapDrawable.setCornerRadius(Math.max(resource.getWidth(), resource.getHeight()) / 2.0f);
                        mProfileImage.setImageDrawable(circularBitmapDrawable);
                    }
                });


        mCancel.setOnClickListener(this);

        mSendTweet.setOnClickListener(this);

        mtweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charactersLeft = 140 - s.length();
                //Log.i("info", "Count: " + charactersLeft);
                mNumCharacters.setText(String.valueOf(charactersLeft));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
