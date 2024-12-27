package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import logic.ClosestStationFinder;
import model.StationInformation;
import model.StationStatus;
import service.CitiBikeService;
import service.CitiBikeServiceFactory;

public class CitiBikeRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, CitiBikeResponse> {

    @Override
    public CitiBikeResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        String body = event.getBody();
        Gson gson = new Gson();
        CitiBikeRequest request = gson.fromJson(body, CitiBikeRequest.class);


        CitiBikeServiceFactory factory = new CitiBikeServiceFactory();
        CitiBikeService service = factory.getService();
        StationInformation stationInfo = service.getStationInformation().blockingGet();
        StationStatus stationStatus = service.getStationStatus().blockingGet();


        ClosestStationFinder finder = new ClosestStationFinder();
        CitiBikeResponse response = new CitiBikeResponse();
        response.from = request.from;
        response.to   = request.to;

        StationInformation.Station startStation = finder.findClosestStationWithBikes(
                request.from.lat,
                request.from.lon,
                stationInfo,
                stationStatus
        );
        StationInformation.Station endStation = finder.findClosestStationWithDocks(
                request.to.lat,
                request.to.lon,
                stationInfo,
                stationStatus
        );
        response.start = startStation;
        response.end   = endStation;

        return response;
    }
}
