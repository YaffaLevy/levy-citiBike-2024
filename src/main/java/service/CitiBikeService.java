package service;

import io.reactivex.rxjava3.core.Single;
import model.StationInformation;
import model.StationStatus;
import retrofit2.http.GET;

public interface CitiBikeService {

    @GET("station_information.json")
    Single<StationInformation> getStationInformation();

    @GET("station_status.json")
    Single<StationStatus> getStationStatus();
}

