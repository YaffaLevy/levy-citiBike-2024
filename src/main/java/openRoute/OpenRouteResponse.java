package openRoute;

import java.util.List;

public class OpenRouteResponse {
    public Feature[] features;

    public static class Feature {
        public Geometry geometry;
    }

    public static class Geometry {
        public List<double[]> coordinates;
    }
}
