package com.example.fitnessevent.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessevent.FitnessEventApplication;
import com.example.fitnessevent.R;
import com.example.fitnessevent.api.model.Event;
import com.example.fitnessevent.helper.DateUtils;
import com.squareup.picasso.Picasso;



public class EventViewHolder extends RecyclerView.ViewHolder {

    private TextView textTitle;
    private TextView textDate;
    private TextView textIsFree;
    private ImageView imgCover;

    public EventViewHolder(View v) {
        super(v);
        textTitle = (TextView) v.findViewById(R.id.event_txt_title);
        textDate = (TextView) v.findViewById(R.id.event_txt_date);
        textIsFree = (TextView) v.findViewById(R.id.event_txt_address);
        imgCover = (ImageView) v.findViewById(R.id.event_img_cover);
        return;
    }

    public void setEvent(Event event) {
        textTitle.setText(event.getName().getText());
        textDate.setText(DateUtils.getEventDate(event.getStart().getLocal()));
        textIsFree.setText(event.getIsFree() ? R.string.free: R.string.paid);
        if (event.getLogo() != null) {
            Picasso.get().load(event.getLogo().getUrl()).into(imgCover);
        }
    }
}
