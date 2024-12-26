package logic;

import model.StationInformation;
import model.StationStatus;
import java.util.Comparator;

public class ClosestStationFinder {

    public StationInformation.Station findClosestStationWithBikes(double userLat,
                                                                  double userLon, StationInformation stationInfo, StationStatus stationStatus) {
        return stationInfo.data.stations.stream()
                //our stream is like a conveyor belt going through each thing in the list
                .filter(station -> hasAvailableBikes(station.station_id, stationStatus))
                //the filter is filtering through the stations assesing the station status
                .min(Comparator.comparingDouble(station ->
                        GeoCalculations.haversine(userLat, userLon, station.lat, station.lon)))
                //min is comparing the users location and the station location to find the closest
                .orElseThrow(() -> new RuntimeException("No station with available bikes found."));
    }


    public StationInformation.Station findClosestStationWithDocks(double userLat,
                                                                  double userLon, StationInformation stationInfo, StationStatus stationStatus) {
        return stationInfo.data.stations.stream()
                .filter(station -> hasAvailableDocks(station.station_id, stationStatus))
                .min(Comparator.comparingDouble(station ->
                        GeoCalculations.haversine(userLat, userLon, station.lat, station.lon)))
                .orElseThrow(() -> new RuntimeException("No station with available docks found."));
    }


    private boolean hasAvailableBikes(String stationId, StationStatus stationStatus) {
        return stationStatus.data.stations.stream()
                .anyMatch(status -> status.station_id.equals(stationId) && status.num_bikes_available > 0);
    }

    private boolean hasAvailableDocks(String stationId, StationStatus stationStatus) {
        return stationStatus.data.stations.stream()
                .anyMatch(status -> status.station_id.equals(stationId) && status.num_docks_available > 0);
    }
}
