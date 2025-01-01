package aws;

import cache.StationsCache;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import logic.ClosestStationFinder;
import model.StationInformation;
import model.StationStatus;
import service.CitiBikeServiceFactory;

public class CitiBikeRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, CitiBikeResponse> {

    private final StationsCache stationsCache = new StationsCache();

    @Override
    public CitiBikeResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        String body = event.getBody();
        Gson gson = new Gson();
        CitiBikeRequest request = gson.fromJson(body, CitiBikeRequest.class);

        StationInformation stationInfo = stationsCache.getStations();
        StationStatus stationStatus = new CitiBikeServiceFactory().getService().getStationStatus().blockingGet();

        ClosestStationFinder finder = new ClosestStationFinder();
        StationInformation.Station startStation = finder.findClosestStationWithBikes(
                request.from.lat, request.from.lon, stationInfo, stationStatus
        );
        StationInformation.Station endStation = finder.findClosestStationWithDocks(
                request.to.lat, request.to.lon, stationInfo, stationStatus
        );
        CitiBikeResponse response = new CitiBikeResponse();
        response.from = request.from;
        response.to = request.to;
        response.start = startStation;
        response.end = endStation;

        return response;
    }
}
