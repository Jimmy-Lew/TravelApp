package sg.edu.np.mad.travelapp.data.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sg.edu.np.mad.travelapp.data.model.BusStop;
import sg.edu.np.mad.travelapp.data.model.Service;

public interface BusAPI {
    String BASE_URL = "https://travelah-api.onrender.com/";

    @GET("nearby")
    Call<ArrayList<BusStop>> getNearbyBusStops(@Query("lat") String lat, @Query("lng") String lng);

    @GET("timings")
    Call<ArrayList<Service>> getBusStopTimings(@Query("code") String stopCode);

    @GET("timings/code")
    Call<ArrayList<BusStop>> getBusStopsByCode(@Query("stops") ArrayList<String> query);

    @GET("timings/name")
    Call<ArrayList<BusStop>> getBusStopsByName(@Query("stops") ArrayList<String> query);
}
