package sg.edu.np.mad.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import sg.edu.np.mad.travelapp.data.model.User;
import sg.edu.np.mad.travelapp.data.repository.BusStopRepository;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArrayList<String> query;
    private BusTimingCardAdapter nearbyAdapter = new BusTimingCardAdapter();
    private BusTimingCardAdapter favouritesAdapter = new BusTimingCardAdapter();
    private Location userLocation = new Location("");
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ImageButton searchButton = findViewById(R.id.mainSearchButton);
        EditText searchTextBox = findViewById(R.id.mainSearchTextbox);

        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        CurrentLocationRequest request = new CurrentLocationRequest.Builder()
                .setDurationMillis(Long.MAX_VALUE)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setMaxUpdateAgeMillis(0)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        renderUI(nearbyAdapter, findViewById(R.id.nearbyRecyclerView));
        renderUI(favouritesAdapter, findViewById(R.id.favouriteStopsRecyclerView));

        fusedLocationClient.getCurrentLocation(request, null).addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    userLocation = location;
                    BusStopRepository.get_instance().getNearbyBusStops(location, busStopList -> {
                        nearbyAdapter.setBusStopList(busStopList);
                        nearbyAdapter.notifyDataSetChanged();
                    });
                }
            }
        });

        ref.child("1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    User user = task.getResult().getValue(User.class);
                    nearbyAdapter.setUser(user);
                    nearbyAdapter.notifyDataSetChanged();
                    query = user.getFavouritesList();
                    BusStopRepository.get_instance().getBusStopsByName(query, busStopList -> {
                        favouritesAdapter.setUser(user);
                        favouritesAdapter.setBusStopList(busStopList);
                        favouritesAdapter.notifyDataSetChanged();
                    });
                }
            }
        });

        Bundle locBundle = new Bundle();
        locBundle.putParcelable("location", userLocation);
        locBundle.putString("key", "value");
        NavbarFragment navbarFrag = new NavbarFragment();
        navbarFrag.setArguments(locBundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragmentContainerView, navbarFrag).commit();
    }

    public void renderUI(BusTimingCardAdapter adapter, RecyclerView recycler){
        this.runOnUiThread(() -> {
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
            );
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(adapter);
            recycler.setAdapter(adapter);
        });
    }
}