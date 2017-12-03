package com.bca.bcanotice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

/**
 * Created by dipanker on 23/08/17.
 */

public class Adeptor extends RecyclerView.Adapter<Adeptor.ViewHolder> {

    ArrayList<data> arrayList;
    Context context;
    int i=0;
    ProgressDialog pr;

    public Adeptor(ArrayList<data> arrayList, Context context,ProgressDialog pr) {
        this.arrayList = arrayList;
        this.context = context;
        this.pr=pr;
    }

    @Override
    public Adeptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.design,parent,false);
        ViewHolder viewHolder=new ViewHolder(view,arrayList,context);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final Adeptor.ViewHolder holder, final int position) {



            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter.format(new Date(arrayList.get(i).getTime()));

            holder.time.setText(dateString);
            Glide.with(context).load(arrayList.get(position).getUrl()).asBitmap().centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.imageView.setImageBitmap(resource);
                            if (position==arrayList.size()-1) {
                                pr.dismiss();
                            }

                        }
                    });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = position;
                String key = arrayList.get(pos).getFilename();
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://amrapalinoticeboard.appspot.com/image/" + key);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Notice Deleted", Toast.LENGTH_LONG).show();
                        Intent i=new Intent(context.getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed " + String.valueOf(e), Toast.LENGTH_LONG).show();
                    }
                });
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://amrapalinoticeboard.firebaseio.com").child("BCA");
                databaseReference.child(key).removeValue();
            }
        });






    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        ArrayList<data> arrayList;
        Context context;
        TextView time;
        ImageView imageView;
        ImageView delete;
        public ViewHolder(View itemView,ArrayList<data> arrayList,Context context) {
            super(itemView);
            this.context=context;
            this.arrayList=arrayList;
            time= (TextView) itemView.findViewById(R.id.time);
            imageView=(ImageView) itemView.findViewById(R.id.img);
            delete= (ImageView) itemView.findViewById(R.id.delete);


        }

    }
}
