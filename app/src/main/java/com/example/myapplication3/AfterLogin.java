package com.example.myapplication3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication3.databinding.ActivityAfterLoginBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.HashMap;


public class AfterLogin extends AppCompatActivity {
    ActivityAfterLoginBinding binding;
    Uri imageuri;
    //PyObject obj = null;



    int p=0;
    String v="";
    StorageReference storagereference;
    ProgressDialog progressDialog;

    ActivityResultLauncher<String> launcher;
    FirebaseDatabase database;
    FirebaseStorage storage;

    String selectclass,selectsub;
    TextView tvclassSpinner,tvsubspinner;
    Spinner classSpinner,subSpinner;
    ArrayAdapter<CharSequence>  classAdapter,subAdapter;



    int SELECT_IMAGE_CODE=1;
    class MyTask extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                Socket s = new Socket("192.168.147.219", 5888);
                Log.d("Info", "Connected");
                publishProgress("Please Wait...");
              BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String line = in.readLine();

                while(line!=null) {
                    publishProgress(line);
                    if(line.equals("Completed"))
                        break;
                    line = in.readLine();
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                //e.printStackTrace();
                return "server not found";

            }
            return "Completed";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            binding.buttonUpload.setText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String str) {
            binding.buttonUpload.setText("Upload Image");
            super.onPostExecute(str);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAfterLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int vofc = getIntent().getIntExtra("valueofc",0);
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        launcher=registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        final StorageReference reference=storage.getReference()
                                .child("image");

                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        database.getReference().child("image")
                                                .setValue(result.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        Toast.makeText(AfterLogin.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                                        MyTask task = new MyTask();
                                                        task.execute();
                                                       /* if (!Python.isStarted()) {
                                                            Python.start(new AndroidPlatform(AfterLogin.this));

                                                            Python py = Python.getInstance();
                                                            PyObject pyobj = py.getModule("main");

                                                            obj = pyobj.callAttr("main1");

                                                            //tv.setText(obj.toString());
                                                            Toast.makeText(AfterLogin.this, obj.toString(), Toast.LENGTH_SHORT).show();
                                                        }*/


                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                        HashMap<String,Object> user = new HashMap<>();
                                                        user.put("teacherid", vofc);
                                                        user.put("year", p);
                                                        user.put("fyr",v );
                                                        user.put("Sub",selectsub );
                                                       // user.put("Student Image", encodedimg);

                                                        db.collection("resources").document("res").set(user)
                                                                .addOnSuccessListener(documentReference -> {
                                                                    Toast.makeText(AfterLogin.this, "Data Inserted", Toast.LENGTH_SHORT).show();

                                                                })
                                                                .addOnFailureListener(exception->

                                                        Toast.makeText(AfterLogin.this, "Unable to insert data", Toast.LENGTH_SHORT).show());
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                    }
                });

        classSpinner = findViewById(R.id.spinner_class);

        classAdapter=ArrayAdapter.createFromResource(this,R.array.array_classname,R.layout.spinner_layout);

        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        classSpinner.setAdapter(classAdapter);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                p=i;
                v=classSpinner.getSelectedItem().toString();

                subSpinner=findViewById(R.id.spinner_subject);

                subAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_default_subject, R.layout.spinner_layout);

                selectclass=classSpinner.getSelectedItem().toString();


                int parentID= parent.getId();
                if(parentID==R.id.spinner_class) {
                    switch (selectclass) {
                        case "Select Subject":
                            subAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_default_subject, R.layout.spinner_layout);
                            break;
                        case "First":
                            subAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_first_subject, R.layout.spinner_layout);
                            break;
                        case "Second":
                            subAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_second_subject, R.layout.spinner_layout);
                            break;
                        case "Third":
                            subAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_third_subject, R.layout.spinner_layout);

                        default:
                            break;

                    }

                    subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    subSpinner.setAdapter(subAdapter);

                    subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            selectsub=subSpinner.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        tvclassSpinner=findViewById(R.id.textview_classname);
        tvsubspinner=findViewById(R.id.textview_subjectname);
        binding.buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectclass.equals("Select Class")){
                    Toast.makeText(AfterLogin.this,"Please select class",Toast.LENGTH_LONG).show();

                    tvclassSpinner.setError("Select Class");
                    tvclassSpinner.requestFocus();
                }
                else if(selectsub.equals("Select Subject")){
                    Toast.makeText(AfterLogin.this,"Please select Subject",Toast.LENGTH_LONG).show();

                    tvsubspinner.setError("Select Subject");
                    tvsubspinner.requestFocus();

                    tvclassSpinner.setError(null);

                }
                else{
                    tvclassSpinner.setError(null);
                    tvsubspinner.setError(null);

                    launcher.launch("image/*");

                }
            }
        });


        MaterialButton newbtn=(MaterialButton) findViewById(R.id.btnadds);
        newbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectclass.equals("Select Class")){
                    Toast.makeText(AfterLogin.this,"Please select class",Toast.LENGTH_LONG).show();

                    tvclassSpinner.setError("Select Class");
                    tvclassSpinner.requestFocus();
                }
                else {
                    Intent intent = new Intent(AfterLogin.this, newstu.class);
                    intent.putExtra("teacherid", vofc);
                    intent.putExtra("pos", p);
                    startActivity(intent);
                }
            }
        });

    }





}