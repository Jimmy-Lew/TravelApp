package sg.edu.np.mad.travelapp.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient _instance = null;
    private BusAPI api;

    private RetrofitClient(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BusAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(BusAPI.class);
    }

    public static synchronized RetrofitClient getInstance() {
        return _instance == null ? _instance = new RetrofitClient() : _instance;
    }

    public BusAPI getApi() {return api;}

}
