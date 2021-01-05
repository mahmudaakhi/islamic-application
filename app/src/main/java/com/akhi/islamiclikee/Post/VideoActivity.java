package com.akhi.islamiclikee.Post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.akhi.islamiclikee.R;

public class VideoActivity extends AppCompatActivity {
ActionBar actionBar;
    private String[] camera_Permission;
    private Uri video_uri;
    String[] Options;
    int i;
    //camera ar gallery code
    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 100;


    EditText titlevideo;
    Button uploadvideo;
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        uploadvideo=findViewById(R.id.uploadvideo);
        videoView=findViewById(R.id.videoadd);
        titlevideo=findViewById(R.id.titlevideo);
        //INIT ACTIONBAR
        actionBar = getSupportActionBar();
//set title
        actionBar.setTitle("Add New Video");
        //add back button\

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        uploadvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //camera permission
        camera_Permission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //handle gallery
    }


    //camera permission request
   // private void RequestCameraPermission() {
        //request camera permission
  //      ActivityCompat.requestPermissions(this, camera_Permission, CAMERA_REQUEST_CODE);
  //  }

  //  private boolean CheckCameraPermission() {
     //   boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    //    boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
     //   return result1 && result2;
   // }


    private void VideoPickGallery() {
        //pick gallery from
        Intent intent = new Intent();
        intent.setType("video/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select video"), VIDEO_PICK_GALLERY_CODE);

   }

    private void VideoPickCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent1, VIDEO_PICK_GALLERY_CODE);

   }

    public void OnRequestPermissionResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    //check permission allowed or not
                    boolean CameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (CameraAccepted && StorageAccepted) {
                        //both permission allowed
                        VideoPickCamera();
                    } else {
                        Toast.makeText(this, "camera and storage permission are required", Toast.LENGTH_SHORT).show();
                    }

                }
        }
        super.onRequestPermissionsResult(requestCode, permission, grantResults);

    }


    protected void OnActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode==RESULT_OK){
            if (requestCode==VIDEO_PICK_GALLERY_CODE){
                video_uri=data.getData();

                //show picked video in videoview
                setVideoToVideoView();
            }
            else if(requestCode==VIDEO_PICK_CAMERA_CODE){
                video_uri=data.getData();
                setVideoToVideoView();

            }
        }
        super.onActivityResult(requestCode,resultCode,data);

    }

    private void setVideoToVideoView() {
        MediaController mediaController=new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video_uri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.pause();
            }
        });

    }      //set media controller to video view

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}
