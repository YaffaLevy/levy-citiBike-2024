package service;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import org.junit.jupiter.api.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import service.LambdaService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LambdaServiceTest {

    @Test
    void getClosestStation() {
        //given
        LambdaService service = new LambdaServiceFactory().getService();

        CitiBikeRequest.Location fromLocation = new CitiBikeRequest.Location(40.73061, -73.935242);
        CitiBikeRequest.Location toLocation = new CitiBikeRequest.Location(40.719, -73.9585);
        CitiBikeRequest request = new CitiBikeRequest(fromLocation, toLocation);

        //when
        CitiBikeResponse response = service.getClosestStations(request).blockingGet();

        //then
        assertNotNull(response);
        assertNotNull(response.from);
        assertNotNull(response.to);
        assertNotNull(response.start);
        assertNotNull(response.end);
        assertNotNull(response.start.name);
        assertNotNull(response.end.name);
    }
}
