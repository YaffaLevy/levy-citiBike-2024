package ui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CitiBikeComponent extends JComponent {
    private final JXMapViewer mapViewer;
    private final List<GeoPosition> track;
    private final Set<Waypoint> waypoints;
    private final RoutePainter routePainter;

    public CitiBikeComponent() {
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
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

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

    public void updateRoute(List<GeoPosition> route, List<GeoPosition> stations) {
        if (route == null || route.isEmpty()) {
            throw new IllegalArgumentException("Route cannot be null or empty.");
        }

        clearRoute();
        track.addAll(route);
        routePainter.setTrack(track);

        for (GeoPosition station : stations) {
            addWaypoint(station);
        }

        mapViewer.zoomToBestFit(Set.copyOf(route), 1.0);
        updateMap();
    }

    public void clearRoute() {
        track.clear();
        waypoints.clear();
        updateMap();
    }

    public void clearMap() {
        clearRoute();
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }
}