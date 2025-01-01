package aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import service.LambdaService;
import service.LambdaServiceFactory;

public class CitiBikeRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, CitiBikeResponse> {

    private final LambdaService lambdaService = new LambdaServiceFactory().getService();

    @Override
    public CitiBikeResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        String body = event.getBody();
        Gson gson = new Gson();
        CitiBikeRequest request = gson.fromJson(body, CitiBikeRequest.class);


        CitiBikeResponse response = lambdaService.getClosestStations(request).blockingGet();

        return response;
    }
}
