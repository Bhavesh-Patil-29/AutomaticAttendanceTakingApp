package com.example.myapplication3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication3.databinding.ActivityNewstuBinding;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class newstu extends AppCompatActivity {

        ActivityNewstuBinding bin;
        ImageView i;

        //Intent intent = getIntent();

        public  Bitmap bmp;
        private static final int SELECT_IMG=1;
        private static final int PICK_IMAGE_MULTIPLE=1;
        int count;
        Uri uri;

        EditText n,r,e;
        String[] encodedimg;
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            bin=ActivityNewstuBinding.inflate(getLayoutInflater());
            setContentView(bin.getRoot());

            int techearid = getIntent().getIntExtra("teacherid",0);
            int position = getIntent().getIntExtra("pos",0);

            i=findViewById(R.id.img);
            n=findViewById(R.id.inputName);
            r= findViewById(R.id.inputrno);
            e=findViewById(R.id.inputenroll);
            Button b=findViewById(R.id.btn);


            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                    startActivityForResult(Intent.createChooser(intent,"Select Images"),PICK_IMAGE_MULTIPLE);
                }
            });

            bin.btnadd.setOnClickListener(a->{
                if(isValidDetails()) {
                    insertData(techearid,position);
                }
            });
        }

        protected void onActivityResult(int requestcode, int resultcode, @Nullable Intent data) {
            super.onActivityResult(requestcode, resultcode, data);
                if(requestcode==PICK_IMAGE_MULTIPLE && resultcode==RESULT_OK &&data!=null)
                {
                    if(data.getClipData()!=null) {
                        count = data.getClipData().getItemCount();
                        encodedimg = new String[count];
                        for(int i=0;i<count;i++) {
                            uri = data.getClipData().getItemAt(i).getUri();
                            bin.img.setImageURI(uri);
                            try {
                                InputStream ins = getContentResolver().openInputStream(uri);
                                bmp = BitmapFactory.decodeStream(ins);
                                encodedimg[i] = encodedImg(bmp);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        private void showToast(String msg)
        {
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        }
        private Boolean isValidDetails()
        {
            if(encodedimg==null) {
                showToast("Select Profile Image");
                return false;
            }
            else if(bin.inputName.getText().toString().trim().isEmpty()){
                showToast("Enter Name");
                return false;
            } else if (bin.inputrno.getText().toString().trim().isEmpty()) {
                showToast("Enter Roll No");
                return false;
            } else if (bin.inputenroll.getText().toString().trim().isEmpty()){
                showToast("Enter Enrollment No");
                return false;
            }else{return true;}
        }
        private String encodedImg(Bitmap bmp)
        {

                int previewwidth = 150;
                int previewHeigth = bmp.getHeight() * previewwidth / bmp.getWidth();
                Bitmap prebmp = Bitmap.createScaledBitmap(bmp, previewwidth, previewHeigth, false);
                ByteArrayOutputStream aout = new ByteArrayOutputStream();
                prebmp.compress(Bitmap.CompressFormat.JPEG, 50, aout);
                byte[] bytes = aout.toByteArray();
                return Base64.encodeToString(bytes, Base64.DEFAULT);


        }
        public void insertData(int t, int p) {
            int c=0;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            HashMap<String,Object> user = new HashMap<>();
            user.put("Name", bin.inputName.getText().toString());
            user.put("Enrollment NO", bin.inputenroll.getText().toString());
            user.put("Roll No", bin.inputrno.getText().toString());
            for(int i=0;i<count;i++) {
                user.put("Student Image"+String.valueOf(i), encodedimg[i]);
            }
            AtomicInteger in= new AtomicInteger(4);
            db.collection("Students"+String.valueOf(t)+String.valueOf(p)).document("std"+bin.inputrno.getText().toString()).set(user)
                    .addOnSuccessListener(documentReference -> {
                        showToast("Student Added In Class");
                        n.setText("");
                        r.setText("");
                        e.setText("");
                        i.setImageResource(0);
                        i.setBackgroundColor(Color.TRANSPARENT);

                    })
                    .addOnFailureListener(exception->
                            showToast(exception.getMessage()+" Unable to insert data"));
        }
}





