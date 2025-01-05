package OpenRoute;

import OpenRoute.GeoPoint;

import java.util.List;

public class Route {
    private GeoPoint start;
    private GeoPoint end;
    private List<GeoPoint> route;

    // Getters and setters
    public GeoPoint getStart() {
        return start;
    }

    public void setStart(GeoPoint start) {
        this.start = start;
    }

    public GeoPoint getEnd() {
        return end;
    }

    public void setEnd(GeoPoint end) {
        this.end = end;
    }

    public List<GeoPoint> getRoute() {
        return route;
    }

    public void setRoute(List<GeoPoint> route) {
        this.route = route;
    }
}
