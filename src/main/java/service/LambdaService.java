package service;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LambdaService {
    @POST("/")
    Single<CitiBikeResponse> getClosestStations(@Body CitiBikeRequest request);
}
