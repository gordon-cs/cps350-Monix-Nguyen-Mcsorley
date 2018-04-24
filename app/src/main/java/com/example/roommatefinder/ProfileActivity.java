package com.example.roommatefinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePic;
    private TextView profileName, profileClass, profileEmail, profileGender;
    private Button profileUpdate, changePassword,profilepicUpdate;

    //1st step: import firebase auth and database
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    //Constants
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //variables
    private String mCurrentPhotoPath;
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
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                profileName.setText(userProfile.getUserName());
                profileEmail.setText(userProfile.getUserEmail());
                profileClass.setText(userProfile.getUserClass());
                profileGender.setText(userProfile.getUserGender());

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

    private void updateProfilePic()
    {
        ImageView mImageView = findViewById(R.id.ivProfilePic);
        //mImageView.setImageBitmap();


    }

    private void download()
    {
        File localFile = null;
        try {
            localFile = File.createTempFile(profileName.getText().toString(), "jpg");
        }
        catch(IOException e) {}
        StorageReference ref = storageReference.child("UserProfilePics/"+ profileName.getText().toString());
        ref.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }



    //Take a picture using premade pictureIntent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //checking to make sure that there is a camera intent or camera that can be used
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photo = null;

            try{ photo = createImageFile(); }
            catch (IOException e) {}

            if(photo != null) {
                //Get Photo location
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.roommatefinder.fileprovider",
                        photo);
                //Store Picture
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }
    }

    // method that is executed when startActivityForResult() returns
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            this.upload();
        }
    }

    // Upload photo to Firebase
    private void upload ()
    {
        //Creating a tag for file in firebase
        StorageReference ref = storageReference.child("UserProfilePics/"+ profileName.getText().toString());
        //putting file in firebase
        ref.putFile(photoURI);
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
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
