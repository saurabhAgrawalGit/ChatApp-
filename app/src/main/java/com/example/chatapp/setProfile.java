package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setProfile extends AppCompatActivity {

    EditText user_name;
    Button setPro;
    ImageView dp;
    private  static int  PICK_CODE=123;
    private Uri imgPath;
    FirebaseAuth firebaseAuth;
    String name;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String uriToken;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_profile);
        user_name =findViewById(R.id.editTextTextPersonName);
        setPro =findViewById(R.id.saveprofile_btn);
        dp =findViewById(R.id.user_img);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage =FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();


        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_CODE);

            }
        });

        setPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=user_name.getText().toString();
                if(name.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"please enter your name ",Toast.LENGTH_SHORT).show();

                }
                else if(imgPath==null)
                {
                    Toast.makeText(getApplicationContext(),"please upload Image ",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendData();
                    Intent intent = new Intent(setProfile.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private  void  sendData()
    {
        name=user_name.getText().toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        UserProfile userProfile=new UserProfile(name,firebaseAuth.getUid());
        databaseReference.setValue(userProfile);
        Toast.makeText(getApplicationContext(),"User Profile Added Successfully",Toast.LENGTH_SHORT);
        sendImgToStorage();
    }
    private  void  sendImgToStorage()
    {
        StorageReference imgRef=storageReference.child("Image").child(firebaseAuth.getUid()).child("Profile Pic");

        Bitmap bitmap=null;
        try {
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imgPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data=byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uriToken=uri.toString();
                        Toast.makeText(getApplicationContext(),"URI get success ",Toast.LENGTH_SHORT).show();
                        sendDataCloud();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"uri get failed ",Toast.LENGTH_SHORT).show();


                    }
                });
                Toast.makeText(getApplicationContext(),"Image is upload ",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image not upload ",Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void sendDataCloud() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String,Object> userdata=new HashMap<>();
        userdata.put("name",name);
        userdata.put("image",uriToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");
        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"data on cloud success ",Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_CODE&&resultCode==RESULT_OK)
        {
            imgPath=data.getData();
            dp.setImageURI(imgPath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}