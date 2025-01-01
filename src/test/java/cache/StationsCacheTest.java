package cache;

import model.StationInformation;
import org.junit.jupiter.api.Test;
import service.CitiBikeService;
import service.CitiBikeServiceFactory;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StationsCacheTest {

    @Test
    void getStations() {
        //given
        StationsCache stationsCache = new StationsCache();

        //when
        StationInformation response = stationsCache.getStations();

        //then
        assertNotNull(response);
        assertNotNull(response.data);
        assertNotNull(response.data.stations);
        assertTrue(response.data.stations.size() > 0);
        StationInformation.Station firstStation = response.data.stations.get(0);
        assertNotNull(firstStation.station_id);
        assertNotNull(firstStation.name);
        assertTrue(firstStation.lat != 0);
        assertTrue(firstStation.lon != 0);
    }

    @Test
    void getStationsAfterCacheExpiry() {
        //given
        StationsCache stationsCache = new StationsCache();
        StationInformation initialResponse = stationsCache.getStations();
        assertNotNull(initialResponse);
        stationsCache.setLastModified(Instant.now().minus(Duration.ofHours(2)));

        //when
        StationInformation responseAfterExpiry = stationsCache.getStations();

        //then
        assertNotNull(responseAfterExpiry);
        assertNotNull(responseAfterExpiry.data);
        assertNotNull(responseAfterExpiry.data.stations);
        assertTrue(responseAfterExpiry.data.stations.size() > 0);
        StationInformation.Station firstStation = responseAfterExpiry.data.stations.get(0);
        assertNotNull(firstStation.station_id);
        assertNotNull(firstStation.name);
        assertTrue(firstStation.lat != 0);
        assertTrue(firstStation.lon != 0);
    }
}