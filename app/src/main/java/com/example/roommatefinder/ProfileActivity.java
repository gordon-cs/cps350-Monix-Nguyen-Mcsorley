package com.example.roommatefinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePic;
    private TextView profileName, profileClass, profileEmail, profileGender;
    private Button profileUpdate, changePassword,profilepicUpdate;
    private File userPic;

    //1st step: import firebase auth and database
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    static UserProfile userProfile;


    //Constants
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //variables
    private Uri photoURI;

    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileName = findViewById(R.id.tvProfileName);
        profileEmail = findViewById(R.id.tvProfileEmail);
        profileClass = findViewById(R.id.tvProfileClass);
        profileGender = findViewById(R.id.tvProfileGender);
        changePassword = findViewById(R.id.btnChangePassword);
        profileUpdate = findViewById(R.id.btnProfileUpdate);
        profilepicUpdate = findViewById(R.id.btnPicUpdate);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //2nd step: get instance of firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //3rd step: create reference to database, give it object name
        //parameter should be userID
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        //4th step: retrieve data with ValueEventListener - this will update everytime database change
        //https://firebase.google.com/docs/database/admin/retrieve-data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //when theres data change or when app starts
                userProfile = dataSnapshot.getValue(UserProfile.class);

                profileName.setText(userProfile.getUserName());
                profileEmail.setText(userProfile.getUserEmail());
                profileClass.setText(userProfile.getUserClass());
                profileGender.setText(userProfile.getUserGender());


                try{
                    displayLocalPic();
                }
                catch (Exception e){
                    Toast.makeText(ProfileActivity.this,"No local Photo",Toast.LENGTH_LONG).show();
                }
                try{
                    downloadPicture();
                }
                catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "No Photo to Load", Toast.LENGTH_LONG).show();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        profilepicUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdateProfile.class));
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdatePassword.class));
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home: //when click on top direct to previous act
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }





    //Take a picture using premade pictureIntent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //checking to make sure that there is a camera intent or camera that can be used
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photo = null;

            try{ photo = createImageFile(); }
            catch (IOException e) {
                Log.e("IO Exception","DispatchTakePicture CreateImage Failed");
            }

            if( photo != null ) {
                //Get Photo location
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.roommatefinder.fileprovider",
                        photo);
                //Store Picture
                userProfile.setUserPhoto(photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, userProfile.getUserPhoto());
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }
    }

    // method that is executed when startActivityForResult() returns
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            this.upload();
            this.displayLocalPic();
        }
    }

    // Creates an image file in app's internal storage
    private File createImageFile() throws IOException {
        // Create a unique image file name
        String imageFileName = this.profileName.getText().toString(); // must be changed to unique id
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    // Upload photo to Firebase
    private void upload ()
    {
        //Creating a tag for file in firebase
        StorageReference ref = storageReference.child(firebaseAuth.getUid()+ "/" + "Users");


        //putting file in firebase
        ref.putFile(userProfile.getUserPhoto()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                userProfile.setUserPhoto(downloadUrl);
            }
        });
    }

        private void downloadPicture()
        {
            userPic = null;
            final ImageView mImageView = findViewById(R.id.ivProfilePic);

            try {
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                userPic = File.createTempFile(profileName.getText().toString(), "jpg",storageDir);
            }
            catch(IOException e) {}
            StorageReference ref = storageReference.child(firebaseAuth.getUid() +"/" + "Users");

            final Uri temp = FileProvider.getUriForFile(this,
                    "com.example.roommatefinder.fileprovider",
                    userPic);

            ref.getFile(userPic)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            Bitmap bitmap = BitmapFactory.decodeFile(userPic.getAbsolutePath());
                            mImageView.setImageBitmap(bitmap);
                            userProfile.setUserPhoto(temp);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...

                }
            });

        }

        private void displayLocalPic()
        {
            ImageView mImageView = findViewById(R.id.ivProfilePic);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), userProfile.getUserPhoto());
                mImageView.setImageBitmap(bitmap);
            }
            catch (IOException e) {}

        }

        private void compressPic()
        {

        }

}
