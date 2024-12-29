package view;

import logic.ClosestStationFinder;
import model.StationInformation;
import model.StationStatus;
import org.jxmapviewer.viewer.GeoPosition;
import service.CitiBikeService;
import service.CitiBikeServiceFactory;

public class CitiBikeController {
    private final CitiBikeComponent view;
    private final CitiBikeService service;

    public CitiBikeController(CitiBikeComponent view) {
        this.view = view;
        CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
        this.service = factory.getService();
    }

    public void calculateRoute() {
        if (view.getFromPosition() != null && view.getToPosition() != null) {
            StationInformation stationInfo = service.getStationInformation().blockingGet();
            StationStatus stationStatus = service.getStationStatus().blockingGet();

            ClosestStationFinder finder = new ClosestStationFinder();

            StationInformation.Station startStation = finder.findClosestStationWithBikes(
                    view.getFromPosition().getLatitude(),
                    view.getFromPosition().getLongitude(),
                    stationInfo,
                    stationStatus
            );

            StationInformation.Station endStation = finder.findClosestStationWithDocks(
                    view.getToPosition().getLatitude(),
                    view.getToPosition().getLongitude(),
                    stationInfo,
                    stationStatus
            );

            GeoPosition startGeoPosition = new GeoPosition(startStation.lat, startStation.lon);
            GeoPosition endGeoPosition = new GeoPosition(endStation.lat, endStation.lon);

            view.calculateRoute(startGeoPosition, endGeoPosition);
        }
    }

    public void clearMap() {
        view.clearMap();
    }
}
