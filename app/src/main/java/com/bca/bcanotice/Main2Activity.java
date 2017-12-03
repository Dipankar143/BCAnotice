package com.bca.bcanotice;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    EditText title,msg;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        title= (EditText) findViewById(R.id.input_title);
        msg= (EditText) findViewById(R.id.input_msg);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
    }

    public void notify(View view) throws JSONException {
        final String tit=title.getText().toString();
        final String msgs=msg.getText().toString();
        if (tit.equals("")&&msgs.equals("")){

            Toast.makeText(getApplicationContext(),"Please fill all above feilds",Toast.LENGTH_LONG).show();

        }
        else {
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String url = "https://fcm.googleapis.com/fcm/send";

            final JSONObject data = new JSONObject();
            data.put("title", tit);
            data.put("msg", msgs);
            JSONObject notification_data = new JSONObject();
            notification_data.put("data", data);
            notification_data.put("to", "/topics/news");

            JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://amrapalinoticeboard.firebaseio.com").child("notify");
                    DatabaseReference dt = databaseReference.child(String.valueOf(System.currentTimeMillis()));
                    dt.child("title").setValue(tit);
                    dt.child("msg").setValue(msgs);
                    dt.child("faculty").setValue("BCA");
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), "Getting Some Errors", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

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
        }
    }

    public void delete_noti(View view){

        Intent i=new Intent(this,delete_noti.class);
        startActivity(i);

    }


}
