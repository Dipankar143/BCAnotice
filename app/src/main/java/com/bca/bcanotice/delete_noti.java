package com.bca.bcanotice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class delete_noti extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.Adapter myAdeptor;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<only_text_Data> arrayList;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_noti);
        recyclerView= (RecyclerView) findViewById(R.id.noti_rec);
        arrayList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final ProgressDialog progressDialog;
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        myAdeptor=new only_text_adeptor(arrayList,this,progressDialog);




        myRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://amrapalinoticeboard.firebaseio.com").child("notify");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                title_Data title_data= dataSnapshot.getValue(title_Data.class);
                String title=title_data.getTitle();
                String msg=title_data.getMsg();
                String faculty=title_data.getFaculty();
                String key=dataSnapshot.getKey();
                if (faculty.equals("BCA")) {
                    arrayList.add(new only_text_Data(title, msg, faculty, key));
                }
                    progressDialog.show();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                title_Data title_data= dataSnapshot.getValue(title_Data.class);
                String title=title_data.getTitle();
                String msg=title_data.getMsg();
                String faculty=title_data.getFaculty();
                String key=dataSnapshot.getKey();
                if (faculty.equals("BCA")) {
                    arrayList.add(new only_text_Data(title, msg, faculty, key));
                }


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                title_Data title_data= dataSnapshot.getValue(title_Data.class);
                String title=title_data.getTitle();
                String msg=title_data.getMsg();
                String faculty=title_data.getFaculty();
                String key=dataSnapshot.getKey();
                if (faculty.equals("BCA")) {
                    arrayList.add(new only_text_Data(title, msg, faculty, key));
                }
                progressDialog.show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                title_Data title_data= dataSnapshot.getValue(title_Data.class);
                String title=title_data.getTitle();
                String msg=title_data.getMsg();
                String faculty=title_data.getFaculty();
                String key=dataSnapshot.getKey();
                if (faculty.equals("BCA")) {
                    arrayList.add(new only_text_Data(title, msg, faculty, key));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do your work
                recyclerView.setAdapter(myAdeptor);
                if (arrayList.size()==0){

                    Toast.makeText(getApplicationContext(),"NO Notice Found",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(),Main2Activity.class);
                    startActivity(intent);


                }
            }
        },5000);
    }
}
