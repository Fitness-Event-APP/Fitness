package com.example.fitnessevent;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EventCreateActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {

    private Context context;
    private LinearLayout llDate, llTime, ll1Date, ll1Time;
    private TextView tvDate, tvTime, tv1Date, tv1Time;
    private int year, month, day, hour, minute;
    //display on textview
    private StringBuffer date, time;
////////////////////////
    private static final String TAG = "EVENTCREATE";
    private static final String KEY_TITLE = "CreatEvent";
    private static final String KEY_LOCATION = "Location";
    private static final String KEY_QUANTITY = "Quantity";
    private static final String KEY_PRICE = "Price";
    private static final String KEY_DESCRIPTION = "Description";

    private EditText edit_event_title, edit_location, edit_description, edit_quantity, edit_price;
    private Button button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    ///////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
        
        context = this;
        date = new StringBuffer();
        time = new StringBuffer();
        initView();
        initDateTime();

        ////////////////////////////////////////////////////////////////




    }

    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
    }

    private void initView() {
        llDate = findViewById(R.id.ll_date);
        ll1Date = findViewById(R.id.ll1_date);
        tvDate =  findViewById(R.id.tv_date);
        llTime =  findViewById(R.id.ll_time);
        ll1Time = findViewById(R.id.ll1_time);
        tvTime =  findViewById(R.id.tv_time);
        tv1Date = findViewById(R.id.tv1_date);
        tv1Time =findViewById(R.id.tv1_time);
        llDate.setOnClickListener(this);
        llTime.setOnClickListener(this);
        ll1Date.setOnClickListener(this);
        ll1Time.setOnClickListener(this);


        edit_event_title = findViewById(R.id.editText);
        edit_location = findViewById(R.id.input_location);
        edit_description = findViewById(R.id.input_description);
        edit_quantity = findViewById(R.id.input_quantity);
        edit_price = findViewById(R.id.input_price);
        button = findViewById(R.id.submit_button);
    }

    public void submit(View v){
        String event_title = edit_event_title.getText().toString();
        String location = edit_location.getText().toString();
        String description = edit_description.getText().toString();
        String quantity = edit_quantity.getText().toString();
        String price = edit_price.getText().toString();

        Map<String,Object> note = new HashMap<>();
        note.put(KEY_TITLE,event_title);
        note.put(KEY_LOCATION,location);
        note.put(KEY_DESCRIPTION,description);
        note.put(KEY_QUANTITY,quantity);
        note.put(KEY_PRICE,price);

        db.collection("user").document("CreateEvent").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EventCreateActivity.this,"Save!!!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EventCreateActivity.this,"NOT SAVED !!!", Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_date:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (date.length() > 0) {
                            date.delete(0, date.length());
                        }
                        tvDate.setText(date.append((month)).append("/").append(day).append("/").append(year));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_date, null);
                final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

                dialog.setTitle("Setting");
                dialog.setView(dialogView);
                dialog.show();
                //init listener
                datePicker.init(year, month - 1, day, this);
                break;

            case R.id.ll1_date:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (date.length() > 0) {
                            date.delete(0, date.length());
                        }
                        tv1Date.setText(date.append((month)).append("/").append(day).append("/").append(year));
                        dialog.dismiss();
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog1 = builder1.create();
                View dialogView1 = View.inflate(context, R.layout.dialog_date, null);
                final DatePicker datePicker1 = dialogView1.findViewById(R.id.datePicker);

                dialog1.setTitle("Setting");
                dialog1.setView(dialogView1);
                dialog1.show();

                datePicker1.init(year, month - 1, day, this);
                break;

            case R.id.ll_time:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (time.length() > 0) {
                            time.delete(0, time.length());
                        }
                        tvTime.setText(time.append(String.valueOf(hour)).append(":").append(String.valueOf(minute)));
                        dialog.dismiss();
                    }
                });
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog2 = builder2.create();
                View dialogView2 = View.inflate(context, R.layout.dialog_time, null);
                TimePicker timePicker = dialogView2.findViewById(R.id.timePicker);
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minute);
                timePicker.setIs24HourView(true);
                // 24h
                timePicker.setOnTimeChangedListener(this);
                dialog2.setTitle("Setting");
                dialog2.setView(dialogView2);
                dialog2.show();
                break;
            case R.id.ll1_time:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
                builder3.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (time.length() > 0) {
                            time.delete(0, time.length());
                        }
                        tv1Time.setText(time.append(String.valueOf(hour)).append(":").append(String.valueOf(minute)));
                        dialog.dismiss();
                    }
                });
                builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog3 = builder3.create();
                View dialogView3 = View.inflate(context, R.layout.dialog_time, null);
                TimePicker timePicker3 = (TimePicker) dialogView3.findViewById(R.id.timePicker);
                timePicker3.setCurrentHour(hour);
                timePicker3.setCurrentMinute(minute);
                timePicker3.setIs24HourView(true); //设置24小时制
                timePicker3.setOnTimeChangedListener(this);
                dialog3.setTitle("Setting");
                dialog3.setView(dialogView3);
                dialog3.show();
                break;

        }

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;

    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;

    }
}
