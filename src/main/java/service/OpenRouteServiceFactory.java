package service;

import OpenRoute.RoutingService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenRouteServiceFactory {

    public RoutingService createService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(new OkHttpClient())
                .build();

        OpenRouteService api = retrofit.create(OpenRouteService.class);
        return new RoutingService(api);
    }
}