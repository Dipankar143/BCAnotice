package com.bca.bcanotice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    PhotoView photoView;
    GalleryPhoto galleryPhoto;
    Uri uri;
    StorageReference mstorageReference;
    DatabaseReference myRef;
    final int GAL_PHOTO=12345;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//For permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }


        myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://amrapalinoticeboard.firebaseio.com").child("BCA");

        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Uploading");

        galleryPhoto=new GalleryPhoto(this);
        photoView= (PhotoView) findViewById(R.id.image_view);
        mstorageReference= FirebaseStorage.getInstance().getReferenceFromUrl("gs://amrapalinoticeboard.appspot.com");
    }
    public void upload(View view){

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent,GAL_PHOTO);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==RESULT_OK){
            if (requestCode==GAL_PHOTO){
                uri=data.getData();
                galleryPhoto.setPhotoUri(uri);
                String pathPhoto=galleryPhoto.getPath();
                try {
                    Bitmap bitmap= ImageLoader.init().from(pathPhoto).getBitmap();
                    photoView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void uploadImg(View view) throws IOException {
        progressDialog.show();
        if (uri==null){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Please Select Any Image",Toast.LENGTH_LONG).show();
        }
        else {
        Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
        StorageReference filePath=mstorageReference.child("image").child(uri.getLastPathSegment());
        filePath.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_LONG).show();
                try{


                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    String url = "https://fcm.googleapis.com/fcm/send";

                    JSONObject data = new JSONObject();
                    data.put("title", "BCA new Notice");
                    data.put("url", taskSnapshot.getDownloadUrl());
                    JSONObject notification_data = new JSONObject();
                    notification_data.put("data", data);
                    notification_data.put("to","/topics/news");

                    JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            String api_key_header_value = "Key=AAAAO-CtYNg:APA91bEjBJ6puUrYx6g6KOjWhX2ObTyN5DL5xsccwi37IxJw3w5d_rTJuQuNoERxpJwk8CvPhSqalR9osxgZLxWWxOcAfSNlGlFp0A-AWH6xiUiXRwPgT5d7tnSb4_Eel8jxtDkRdVyE";
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", api_key_header_value);
                            return headers;
                        }
                    };

                    queue.add(request);

                }catch (Exception e){
                    e.printStackTrace();
                }
                Uri uri2=taskSnapshot.getDownloadUrl();
                long uri3=taskSnapshot.getMetadata().getCreationTimeMillis();
                myRef.child(taskSnapshot.getMetadata().getName().toString()).child("url").setValue(uri2.toString());
                myRef.child(taskSnapshot.getMetadata().getName().toString()).child("time").setValue(uri3);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Faild "+e,Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                long peogress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploading "+(int) peogress+" %");

            }
        });
    }
    }

    //Delete Function

    public void delete(View view){
Intent intent=new Intent(this,deleteFile.class);
        startActivity(intent);

    }
    public void noti(View view){
        Intent i=new Intent(this,Main2Activity.class);
        startActivity(i);

    }
}
