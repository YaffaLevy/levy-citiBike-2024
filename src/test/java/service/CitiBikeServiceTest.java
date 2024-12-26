package service;

import model.StationInformation;
import model.StationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CitiBikeServiceTest {

    @Test
    void getStationInformation() {
        // Given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        // When
        StationInformation response = service.getStationInformation().blockingGet();

        // Then
        assertNotNull(response);
        assertNotNull(response.data);
        assertNotNull(response.data.stations);
        assertTrue(response.data.stations.size() > 0);

        // Validate the first station's data
        StationInformation.Station firstStation = response.data.stations.get(0);
        assertNotNull(firstStation.station_id);
        assertNotNull(firstStation.name);
        assertTrue(firstStation.lat != 0);
        assertTrue(firstStation.lon != 0);
    }

    @Test
    void getStationStatus() {
        // Given
        CitiBikeService service = new CitiBikeServiceFactory().getService();

        // When
        StationStatus response = service.getStationStatus().blockingGet();

        // Then
        assertNotNull(response);
        assertNotNull(response.data);
        assertNotNull(response.data.stations);
        assertTrue(response.data.stations.size() > 0);

        // Validate the first station's status
        StationStatus.Status firstStation = response.data.stations.get(0);
        assertNotNull(firstStation.station_id);
        assertTrue(firstStation.num_bikes_available >= 0);
        assertTrue(firstStation.num_docks_available >= 0);
    }
}
