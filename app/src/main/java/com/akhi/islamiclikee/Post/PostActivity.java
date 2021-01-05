package com.akhi.islamiclikee.Post;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.akhi.islamiclikee.Home;
import com.akhi.islamiclikee.R;
import com.akhi.islamiclikee.Utils.methods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {
    ImageView postNow, backFromPost, addedImage;
    EditText addedCaption, AddedTag;
    FloatingActionButton videobtn;
    DatabaseReference databaseReference, data;
    StorageReference storageReference, ref;

    methods method;
    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 100;


    String[] Options;
int i;
    int count = 0;
    int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    String RandomUId, userId;
    String postCount;
    String caption, tags;
    private String[] camera_Permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        videobtn = findViewById(R.id.video);
        postNow = (ImageView) findViewById(R.id.post_now);
        backFromPost = (ImageView) findViewById(R.id.back_from_post);
        addedImage = (ImageView) findViewById(R.id.added_image);
        addedCaption = (EditText) findViewById(R.id.added_caption);
        AddedTag = (EditText) findViewById(R.id.added_tags);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        method = new methods();
        count = getCount();
        camera_Permission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        videobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//video koi theke pick korbo seta set korlam
                VideoPickDialog();

                //intent
                Intent intent = new Intent(PostActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
    }
    private void VideoPickDialog() {
        String[] Option={"Camera","Gallery"};
        //alert dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From")
                .setItems(Options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (i==0){
//camera click
                            if (!CheckCameraPermission()){
                                //camera permission not allowed request it
                                RequestCameraPermission();
                            }
                            else{
                                VideoPickCamera();
                            }
                        }
                        else if(i==1){
                            //gallery click
                            VideoPickGallery();

                        }
                    }

                }).show();
    }

    private boolean CheckCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }
    private void RequestCameraPermission() {
        //request camera permission
        ActivityCompat.requestPermissions(this, camera_Permission, CAMERA_REQUEST_CODE);
    }
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            addedImage.setImageURI(imageUri);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadimage() {
        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(PostActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            caption = addedCaption.getText().toString().trim();
            tags = AddedTag.getText().toString().trim();

            RandomUId = UUID.randomUUID().toString();
            userId = FirebaseAuth.getInstance().getUid();
            ref = storageReference.child("photos/users/"+"/"+userId+"/photo"+(count+1));
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            increasePostCount(count);
                            addPost(caption, getTimestamp(), String.valueOf(uri), RandomUId, userId, tags);
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Posted successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PostActivity.this,Home.class));
                            finish();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostActivity.this,Home.class));
                    finish();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            });

        }

    }

    //******************************FUNCTION TO ADD PHOTO TO FIREBASE STORAGE********
    public void addPost(String caption, String date_Created, String image_Path, String photo_id, String user_id, String tags){

        HashMap<String, String> hashMappp = new HashMap<>();
        hashMappp.put("caption", caption);
        hashMappp.put("date_Created", date_Created);
        hashMappp.put("image_Path", image_Path);
        hashMappp.put("photo_id", photo_id);
        hashMappp.put("tags", tags);
        hashMappp.put("user_id", user_id);
        databaseReference.child("User_Photo").child(user_id).child(photo_id).setValue(hashMappp);
        databaseReference.child("Photo").child(photo_id).setValue(hashMappp);


    }

    //******************************FUNCTION TO INCREASE POST COUNT********
    public void increasePostCount(final int count){


        data = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postCount = Integer.toString(count+1);
                data.child("posts").setValue(postCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    //******************************FUNCTION TO GET POST TIME********
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
    //******************************FUNCTION TO GET POST Count********
    public int getCount() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = method.getImagecount(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return count;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}