package com.example.fitnessevent.ui.home;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessevent.FitnessEventApplication;
import com.example.fitnessevent.R;
import com.example.fitnessevent.api.EventbriteApi;
import com.example.fitnessevent.api.baseapi.CallId;
import com.example.fitnessevent.api.baseapi.CallOrigin;
import com.example.fitnessevent.api.baseapi.CallType;
import com.example.fitnessevent.api.model.Event;
import com.example.fitnessevent.api.model.PaginatedEvents;
import com.example.fitnessevent.helper.Constants;
import com.example.fitnessevent.helper.PreferencesHelper;
import com.example.fitnessevent.helper.ResourcesHelper;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener {
    private HomeViewModel homeViewModel;
    private static final int NO_FLAGS = 0;
    private static final int ACCESS_COARSE_LOCATION_PERMISSION_REQUEST = 7001;

    private Toolbar toolbar;
    private TextView txtNoResults;
    private TextView txtWaitForResults;
    private SearchView searchView;
    private MenuItem menuSearch;
    private CoordinatorLayout coordinatorLayout;
    private EventbriteApi eventbriteApi;
    private Location location;
    private RecyclerView recyclerEvents;
    private EventAdapter eventAdapter;
    private PaginatedEvents lastPageLoaded;
    private String currentQuery;
    private CallId getEventsCallId;

    public static double getShorterCoordinate(double coordinate) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat(Constants.COORDINATES_FORMAT, dfs);

        return Double.parseDouble(decimalFormat.format(coordinate));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

            }
        });
        eventbriteApi = FitnessEventApplication.getApplication().getApiEventbrite();

        txtNoResults = (TextView) root.findViewById(R.id.home_txt_no_results);
        txtWaitForResults = (TextView) root.findViewById(R.id.home_txt_wait_first_time);
        coordinatorLayout = (CoordinatorLayout) root.findViewById(R.id.home_coordinator_layout);
        recyclerEvents = (RecyclerView) root.findViewById(R.id.home_events_recycler);

        setupTaskDescription();
        setupRecyclerView();
        checkForLocationPermission();
        /*
        Button button_title1 = (Button) root.findViewById(R.id.button_title1);
        button_title1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eventIntent = new Intent(getActivity(), EventActivity.class);
                startActivity(eventIntent);
            }
        });
        */
        return root;
    }



    private void setupRecyclerView() {
        recyclerEvents.setHasFixedSize(true);
        // Change this to getActivity().getApplicationContext()
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerEvents.setLayoutManager(layoutManager);
        eventAdapter = new EventAdapter(new ArrayList<Event>());
        recyclerEvents.setAdapter(eventAdapter);

        recyclerEvents.setVisibility(View.GONE);
    }

    private void checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_PERMISSION_REQUEST);
        } else {
            LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            getEvents(null);
        }
    }

    private void getEvents(String query) {
        Snackbar.make(coordinatorLayout, R.string.getting_events, Snackbar.LENGTH_SHORT).show();
        getEventsCallId = new CallId(CallOrigin.HOME, CallType.GET_EVENTS);
        Callback<PaginatedEvents> callback = generateGetEventsCallback();
        eventbriteApi.registerCallback(getEventsCallId, callback);
        try {
            eventbriteApi.getEvents(query, getShorterCoordinate(37.773972), getShorterCoordinate(-122.431297
            ),lastPageLoaded,getEventsCallId,callback);
        } catch (IOException e) {
            Log.d("getEvents","Failed in HomeFragment");
            e.printStackTrace();
        }
        PreferencesHelper.setLastSearch(query);
    }

    private Callback<PaginatedEvents> generateGetEventsCallback() {
        return new Callback<PaginatedEvents>() {
            @Override
            public void onResponse(Call<PaginatedEvents> call, Response<PaginatedEvents> response) {
                PaginatedEvents paginatedEvents = response.body();
                if (paginatedEvents.getEvents().isEmpty()) {
                    eventAdapter.setKeepLoading(false);
                    if (eventAdapter.getItemCount() == 0) {
                        txtNoResults.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                txtNoResults.setVisibility(View.GONE);
                updateEventsList(paginatedEvents.getEvents());
                lastPageLoaded = paginatedEvents;
            }

            @Override
            public void onFailure(Call<PaginatedEvents> call, Throwable t) {
                handleGetEventsFailure();
            }
        };
    }

    private void handleGetEventsFailure() {
        txtWaitForResults.setVisibility(View.GONE);
        txtNoResults.setVisibility(View.VISIBLE);
    }

    private void updateEventsList(List<Event> eventslist) {
        eventAdapter.add(eventslist);
        eventAdapter.notifyDataSetChanged();

        if (recyclerEvents.getVisibility() != View.VISIBLE) {
            recyclerEvents.setVisibility(View.VISIBLE);
        }
    }

    private void setupTaskDescription() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap icon = BitmapFactory.decodeResource(ResourcesHelper.getResources(),
                    R.drawable.ic_launcher);
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(ResourcesHelper.getString(R.string.app_name), R.drawable.ic_launcher, ResourcesHelper.getResources().getColor(R.color.colorPrimary));
            getActivity().setTaskDescription(taskDescription);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        resetSearch();
        currentQuery = query.trim();
        getEvents(currentQuery);
        hideKeyboard();
        return true;
    }

    private void resetSearch() {
        lastPageLoaded = null;
        eventAdapter.reset();
    }

    public void hideKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) FitnessEventApplication.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), NO_FLAGS);
        }
    }




    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoadMoreEvents event) {
        getEvents(currentQuery);
    }

    @Override
    public void onStart(){
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventbriteApi.unregisterCallback(getEventsCallId);
        EventBus.getDefault().unregister(this);
    }
}