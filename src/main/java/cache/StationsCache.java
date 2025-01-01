package cache;

import com.google.gson.Gson;
import model.StationInformation;
import service.CitiBikeService;
import service.CitiBikeServiceFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class StationsCache {
    private static final String BUCKET_NAME = "levy-citi";
    private static final String KEY_NAME = "station_information.json";
    private StationInformation stations;
    private Instant lastModified;
    private final S3Client s3Client;
    private final CitiBikeService service;

    public StationsCache() {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.service = new CitiBikeServiceFactory().getService();
    }

    public StationInformation getStations() {
        if (stations != null && lastModified != null && Duration.between(lastModified, Instant.now()).toHours() < 1) {
            return stations;
        }

        if (stations == null && isS3LastModifiedOverAnHour()) {
            stations = downloadStationsFromService();
            lastModified = Instant.now();
            uploadStationsToS3(stations);
        } else if (stations == null) {
            stations = readStationsFromS3();
            lastModified = getS3LastModified();
        }

        if (lastModified == null || Duration.between(lastModified, Instant.now()).toHours() >= 1) {
            stations = downloadStationsFromService();
            lastModified = Instant.now();
            uploadStationsToS3(stations);
        }

        return stations;
    }

    private StationInformation downloadStationsFromService() {
        return service.getStationInformation().blockingGet();
    }

    private void uploadStationsToS3(StationInformation stations) {
        try {
            Gson gson = new Gson();
            String content = gson.toJson(stations);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(KEY_NAME)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StationInformation readStationsFromS3() {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(KEY_NAME)
                    .build();
            InputStream in = s3Client.getObject(getObjectRequest);
            Gson gson = new Gson();
            return gson.fromJson(new InputStreamReader(in), StationInformation.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new StationInformation();
        }
    }

    private boolean isS3LastModifiedOverAnHour() {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(KEY_NAME)
                    .build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            Instant s3LastModified = headObjectResponse.lastModified();
            return Duration.between(s3LastModified, Instant.now()).toHours() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Instant getS3LastModified() {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(KEY_NAME)
                    .build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            return headObjectResponse.lastModified();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }
}