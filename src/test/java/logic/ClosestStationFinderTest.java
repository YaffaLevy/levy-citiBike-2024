package logic;

import model.StationInformation;
import model.StationStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClosestStationFinderTest {

    @Test
    void findClosestStationWithBikes_returnsCorrectStation() {
        //given
        StationInformation stationInfo = new StationInformation();
        stationInfo.data = new StationInformation.Data();
        stationInfo.data.stations = List.of(
                new StationInformation.Station("1", "Station A", 40.7128, -74.0060),
                new StationInformation.Station("2", "Station B", 40.730610, -73.935242)
        );

        StationStatus stationStatus = new StationStatus();
        stationStatus.data = new StationStatus.Data();
        stationStatus.data.stations = List.of(
                new StationStatus.Status("1", 0, 5),
                new StationStatus.Status("2", 2, 2)
        );

        ClosestStationFinder finder = new ClosestStationFinder();

        //when
        StationInformation.Station closestStation = finder.findClosestStationWithBikes(
                40.730610, -73.935242, stationInfo, stationStatus);

        //then
        assertNotNull(closestStation);
        assertEquals("2", closestStation.station_id, "Station B should be the closest station with bikes.");
    }

    @Test
    void findClosestStationWithDocks_returnsCorrectStation() {
        //given
        StationInformation stationInfo = new StationInformation();
        stationInfo.data = new StationInformation.Data();
        stationInfo.data.stations = List.of(
                new StationInformation.Station("1", "Station A", 40.7128, -74.0060),
                new StationInformation.Station("2", "Station B", 40.730610, -73.935242)
        );

        StationStatus stationStatus = new StationStatus();
        stationStatus.data = new StationStatus.Data();
        stationStatus.data.stations = List.of(
                new StationStatus.Status("1", 3, 0),
                new StationStatus.Status("2", 1, 2)
        );

        ClosestStationFinder finder = new ClosestStationFinder();

        //when
        StationInformation.Station closestStation = finder.findClosestStationWithDocks(
                40.730610, -73.935242, stationInfo, stationStatus);

        //then
        assertNotNull(closestStation);
        assertEquals("2", closestStation.station_id, "Station B should be the closest station with docks.");
    }

    @Test
    void findClosestStationWithBikes_throwsExceptionWhenNoStationsAvailable() {
        //given
        StationInformation stationInfo = new StationInformation();
        stationInfo.data = new StationInformation.Data();
        stationInfo.data.stations = List.of(
                new StationInformation.Station("1", "Station A", 40.7128, -74.0060)
        );

        StationStatus stationStatus = new StationStatus();
        stationStatus.data = new StationStatus.Data();
        stationStatus.data.stations = List.of(
                new StationStatus.Status("1", 0, 5)
        );

        ClosestStationFinder finder = new ClosestStationFinder();

        //when
        Exception exception = assertThrows(RuntimeException.class, () ->
                finder.findClosestStationWithBikes(40.730610, -73.935242, stationInfo, stationStatus)
        );
        //then
        assertEquals("No station with available bikes found.", exception.getMessage());
    }
}
