package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class AfterNewT extends AppCompatActivity {
    int c=1;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_new_t);

        EditText username=(EditText) findViewById(R.id.username);
        EditText email=(EditText) findViewById(R.id.email);
        EditText password=(EditText) findViewById(R.id.password);
        EditText repassword=(EditText) findViewById(R.id.repassword);


        MaterialButton regbtn=(MaterialButton) findViewById(R.id.signupbtn);

                regbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username1=username.getText().toString();
                        String email1=email.getText().toString();
                        String pass=password.getText().toString();
                        String repass=repassword.getText().toString();

                        if(username1.equals(""))
                        {
                            username.setError("Please enter Username");
                        }
                        else if(email1.equals("")){
                            email.setError("Please enter Email");
                        }
                        else if(pass.equals("")){
                            password.setError("Please enter password");
                        }
                        else if(repass.equals("")){
                            repassword.setError("Please Re-enter password");
                        }
                        else if(!pass.equals(repass)){
                            password.setText("");
                            repassword.setText("");

                            repassword.setError("Re-password is not Matched with password");

                        }
                        else{
                            c++;
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            CollectionReference colref = db.collection("Teachers");

                            colref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot querySnapshot) {
                                    count = querySnapshot.size();
                                    //Toast.makeText(AfterNewT.this,""+String.valueOf(count) , Toast.LENGTH_SHORT).show();
                                    HashMap<String,Object> user = new HashMap<>();
                                    user.put("username", username.getText().toString());
                                    user.put("Email", email.getText().toString());
                                    user.put("pass", password.getText().toString());

                                    String docpath="t"+String.valueOf(count+1);
                                    db.collection("Teachers").document(docpath).set(user)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(AfterNewT.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                                username.setText("");
                                                email.setText("");
                                                password.setText("");
                                                repassword.setText("");

                                            })
                                            .addOnFailureListener(exception->
                                                    Toast.makeText(AfterNewT.this, "Unable to Insert Data", Toast.LENGTH_SHORT).show());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                           // Toast.makeText(AfterNewT.this,""+String.valueOf(count) , Toast.LENGTH_SHORT).show();

                            //Toast.makeText(AfterNewT.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


       // public void insertData() {


    }
}