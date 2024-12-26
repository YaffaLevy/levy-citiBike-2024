package model;

import java.util.List;

public class StationInformation {
    public Data data;

    public static class Data {
        public List<Station> stations;
    }

    public static class Station {
        //CHECKSTYLE:OFF
        public String station_id;
        //CHECKSTYLE:ON
        public String name;
        public double lat;
        public double lon;

        public Station(String stationId, String name, double lat, double lon) {
            this.station_id = stationId;
            this.name = name;
            this.lat = lat;
            this.lon = lon;

        }
    }
}

