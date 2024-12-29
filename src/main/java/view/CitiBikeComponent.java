package view;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import view.RoutePainter;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CitiBikeComponent extends JComponent {
    private final JXMapViewer mapViewer;
    private final List<GeoPosition> track;
    private final Set<Waypoint> waypoints;
    private final RoutePainter routePainter;
    private final JTextField fromField;
    private final JTextField toField;
    private GeoPosition fromPosition;
    private GeoPosition toPosition;

    public CitiBikeComponent(JTextField fromField, JTextField toField) {
        this.fromField = fromField;
        this.toField = toField;

        mapViewer = new JXMapViewer();

        // Create a TileFactoryInfo for OpenStreetMap
        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set the initial focus
        GeoPosition initialPosition = new GeoPosition(40.77172827726854, -73.98846745491028); // Example: New York City
        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(initialPosition);


        track = new ArrayList<>();
        waypoints = new HashSet<>();
        routePainter = new RoutePainter(track);

        // Add interactivity
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));


        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Point2D point = new Point2D.Double(x, y);
                GeoPosition position = mapViewer.convertPointToGeoPosition(point);

                if (fromPosition == null) {
                    fromPosition = position;
                    fromField.setText(position.getLatitude() + ", " + position.getLongitude());
                    addWaypoint(position);
                } else if (toPosition == null) {
                    toPosition = position;
                    toField.setText(position.getLatitude() + ", " + position.getLongitude());
                    addWaypoint(position);
                }
            }
        });

        setLayout(new BorderLayout());
        add(mapViewer, BorderLayout.CENTER);
    }

    public void updateMap() {
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints(waypoints);

        List<Painter<JXMapViewer>> painters = List.of(routePainter, waypointPainter);
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        mapViewer.setOverlayPainter(painter);


        if (!track.isEmpty()) {
            mapViewer.setZoom(5);
            mapViewer.setAddressLocation(track.get(0));
        }
    }

    public void addWaypoint(GeoPosition position) {
        waypoints.add(new DefaultWaypoint(position));
        updateMap();
    }

    public void addTrackPoint(GeoPosition position) {
        track.add(position);
        routePainter.setTrack(track);
        updateMap();
    }

    public void calculateRoute(GeoPosition startStation, GeoPosition endStation) {
        if (fromPosition != null && toPosition != null) {
            track.clear();
            track.add(fromPosition);
            track.add(startStation);
            track.add(endStation);
            track.add(toPosition);
            routePainter.setTrack(track);

            addWaypoint(startStation);
            addWaypoint(endStation);

            mapViewer.zoomToBestFit(Set.of(fromPosition, startStation, endStation, toPosition), 1.0);

            updateMap();

        }

    }

    public void clearMap() {
        fromPosition = null;
        toPosition = null;
        fromField.setText("");
        toField.setText("");
        track.clear();
        waypoints.clear();
        updateMap();
    }

    public GeoPosition getFromPosition() {
        return fromPosition;
    }

    public GeoPosition getToPosition() {
        return toPosition;
    }
}