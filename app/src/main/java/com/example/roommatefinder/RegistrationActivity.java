package com.example.roommatefinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName, userEmail, userPassword;
    private Spinner userClass;
    private RadioButton optMale,optFemale;
    private Button regButton, capturePic;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ImageView userProfilePic;
    private FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE = 123;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    String email, name, classYear, password, gender;
    Uri imagePath;
    private StorageReference storageReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null)
        {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userProfilePic.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagePath);
                userProfilePic.setImageBitmap(bitmap);
            }

            catch (IOException e) {}
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setupUIViews();

        // https://www.codementor.io/ashish1dev/firebase-integration-in-your-android-app-udtuuwabb
        // https://medium.com/@peterekeneeze/build-a-simple-blog-app-with-firebase-in-android-studio-b6482275408
        // if using medium website, go to authentication tab, about middle of page
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*"); //all image files. application/* for all types of files
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) //if validate returns true
                {               //then update data to database
                    String user_email = userEmail.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isComplete()){
                                sendUserData();
                                Toast.makeText(RegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            }
                            else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

        capturePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void setupUIViews() {
        userName = findViewById(R.id.etUserName);
        userEmail = findViewById(R.id.etUserEmail);
        userPassword = findViewById(R.id.etUserPassword);
        regButton = findViewById(R.id.btnRegister);
        userLogin = findViewById(R.id.tvUserLogin);
        optMale = findViewById(R.id.optMale);
        optFemale = findViewById(R.id.optFemale);
        userClass = findViewById(R.id.spinner);
        userProfilePic = findViewById(R.id.ivProfile);
        capturePic = findViewById(R.id.btnCapturePic);

    }

    private Boolean validate() {
        Boolean result = false;

        name = userName.getText().toString();
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();

        if(optFemale.isChecked())
        {
            gender = "Female";
        }
        else if (optMale.isChecked())
        {
            gender = "Male";
        }
        else
        {
            gender = null;
        }

        classYear = (String) userClass.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || gender == null || classYear == null) {
            Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
        }
        else {
            result = true;
        }
        return result;
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //connect the auth to the database in firebase by unique ID provided by firebase
        if(firebaseAuth.getUid()!= null) {
            DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());

        if (imagePath != null && firebaseAuth.getUid() != null)
        {
            uploadPhoto();
        }

        UserProfile userProfile = new UserProfile(name, email, classYear, gender);
        myRef.setValue(userProfile);

        }
        else
        {
            Toast.makeText(this, "Password must be at least 9 chars long", Toast.LENGTH_SHORT).show();
        }

    }

    //Take a picture using premade pictureIntent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //checking to make sure that there is a camera intent or camera that can be used
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photo = null;

            try {
                photo = createImageFile();
            } catch (IOException e) {
                Log.e("IO Exception", "DispatchTakePicture CreateImage Failed");
            }

            if (photo != null) {
                //Get Photo location
                imagePath = FileProvider.getUriForFile(this,
                        "com.example.roommatefinder.fileprovider",
                        photo);
                //Store Picture
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    // Creates an image file in app's internal storage
    private File createImageFile() throws IOException {
        // Create a unique image file name
        String imageFileName = "thisisatest"; // must be changed to unique id
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    private void uploadPhoto ()
    {
        //connect to firebase storage, user the Users folder
        //upload tasks put the imagePath on storage
        StorageReference imageRef = storageReference.child(firebaseAuth.getUid()).child("Users");
        UploadTask uploadTask = imageRef.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //tasksnapshot puts pic on storage
                Toast.makeText(RegistrationActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
