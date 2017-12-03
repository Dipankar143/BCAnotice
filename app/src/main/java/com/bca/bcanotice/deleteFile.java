package com.bca.bcanotice;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class deleteFile extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<data> arrayList;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    ProgressDialog pr;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_file);

        databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://amrapalinoticeboard.firebaseio.com").child("BCA");
        pr=new ProgressDialog(this);
        pr.setMessage("Downloading Old Notice...");
        pr.show();
        arrayList=new ArrayList<>();
        recyclerView= (RecyclerView) findViewById(R.id.rec);
        layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new Adeptor(arrayList,getApplicationContext(),pr);
        new task().execute();
    }


    public class task extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    urlTime urlTime=dataSnapshot.getValue(urlTime.class);
                    data data=new data(urlTime.getUrl(),urlTime.getTime(),dataSnapshot.getKey());
                    arrayList.add(data);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    urlTime urlTime=dataSnapshot.getValue(urlTime.class);
                    data data=new data(urlTime.getUrl(),urlTime.getTime(),dataSnapshot.getKey());
                    arrayList.add(data);

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    urlTime urlTime=dataSnapshot.getValue(urlTime.class);
                    data data=new data(urlTime.getUrl(),urlTime.getTime(),dataSnapshot.getKey());
                    arrayList.add(data);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setAdapter(adapter);
                }
            },5000);


        }
    }
}
