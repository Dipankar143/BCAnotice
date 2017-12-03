package com.bca.bcanotice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by dipanker on 29/08/17.
 */

public class only_text_adeptor extends RecyclerView.Adapter<only_text_adeptor.ViewHolder> {
    public only_text_adeptor(ArrayList<only_text_Data> arrayList, Context context, ProgressDialog progressDialog) {
        this.arrayList = arrayList;
        this.context = context;
        this.progressDialog=progressDialog;
    }

    ArrayList<only_text_Data> arrayList;
    Context context;
    ProgressDialog progressDialog;


    @Override
    public only_text_adeptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.noti_design,parent,false);
        ViewHolder viewHolder=new ViewHolder(view,arrayList,context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(only_text_adeptor.ViewHolder holder, final int position) {
        holder.title.setText(arrayList.get(position).getTitle());
        holder.msg.setText(arrayList.get(position).getMsg());
        holder.faculty.setText("By "+arrayList.get(position).getFaculty()+" Faculty");

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://amrapalinoticeboard.firebaseio.com")
                        .child("notify").child(arrayList.get(position).getKey());
                    databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent i=new Intent(context,Main2Activity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                    });


            }
        });
        if (arrayList.size()==position+1){
            progressDialog.dismiss();

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,msg,faculty;
        ImageView del;
        ArrayList<only_text_Data> arrayList;
        Context context;
        public ViewHolder(View itemView,ArrayList<only_text_Data> arrayList,Context context) {
            super(itemView);
            title=(TextView) itemView.findViewById(R.id.txt_title);
            msg=(TextView) itemView.findViewById(R.id.txt_msg);
            faculty= (TextView) itemView.findViewById(R.id.faculty);
            del= (ImageView) itemView.findViewById(R.id.delete_noti);
            this.arrayList=arrayList;
            this.context=context;
        }
    }
}
