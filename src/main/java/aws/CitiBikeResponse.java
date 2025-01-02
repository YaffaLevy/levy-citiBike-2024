package aws;

import model.StationInformation;

import java.util.List;

public class CitiBikeResponse {
    public CitiBikeRequest.Location from;
    public CitiBikeRequest.Location to;
    public StationInformation.Station start;
    public StationInformation.Station end;
    public List<GeoPoint> route;

    public static class GeoPoint {
        public double lat;
        public double lon;

        public GeoPoint(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}
