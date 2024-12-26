package model;

import java.util.List;

public class StationStatus {
    public Data data;

    public static class Data {
        public List<Status> stations;
    }

    public static class Status {
        //CHECKSTYLE:OFF
        public String station_id;
        public int num_bikes_available;
        public int num_docks_available;

        public Status(String stationId, int numBikesAvailable, int numDocksAvailable) {
            //CHECKSTYLE:ON
            this.station_id = stationId;
            this.num_bikes_available = numBikesAvailable;
            this.num_docks_available = numDocksAvailable;
        }
    }
}
