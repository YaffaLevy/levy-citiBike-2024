package service;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenRouteService {
    @GET("v2/directions/cycling-regular")
    Call<JsonObject> getRoute(
            @Query("api_key") String apiKey,
            @Query("start") String start,
            @Query("end") String end
    );
}