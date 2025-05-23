package com.meridianmaps;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arubanetworks.meridian.locationsharing.Friend;
import com.arubanetworks.meridian.locationsharing.Invite;
import com.arubanetworks.meridian.locationsharing.LocationSharing;
import com.arubanetworks.meridian.locationsharing.LocationSharingException;
import com.arubanetworks.meridian.locationsharing.User;
import com.arubanetworks.meridian.util.Icons;
import com.arubanetworks.meridian.editor.EditorKey;

import android.util.Log;

import java.util.List;

// How to create a location sharing profile and share location with friends
public class LocationSharingFragment extends Fragment implements LocationSharing.Listener {

    private static final int IMAGE_REQUEST_FOR_USER_IMAGE = 0x0000000F;
    private Button startPostingLocationUpdates;

    public static LocationSharingFragment newInstance() {
        return new LocationSharingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appId = getArguments() != null ? getArguments().getString("APP_KEY") : null;
        if (appId == null) {
            throw new IllegalStateException("Missing APP_KEY in fragment arguments");
        }
        LocationSharing.initWithAppKey(EditorKey.forApp(appId));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_sharing_example, container, false);

        final LinearLayout loggedOutLayout = rootView.findViewById(R.id.location_sharing_logged_out);
        final LinearLayout loggedInLayout = rootView.findViewById(R.id.location_sharing_logged_in);

        Button createProfileButton = rootView.findViewById(R.id.location_sharing_create_profile);
        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User sampleUser = new User();
                sampleUser.setFullName("Sample User");

                LocationSharing.shared().createUser(sampleUser, new LocationSharing.Callback<User>() {
                    @Override
                    public void onSuccess(User user) {

                        loggedOutLayout.setVisibility(View.GONE);
                        loggedInLayout.setVisibility(View.VISIBLE);
                        if (getActivity() != null) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("User created successfully!")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(LocationSharingException t) {
                        if (getActivity() != null) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("Unable to create user")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }
                });
            }
        });

        Button acceptInviteButton = rootView.findViewById(R.id.location_sharing_accept_invite);
        acceptInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    final EditText edittext = new EditText(getActivity());
                    alert.setMessage("Accept Invite");
                    alert.setView(edittext);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            LocationSharing.shared().acceptInvite(edittext.getText().toString(), new LocationSharing.Callback<Friend>() {
                                @Override
                                public void onSuccess(Friend friend) {
                                    if (getActivity() != null) {
                                        new AlertDialog.Builder(getActivity())
                                                .setMessage(friend.getFullName() + " added as a friend!")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                }

                                @Override
                                public void onError(LocationSharingException t) {
                                    if (getActivity() != null) {
                                        new AlertDialog.Builder(getActivity())
                                                .setMessage("Unable to accept invite")
                                                .setPositiveButton("OK", null)
                                                .show();
                                    }
                                }
                            });
                        }
                    });
                    alert.show();
                }
            }
        });
        startPostingLocationUpdates = rootView.findViewById(R.id.location_sharing_start_updating_location);

        // optionally, we can set a listener so we know when the service is running
        LocationSharing.shared().addListener(this);

        // NOTE: on Android 10+ you need to present the user with a dialog to accept the ACCESS_BACKGROUND_LOCATION permission before starting
        //       location sharing so it will work in the background
        startPostingLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    // we are using the same button to start/stop posting, so we need to check if we are already posting or not
                    // before starting/stopping it
                    if (LocationSharing.shared().isUploadingServiceRunning()) {
                        LocationSharing.shared().stopPostingLocationUpdates(getActivity().getApplicationContext());
                    } else {
                        LocationSharing.shared().startPostingLocationUpdates(getActivity().getApplicationContext());
                    }
                }
            }
        });

        final Button createInviteButton = rootView.findViewById(R.id.location_sharing_create_invite);

        createInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationSharing.shared().createInvite(new LocationSharing.Callback<Invite>() {
                    @Override
                    public void onSuccess(Invite result) {
                        if (getActivity() != null) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("Invited created. URL: " + result.getShareUrl())
                                    .setPositiveButton("OK", null)
                                    .show();

                            // you can share the invite URL here
                        }
                    }

                    @Override
                    public void onError(LocationSharingException e) {
                        // do something
                    }
                });
            }
        });

        final Button retrieveFriendsButton = rootView.findViewById(R.id.location_sharing_retrieve_friends);

        retrieveFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationSharing.shared().getFriends(new LocationSharing.Callback<List<Friend>>() {
                    @Override
                    public void onSuccess(List<Friend> result) {
                        if (getActivity() != null) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("Success! You have " + result.size() + " friends")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(LocationSharingException e) {
                        // do something
                    }
                });


            }
        });

        final Button uploadUserImageButton = rootView.findViewById(R.id.location_sharing_upload_user_image);
        uploadUserImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_REQUEST_FOR_USER_IMAGE);
            }
        });

        if (LocationSharing.shared().getCurrentUser() != null) {
            loggedOutLayout.setVisibility(View.GONE);
            loggedInLayout.setVisibility(View.VISIBLE);
            if (LocationSharing.shared().isUploadingServiceRunning()) {
                startPostingLocationUpdates.setText(getString(R.string.location_sharing_stop_updating_location));
            }
        }

        /*
        // How to use a Meridian image in your application
        if (getActivity() != null) {
            ImageView demoImage = rootView.findViewById(R.id.demo_image);
            demoImage.setVisibility(View.VISIBLE);
            demoImage.setImageDrawable(Icons.Type.SHOE_SHINE.getIconDrawable(getActivity(), 32, Color.GREEN));
        }
        */

        return rootView;
    }

    @Override
    public void onPostingLocationUpdatesStarted() {
        if (getActivity() != null) {
            startPostingLocationUpdates.setText(getString(R.string.location_sharing_stop_updating_location));
        }
    }

    @Override
    public void onFriendsUpdated(List<Friend> friends) {}

    @Override
    public void onPostingLocationUpdatesStopped() {
        if (getActivity() != null) {
            startPostingLocationUpdates.setText(getString(R.string.location_sharing_start_updating_location));
        }
    }

    private void uploadURIasUserImage(Uri uri){
        if(uri == null || getContext() == null || getActivity() == null)return;
        LocationSharing.shared().uploadUserPhoto(getContext(), uri, new LocationSharing.Callback<User>() {
            @Override
            public void onSuccess(User result) {
                if (getActivity() != null) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Success! image uploaded.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onError(LocationSharingException t) {
                if (getActivity() != null) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(String.format("Error uploading image. %s", t.getErrorMessage()))
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK)return;
        if (requestCode == IMAGE_REQUEST_FOR_USER_IMAGE) {
            uploadURIasUserImage(data.getData());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationSharing.shared().setMode(LocationSharing.Mode.FOREGROUND);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationSharing.shared().setMode(LocationSharing.Mode.BACKGROUND);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocationSharing.shared().removeListener(this);
    }
}
