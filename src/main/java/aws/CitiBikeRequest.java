package aws;

public class CitiBikeRequest {
    public Location from;
    public Location to;

    public CitiBikeRequest(Location from, Location to) {
        this.from = from;
        this.to = to;
    }

    public static class Location {
        public double lat;
        public double lon;

        public Location(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}
