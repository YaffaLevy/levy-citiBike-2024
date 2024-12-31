package ui;

import javax.swing.*;
import java.awt.*;

public class CitiBikeFrame extends JFrame {
    private final CitiBikeComponent mapComponent;
    private final CitiBikeController controller;
    private final JTextField fromField;
    private final JTextField toField;

    public CitiBikeFrame() {
        setTitle("CitiBike Map");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        fromField = new JTextField(20);
        toField = new JTextField(20);

        mapComponent = new CitiBikeComponent();
        controller = new CitiBikeController(mapComponent, fromField, toField);

        add(mapComponent, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 2));
        controlPanel.add(new JLabel("From:"));
        controlPanel.add(fromField);
        controlPanel.add(new JLabel("To:"));
        controlPanel.add(toField);

        JButton calculateButton = new JButton("Calculate Route");
        calculateButton.addActionListener(e -> controller.calculateRoute());
        JButton clearButton = new JButton("Clear Map");
        clearButton.addActionListener(e -> controller.clearMap());

        controlPanel.add(calculateButton);
        controlPanel.add(clearButton);

        add(controlPanel, BorderLayout.SOUTH);
    }
}