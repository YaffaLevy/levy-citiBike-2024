package route;

import com.andrewoid.apikeys.ApiKey;
import io.reactivex.rxjava3.core.Single;
import org.jxmapviewer.viewer.GeoPosition;
import service.OpenRouteService;

import java.util.ArrayList;
import java.util.List;

public class RoutingService {
    private final OpenRouteService api;
    private final String apiKey;

    public RoutingService(OpenRouteService api) {
        this.api = api;
        ApiKey apiKeyInstance = new ApiKey();
        this.apiKey = apiKeyInstance.get();
    }

    public Single<List<GeoPosition>> getRouteFromExternalApi(GeoPosition from, GeoPosition to) {
        String start = from.getLongitude() + "," + from.getLatitude();
        String end = to.getLongitude() + "," + to.getLatitude();

        return api.getRoute(apiKey, start, end)
                .map(response -> {
                    List<GeoPosition> route = new ArrayList<>();
                    for (var coord : response.features[0].geometry.coordinates) {
                        double lon = coord[0];
                        double lat = coord[1];
                        route.add(new GeoPosition(lat, lon));
                    }
                    return route;
                });
    }
}
