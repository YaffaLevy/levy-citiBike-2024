package cache;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import com.google.gson.Gson;
import service.LambdaService;
import service.LambdaServiceFactory;
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
    private static final String BUCKET_NAME = "levy-city";
    private static final String KEY_NAME = "station_information.json";
    private CitiBikeResponse stations;
    private Instant lastModified;
    private final S3Client s3Client;
    private final LambdaService service;

    public StationsCache() {
        this.s3Client = S3Client.builder().region(Region.US_EAST_1).build();
        this.service = new LambdaServiceFactory().getService();
    }

    public CitiBikeResponse getStations(CitiBikeRequest request) {
        if (stations != null && Duration.between(lastModified, Instant.now()).toHours() < 1) {
            return stations;
        }

        if (stations != null && Duration.between(lastModified, Instant.now()).toHours() >= 1) {
            stations = fetchFromLambda(request);
            lastModified = Instant.now();
            uploadStationsToS3(stations);
            return stations;
        }

        if (stations == null) {
            if (isS3LastModifiedOverAnHour()) {
                stations = fetchFromLambda(request);
                lastModified = Instant.now();
                uploadStationsToS3(stations);
            } else {
                stations = readStationsFromS3();
                lastModified = getS3LastModified();
            }
            return stations;
        }

        return null;
    }

    private CitiBikeResponse fetchFromLambda(CitiBikeRequest request) {
        return service.getClosestStations(request).blockingGet();
    }

    private void uploadStationsToS3(CitiBikeResponse stations) {
        Gson gson = new Gson();
        String content = gson.toJson(stations);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(KEY_NAME)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
    }

    private CitiBikeResponse readStationsFromS3() {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(KEY_NAME)
                .build();
        InputStream in = s3Client.getObject(getObjectRequest);
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(in), CitiBikeResponse.class);
    }

    private boolean isS3LastModifiedOverAnHour() {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(KEY_NAME)
                .build();

        try {
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            Instant lastModified = headObjectResponse.lastModified();
            return Duration.between(lastModified, Instant.now()).toHours() > 0;
        } catch (Exception e) {
            // either the file doesn't exist in S3 or you don't have access to it.
            return false;
        }
    }

    private Instant getS3LastModified() {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(KEY_NAME)
                .build();

        try {
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            return headObjectResponse.lastModified();
        } catch (Exception e) {
            return null;
        }
    }
}
