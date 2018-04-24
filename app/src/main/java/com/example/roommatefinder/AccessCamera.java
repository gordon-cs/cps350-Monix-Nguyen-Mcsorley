package com.example.roommatefinder;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by max on 4/23/18.
 */

public class AccessCamera {

    //Constants
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //variables
    private String mCurrentPhotoPath;
    private Uri photoURI;

    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;


    public AccessCamera ()
    {


    }



}
