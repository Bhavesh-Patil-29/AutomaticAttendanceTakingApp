package com.example.myapplication3;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


public class signuptabfragment extends Fragment {
    int c=1;
    int count;
    private Context mContext;
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signuptabfragment, container, false);
        EditText username = view.findViewById(R.id.username);
        EditText email = view.findViewById(R.id.email);
        EditText pass = view.findViewById(R.id.password);
        EditText rpass = view.findViewById(R.id.confirm_pass);
        Button btn = view.findViewById(R.id.signup_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username1 = username.getText().toString();
                String email1 = email.getText().toString();
                String pass1 = pass.getText().toString();
                String repass = rpass.getText().toString();

                if (username1.equals("")) {
                    username.setError("Please enter Username");
                } else if (email1.equals("")) {
                    email.setError("Please enter Email");
                } else if (pass1.equals("")) {
                    pass.setError("Please enter password");
                } else if (repass.equals("")) {
                    rpass.setError("Please Re-enter password");
                } else if (!pass1.equals(repass)) {
                    pass.setText("");
                    rpass.setText("");

                    rpass.setError("Re-password is not Matched with password");

                } else {
                    c++;
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    CollectionReference colref = db.collection("Teachers");

                    colref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            count = querySnapshot.size();
                            //Toast.makeText(AfterNewT.this,""+String.valueOf(count) , Toast.LENGTH_SHORT).show();
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("username", username.getText().toString().trim());
                            user.put("Email", email.getText().toString().trim());
                            user.put("pass", pass.getText().toString().trim());

                            String docpath = "t" + String.valueOf(count + 1);
                            db.collection("Teachers").document(docpath).set(user)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(mContext, "Data Inserted", Toast.LENGTH_SHORT).show();
                                        username.setText("");
                                        email.setText("");
                                        pass.setText("");
                                        rpass.setText("");

                                    })
                                    .addOnFailureListener(exception ->
                                            Toast.makeText(mContext, "Unable to Insert Data", Toast.LENGTH_SHORT).show());
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


        return view;
    }

}