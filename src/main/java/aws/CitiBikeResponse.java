package aws;

import model.StationInformation;

public class CitiBikeResponse {
    public CitiBikeRequest.Location from;
    public CitiBikeRequest.Location to;
    public StationInformation.Station start;
    public StationInformation.Station end;
}
