package service;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import com.andrewoid.apikeys.ApiKey;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import org.jxmapviewer.viewer.GeoPosition;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoutingService {
    private final LambdaService lambdaService = new LambdaServiceFactory().getService();
    ApiKey apiKey = new ApiKey();
    String keyString = apiKey.get();
    private static final String EXTERNAL_BASE_URL = "https://api.openrouteservice.org/";

    private final OpenRouteService api;

    public RoutingService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EXTERNAL_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        api = retrofit.create(OpenRouteService.class);
    }

    public List<GeoPosition> getRoute(GeoPosition from, GeoPosition to, List<GeoPosition> stations) {
        try {
            return getRouteFromLambda(from, to, stations);
        } catch (Exception e) {
            e.printStackTrace();
            return getRouteFromExternalApi(from, to);
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

    private List<GeoPosition> getRouteFromExternalApi(GeoPosition from, GeoPosition to) {
        String start = from.getLongitude() + "," + from.getLatitude();
        String end = to.getLongitude() + "," + to.getLatitude();

        Call<JsonObject> call = api.getRoute(apiKey.get(), start, end);

        try {
            retrofit2.Response<JsonObject> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return parseRoute(response.body().toString());
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