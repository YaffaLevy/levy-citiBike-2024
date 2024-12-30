package ui;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import org.jxmapviewer.viewer.GeoPosition;
import service.LambdaService;
import service.LambdaServiceFactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class CitiBikeController {
    private final CitiBikeComponent view;
    private final LambdaService service;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public CitiBikeController(CitiBikeComponent view) {
        this.view = view;
        this.service = new LambdaServiceFactory().getService(); // Get LambdaService instance

        view.getMapViewer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Point2D point = new Point2D.Double(x, y);
                GeoPosition position = view.getMapViewer().convertPointToGeoPosition(point);

                if (view.getFromPosition() == null) {
                    view.setFromPosition(position);
                    view.getFromField().setText(position.getLatitude() + ", " + position.getLongitude());
                    view.addWaypoint(position);
                } else if (view.getToPosition() == null) {
                    view.setToPosition(position);
                    view.getToField().setText(position.getLatitude() + ", " + position.getLongitude());
                    view.addWaypoint(position);
                }
            }
        });
    }

    public void calculateRoute() {
        if (view.getFromPosition() != null && view.getToPosition() != null) {
            CitiBikeRequest request = new CitiBikeRequest(
                    new CitiBikeRequest.Location(view.getFromPosition().getLatitude(), view.getFromPosition().getLongitude()),
                    new CitiBikeRequest.Location(view.getToPosition().getLatitude(), view.getToPosition().getLongitude())
            );

            disposables.add(service.getClosestStations(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(SwingSchedulers.edt())
                    .subscribe(
                            this::handleResponse,
                            Throwable::printStackTrace
                    ));
        }
    }

    private void handleResponse(CitiBikeResponse response) {
        GeoPosition startGeoPosition = new GeoPosition(response.start.lat, response.start.lon);
        GeoPosition endGeoPosition = new GeoPosition(response.end.lat, response.end.lon);

        view.calculateRoute(startGeoPosition, endGeoPosition);
    }

    public void clearMap() {
        view.clearMap();
        disposables.clear();
    }
}
