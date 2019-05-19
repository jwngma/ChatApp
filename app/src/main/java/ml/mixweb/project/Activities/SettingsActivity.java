package ml.mixweb.project.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import ml.mixweb.project.R;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrenrUser;
    private Button mStatusBtn, mImageBtn;

    //
    private TextView mName, mStatus;
    private CircleImageView mDisplayimage;

    private static final int GALLERY_PICK = 1;

    private StorageReference mImageStorage;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayimage = findViewById(R.id.settings_iamge);
        mName = findViewById(R.id.settings_name);
        mStatus = findViewById(R.id.settings_status);
        mImageBtn = findViewById(R.id.settings_Image_btn);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mStatusBtn = findViewById(R.id.settings_status_btn);
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, StatusActivity.class);
                startActivity(intent);

            }
        });

        mCurrenrUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_Uid = mCurrenrUser.getUid();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_Uid);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                if (!image.equals("default")) {
                    //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_image).into(mDisplayimage);
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.circle_small_image).into(mDisplayimage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.circle_small_image).into(mDisplayimage);


                        }
                    });





                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("img/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage(" please wait while we save your Image");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                final File thumb_filepath = new File(resultUri.getPath());

                String current_user_id = mCurrenrUser.getUid();

                final Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxHeight(200)
                        .setMaxWidth(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepath);

                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas);
                final byte[] thumb_byte = boas.toByteArray();

                StorageReference filepath = mImageStorage.child("profile_image").child(current_user_id + ".jpeg");

                final StorageReference thump_filepath = mImageStorage.child("profile_image").child("thumbs").child(current_user_id + ".jpeg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thump_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {
                                        Map update_hashMap=new HashMap();
                                        update_hashMap.put("image",download_url);
                                        update_hashMap.put("thumb_image",thumb_downloadUrl);

                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    progressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Succesfully uploaded", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    } else {
                                        String error = thumb_task.getException().getMessage();
                                        Toast.makeText(SettingsActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();


                                    }

                                }
                            });


                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } else {
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
