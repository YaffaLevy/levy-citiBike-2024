package service;

import io.reactivex.rxjava3.core.Single;
import openRoute.OpenRouteResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenRouteService {
    @GET("v2/directions/cycling-regular")
    Single<OpenRouteResponse> getRoute(
            @Query("api_key") String apiKey,
            @Query("start") String start,
            @Query("end") String end
    );
}
