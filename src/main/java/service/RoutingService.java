package service;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoutingService {
    private final LambdaService lambdaService = new LambdaServiceFactory().getService();
    private static final String EXTERNAL_API_KEY = "5b3ce3597851110001cf6248f0f1e1a32a344af0aee1fb9ff05288bc";
    private static final String EXTERNAL_BASE_URL = "https://api.openrouteservice.org/v2/directions/cycling-regular";

    public List<GeoPosition> getRoute(GeoPosition from, GeoPosition to, List<GeoPosition> stations) {
        try {
            return getRouteFromLambda(from, to, stations);
        } catch (Exception e) {
            e.printStackTrace();
            return getRouteFromExternalAPI(from, to);
        }
    }

    private List<GeoPosition> getRouteFromLambda(GeoPosition from, GeoPosition to, List<GeoPosition> stations) {
        CitiBikeRequest request = new CitiBikeRequest(
                new CitiBikeRequest.Location(from.getLatitude(), from.getLongitude()),
                new CitiBikeRequest.Location(to.getLatitude(), to.getLongitude())
        );

        CitiBikeResponse response = lambdaService.getClosestStations(request).blockingGet();

        if (response.route == null || response.route.isEmpty()) {
            throw new RuntimeException("Lambda returned no route.");
        }

        List<GeoPosition> route = new ArrayList<>();
        for (CitiBikeResponse.GeoPoint point : response.route) {
            route.add(new GeoPosition(point.lat, point.lon));
        }

        // Add stations to the route
        for (GeoPosition station : stations) {
            route.add(station);
        }

        return route;
    }

    private List<GeoPosition> getRouteFromExternalAPI(GeoPosition from, GeoPosition to) {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s?api_key=%s&start=%f,%f&end=%f,%f",
                EXTERNAL_BASE_URL, EXTERNAL_API_KEY, from.getLongitude(), from.getLatitude(),
                to.getLongitude(), to.getLatitude());

        try {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                return parseRoute(json);
            } else {
                throw new IOException("Failed to fetch route from external API: " + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch route from external API", e);
        }
    }

    private List<GeoPosition> parseRoute(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        List<GeoPosition> route = new ArrayList<>();

        for (var coord : jsonObject.getAsJsonArray("features")
                .get(0).getAsJsonObject()
                .getAsJsonObject("geometry")
                .getAsJsonArray("coordinates")) {
            double lon = coord.getAsJsonArray().get(0).getAsDouble();
            double lat = coord.getAsJsonArray().get(1).getAsDouble();
            route.add(new GeoPosition(lat, lon));
        }
        return route;
    }
}