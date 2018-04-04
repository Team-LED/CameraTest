package com.example.android.cameratest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button capture_image_button_view;
    ImageView picture_image_view;
    static final int CAM_REQUEST = 1;
    static final int PERMISSION_REQUEST = 123;
    String mCurrentPhotoPath;

    protected boolean permissionsToTakePhotos = false;
    protected boolean permissionsToWriteFiles = false;
    protected String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_REQUEST:
                permissionsToTakePhotos = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionsToWriteFiles = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if(!permissionsToWriteFiles || !permissionsToTakePhotos)
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);


        setContentView(R.layout.activity_main);
        capture_image_button_view = (Button) findViewById(R.id.btnCapture);
        picture_image_view = (ImageView) findViewById(R.id.Image_View);
        capture_image_button_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAM_REQUEST);
            }
        }
    }
    private File createImageFile() throws IOException    //getFile()
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;


        //File Folder = new File("sdcard/camera_app");
        ///if (!Folder.exists()) {
        //  Folder.mkdir();
        //}
        //File image_file = new File(Folder, "Recorder.jpg");
        //return image_file;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAM_REQUEST && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap)extras.get("data");
            picture_image_view.setImageBitmap(bitmap);
        }
        //super.onActivityResult(requestCode,resultCode,data);
        //String path = "sdcard/camera_app/Recorder.jpg";
        //imageView.setImageBitmap(bitmap);
        //imageView.setImageDrawable(Drawable.createFromPath(path));
    }
}
