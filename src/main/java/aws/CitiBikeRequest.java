package aws;

public class CitiBikeRequest {
    public Location from;
    public Location to;

    public static class Location {
        public double lat;
        public double lon;
    }
}
