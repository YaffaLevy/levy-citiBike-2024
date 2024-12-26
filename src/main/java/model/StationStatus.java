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
        //CHECKSTYLE:ON

        public Status(String station_id, int num_bikes_available, int num_docks_available) {
            this.station_id = station_id;
            this.num_bikes_available = num_bikes_available;
            this.num_docks_available = num_docks_available;
        }
    }
}
