package com.example.myapplication3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.annotation.Nullable;


public class LoginTabFragment extends Fragment {

    private EditText et1,et2;
    private Button b1;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection("Teachers");
    int count;
    int c=1;
    String id;

    public void onAttach(Context context)
    {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);
        MainActivity ma = new MainActivity();
        et1 = view.findViewById(R.id.log_username);
        et2 = view.findViewById(R.id.log_password);
        Button btn = view.findViewById(R.id.login_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //c=1;
                colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

                    public void onSuccess(QuerySnapshot querySnapshot){
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        count=documents.size();
                        for(DocumentSnapshot document : documents) {

                            if (document.exists()) {
                                String pass = document.getString("pass");
                                String user = document.getString("username");



                                if(et1.getText().toString().trim().equals(user) && et2.getText().toString().trim().equals(pass)) {
                                    id = document.getId();
                                    c = Integer.parseInt(id.substring(1));
                                    Toast.makeText(mContext, "Login Successfull", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, AfterLogin.class);
                                    //Toast.makeText(MainActivity.this, "Value of c = "+String.valueOf(c), Toast.LENGTH_SHORT).show();
                                    intent.putExtra("valueofc",c);
                                    c=0;
                                    startActivity(intent);

                                }
                                c++;
                                if(c==(count+1)) {
                                    et1.setText("");
                                    et2.setText("");
                                    Toast.makeText(mContext, "Invalid Credencials", Toast.LENGTH_SHORT).show();
                                }


                                //Map<String, Object> data = document.getData();
                                //Toast.makeText(MainActivity.this,"Data : "+data,Toast.LENGTH_SHORT).show();
                                // Log.d(TAG, "Document Data" + data);
                            }
                            else {
                                // Toast.makeText(MainActivity.this,"Login UnSuccessfull",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener(){
                    public void onFailure(@Nullable Exception e){
                        //Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG,"Error getting Documents",e);
                    }
                });


            }
        });
        return view;
    }
}