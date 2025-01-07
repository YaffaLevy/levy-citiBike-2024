package ui;

import aws.CitiBikeRequest;
import aws.CitiBikeResponse;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import org.jxmapviewer.viewer.GeoPosition;
import route.RoutingService;
import service.OpenRouteServiceFactory;
import service.LambdaService;
import service.LambdaServiceFactory;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CitiBikeController {
    private final CitiBikeComponent view;
    private final LambdaService lambdaService;
    private final RoutingService routingService;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private GeoPosition fromPosition;
    private GeoPosition toPosition;
    private final JTextField fromField;
    private final JTextField toField;

    public CitiBikeController(CitiBikeComponent view, JTextField fromField, JTextField toField) {
        this.view = view;
        this.lambdaService = new LambdaServiceFactory().getService();
        this.routingService = new OpenRouteServiceFactory().createService();
        this.fromField = fromField;
        this.toField = toField;

        view.getMapViewer().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Point2D point = new Point2D.Double(x, y);
                GeoPosition position = view.getMapViewer().convertPointToGeoPosition(point);

                if (getFromPosition() == null) {
                    setFromPosition(position);
                    getFromField().setText(position.getLatitude() + ", " + position.getLongitude());
                    view.addWaypoint(position);
                } else if (getToPosition() == null) {
                    setToPosition(position);
                    getToField().setText(position.getLatitude() + ", " + position.getLongitude());
                    view.addWaypoint(position);
                }
            }
        });
    }

    public void calculateRoute() {
        if (getFromPosition() != null && getToPosition() != null) {
            CitiBikeRequest request = new CitiBikeRequest(
                    new CitiBikeRequest.Location(getFromPosition().getLatitude(), getFromPosition().getLongitude()),
                    new CitiBikeRequest.Location(getToPosition().getLatitude(), getToPosition().getLongitude())
            );

            disposables.add(lambdaService.getClosestStations(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(SwingSchedulers.edt())
                    .subscribe(
                            this::handleClosestStationsResponse,
                            Throwable::printStackTrace
                    ));
        }
    }

    private void handleClosestStationsResponse(CitiBikeResponse response) {
        try {
            GeoPosition startStation = new GeoPosition(response.start.lat, response.start.lon);
            GeoPosition endStation = new GeoPosition(response.end.lat, response.end.lon);

            //flatmap is being used to chain more asynchronous opertaions

            disposables.add(routingService.getRouteFromApi(getFromPosition(), startStation)
                    .flatMap(route1 -> routingService.getRouteFromApi(endStation, getToPosition())
                            .map(route2 -> {
                                List<GeoPosition> combinedRoute = new ArrayList<>(route1);
                                combinedRoute.addAll(route2);
                                return combinedRoute;
                            }))
                    .subscribeOn(Schedulers.io())
                    .observeOn(SwingSchedulers.edt())
                    .subscribe(
                            route -> view.updateRoute(route, List.of(getFromPosition(), startStation, endStation, getToPosition())),
                            Throwable::printStackTrace
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to calculate route. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void clearMap() {
        fromPosition = null;
        toPosition = null;
        fromField.setText("");
        toField.setText("");
        view.clearMap();
        disposables.clear();
    }

    public GeoPosition getFromPosition() {
        return fromPosition;
    }

    public GeoPosition getToPosition() {
        return toPosition;
    }

    public void setFromPosition(GeoPosition fromPosition) {
        this.fromPosition = fromPosition;
    }

    public void setToPosition(GeoPosition toPosition) {
        this.toPosition = toPosition;
    }

    public JTextField getFromField() {
        return fromField;
    }

    public JTextField getToField() {
        return toField;
    }
}