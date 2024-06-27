package com.example.myapplication3;

import static android.content.ContentValues.TAG;
import static com.example.myapplication3.R.id.password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.Map;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;




FirebaseFirestore db = FirebaseFirestore.getInstance();
CollectionReference colRef = db.collection("Teachers");
int count;
int c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);


        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("SignUp"));

        FragmentManager fragmentManager = getSupportFragmentManager();

        adapter = new ViewPagerAdapter(fragmentManager,getLifecycle());
        viewPager2.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
       /* externalFrag = (LoginTabFragment) fragmentManager.findFragmentById(R.id.loginfrag);
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.add(R.id.loginfrag,externalFrag);
        //fragmentTransaction.commit();

        EditText username=  fragmentManager.findFragmentById(R.id.loginfrag).getView().findViewById(R.id.username);
        EditText password=fragmentManager.findFragmentById(R.id.loginfrag).getView().findViewById(R.id.password);

        Button loginbtn=fragmentManager.findFragmentById(R.id.loginfrag).getView().findViewById(R.id.login_btn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                c=1;
                colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

                    public void onSuccess(QuerySnapshot querySnapshot){
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        for(DocumentSnapshot document : documents) {
                            if (document.exists()) {
                                String pass = document.getString("pass");
                                String user = document.getString("username");

                                count=documents.size();

                                if(username.getText().toString().equals(user) && password.getText().toString().equals(pass)) {
                                    Toast.makeText(MainActivity.this, "Login Successfull"+String.valueOf(c), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, AfterLogin.class);
                                    //Toast.makeText(MainActivity.this, "Value of c = "+String.valueOf(c), Toast.LENGTH_SHORT).show();
                                    intent.putExtra("valueofc",c);
                                    c=0;
                                    startActivity(intent);

                                }
                                c++;
                                if(c==(count+1)) {
                                    username.setText("");
                                    password.setText("");
                                    Toast.makeText(MainActivity.this, "Login UnSuccessfull", Toast.LENGTH_SHORT).show();
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














               // if(username.getText().toString().equals("b") && password.getText().toString().equals("b")){
                 //   Toast.makeText(MainActivity.this,"Login Successfull",Toast.LENGTH_SHORT).show();
                    //Intent intent=new Intent(MainActivity.this,AfterLogin.class);
                    //startActivity(intent);
            //}
              //  else {
                //    Toast.makeText(MainActivity.this,"Login UnSuccessfull",Toast.LENGTH_SHORT).show();
                //}

                }
        });

       // Button newbtn=(Button) findViewById(R.id.signup_button);
      //  newbtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
         //   public void onClick(View v) {
           //     Intent intent1=new Intent(MainActivity.this,AfterNewT.class);
           //     startActivity(intent1);
          //  }
      //  });*/





    }
}