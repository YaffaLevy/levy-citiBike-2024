package service;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import io.reactivex.rxjava3.core.Single;
import model.StationInformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class LambdaServiceTest {

    @Test
    void getClosestStations() {
        //given
        LambdaService mockService = mock(LambdaService.class);
        CitiBikeRequest.Location fromLocation = new CitiBikeRequest.Location(40.73061, -73.935242);
        CitiBikeRequest.Location toLocation = new CitiBikeRequest.Location(40.719, -73.9585);
        CitiBikeRequest request = new CitiBikeRequest(fromLocation, toLocation);
        StationInformation.Station startStation = new StationInformation.Station("1", "Station A", 40.7128, -74.0060);
        StationInformation.Station endStation = new StationInformation.Station("2", "Station B", 40.730610, -73.935242);
        CitiBikeResponse mockResponse = new CitiBikeResponse();
        mockResponse.from = fromLocation;
        mockResponse.to = toLocation;
        mockResponse.start = startStation;
        mockResponse.end = endStation;
        when(mockService.getClosestStations(any(CitiBikeRequest.class))).thenReturn(Single.just(mockResponse));

        //when
        CitiBikeResponse response = mockService.getClosestStations(request).blockingGet();

        //then
        assertNotNull(response);
        assertNotNull(response.start);
        assertNotNull(response.end);
        assertNotNull(response.from);
        assertNotNull(response.to);

        assertNotNull(response.start.name);
        assertNotNull(response.end.name);

        //make sure service called only once
        verify(mockService, times(1)).getClosestStations(request);
    }
}
