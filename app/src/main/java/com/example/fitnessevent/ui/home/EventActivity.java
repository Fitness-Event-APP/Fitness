package com.example.fitnessevent.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.fitnessevent.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /////
    private FirebaseAuth mAuth;
    String activityTitle = "";
//    FirebaseUser currentUser = mAuth.getCurrentUser();
//    private DocumentReference dRef = FirebaseFirestore.getInstance().document("/user/"+currentUser.getUid());
    private DocumentReference dRef = FirebaseFirestore.getInstance().document("/user/uoOzYdmKBwNOMYcPmEnEYktb9nY2");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getIncomingIntent();


        Button like_button = findViewById(R.id.like_button);
        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            Map<String, Object> myUser = documentSnapshot.getData();

                            ArrayList<String> myLikes = (ArrayList<String>) documentSnapshot.get("likes");
                            myLikes.add(activityTitle);
                            myUser.put("likes", (Object) myLikes);

                            db.collection("user").document("uoOzYdmKBwNOMYcPmEnEYktb9nY2")
                                    .set(myUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot added with liked event");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding like event", e);
                                        }
                                    });
                        }
                    }
                });






            }
        });


    }

    private void getIncomingIntent() {
        Log.d(TAG,"getIncomingIntent: Checking for incoming intents.");
        if (getIntent().hasExtra("image_url") && getIntent().hasExtra("title")
                && getIntent().hasExtra("description") && getIntent().hasExtra("startTime")
                && getIntent().hasExtra("endTime") && getIntent().hasExtra("address")) {
            Log.d(TAG,"getIncomingIntent: found intent extras");

            String imageUrl = getIntent().getStringExtra("image_url");
            String title = getIntent().getStringExtra("title");
            String description = getIntent().getStringExtra("description");
            String startTime = getIntent().getStringExtra("startTime");
            String endTime = getIntent().getStringExtra("endTime");
            String address = getIntent().getStringExtra("address");

            activityTitle = title;  /////

            setEvent(imageUrl, title, description, startTime, endTime, address);
        }
    }

    private void setEvent(String imageUrl, String title, String description, String startTime, String endTime, String address) {
        Log.d(TAG,"Set Event");

        ImageView imageView = findViewById(R.id.imageViewHR);
        Picasso.get().load(imageUrl).into(imageView);

        TextView textViewTitle = findViewById(R.id.title_eventPage);
        textViewTitle.setText(title);

        TextView textViewDescription = findViewById(R.id.contentOfDescription);
        textViewDescription.setText(description);

        TextView textViewtime = findViewById(R.id.time);
        textViewtime.setText(startTime + "-" + endTime);

        TextView textViewAddress = findViewById(R.id.location);
        textViewAddress.setText(address);
    }


}
