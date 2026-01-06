package com.ignite.controller;

import com.ignite.model.ResolvedReport;
import com.ignite.service.AdminService;
import com.ignite.util.StyleUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;

public class ResolvedReportsController {

    private final AdminService adminService;
    private TableView<ResolvedReport> table;
    private ObservableList<ResolvedReport> data;

    public ResolvedReportsController() {
        this.adminService = new AdminService();
        this.data = FXCollections.observableArrayList();
    }

    /**
     * Create the main UI container for the Resolved Reports tab.
     * You can use this in AdminDashboardController like:
     *
     * ResolvedReportsController controller = new ResolvedReportsController();
     * VBox resolvedContent = controller.createResolvedReportsView();
     * tab.setContent(resolvedContent);
     */
    public VBox createResolvedReportsView() {
        VBox mainContainer = new VBox(15);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(20));
        StyleUtil.applyDarkTheme(mainContainer);

        // Title at top
        Label title = new Label("Resolved Reports");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 24px; -fx-font-weight: bold;");

        // Card container around table
        VBox tableContainer = new VBox(10);
        tableContainer.setAlignment(Pos.TOP_CENTER);
        tableContainer.setMaxWidth(950);
        tableContainer.setPadding(new Insets(15));
        tableContainer.setStyle(
                "-fx-background-color: #0b1723;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.25), 12,0,0,2);"
        );

        Label tableHeader = new Label("Resolved Reports History");
        tableHeader.setStyle(
                "-fx-text-fill: #00ff88;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 0 8 0;"
        );

        // Table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(450);
        table.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-table-cell-border-color: transparent;" +
                        "-fx-table-header-border-color: #00ff88;" +
                        "-fx-selection-bar: #003322;" +
                        "-fx-selection-bar-non-focused: #003322;"
        );

        // Load data initially
        data.setAll(adminService.getAllResolvedReports());
        table.setItems(data);

        // Define columns
        createColumns();

        // Add row coloring based on content type (POST vs COMMENT)
        installRowStyling();

        tableContainer.getChildren().addAll(tableHeader, table);

        // Refresh button under the card
        Button refreshBtn = StyleUtil.createSecondaryButton("Refresh");
        refreshBtn.setOnAction(e -> data.setAll(adminService.getAllResolvedReports()));

        HBox actions = new HBox(refreshBtn);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(10, 0, 0, 0));

        mainContainer.getChildren().addAll(title, tableContainer, actions);
        return mainContainer;
    }

    private void createColumns() {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");

        // ID
        TableColumn<ResolvedReport, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getResolvedId()));
        idCol.setPrefWidth(60);

        // Reporter ID
        TableColumn<ResolvedReport, Number> reporterCol = new TableColumn<>("Reporter ID");
        reporterCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getReporterUserId()));
        reporterCol.setPrefWidth(90);

        // Content Type (POST / COMMENT)
        TableColumn<ResolvedReport, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContentType()));
        typeCol.setPrefWidth(90);

        // Make the type column text colored/icons
        typeCol.setCellFactory(col -> new TableCell<ResolvedReport, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                // Display with a small icon-ish label
                if ("POST".equalsIgnoreCase(item)) {
                    setText("POST");
                    setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");
                } else if ("COMMENT".equalsIgnoreCase(item)) {
                    setText("COMMENT");
                    setStyle("-fx-text-fill: #ff8844; -fx-font-weight: bold;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #e0e0e0;");
                }
            }
        });

        // Content ID
        TableColumn<ResolvedReport, Number> contentIdCol = new TableColumn<>("Content ID");
        contentIdCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getContentId()));
        contentIdCol.setPrefWidth(90);

        // Report date
        TableColumn<ResolvedReport, String> reportedDateCol = new TableColumn<>("Reported");
        reportedDateCol.setCellValueFactory(c -> {
            if (c.getValue().getReportDate() == null) {
                return new SimpleStringProperty("N/A");
            }
            return new SimpleStringProperty(df.format(c.getValue().getReportDate()));
        });
        reportedDateCol.setPrefWidth(150);

        // Resolved date
        TableColumn<ResolvedReport, String> resolvedDateCol = new TableColumn<>("Resolved");
        resolvedDateCol.setCellValueFactory(c -> {
            if (c.getValue().getResolvedDate() == null) {
                return new SimpleStringProperty("N/A");
            }
            return new SimpleStringProperty(df.format(c.getValue().getResolvedDate()));
        });
        resolvedDateCol.setPrefWidth(150);

        // Reason
        TableColumn<ResolvedReport, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getReason() != null ? c.getValue().getReason() : ""
        ));
        reasonCol.setPrefWidth(260);

        table.getColumns().addAll(
                idCol,
                reporterCol,
                typeCol,
                contentIdCol,
                reportedDateCol,
                resolvedDateCol,
                reasonCol
        );

        // Header styles
        for (TableColumn<ResolvedReport, ?> column : table.getColumns()) {
            column.setStyle(
                    "-fx-background-color: #0d1b2a;" +
                            "-fx-text-fill: #00ff88;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 13px;" +
                            "-fx-alignment: CENTER-LEFT;"
            );
        }
    }

    private void installRowStyling() {
        table.setRowFactory(tv -> new TableRow<ResolvedReport>() {
            @Override
            protected void updateItem(ResolvedReport item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                // Selected row styling
                if (isSelected()) {
                    setStyle(
                            "-fx-background-color: linear-gradient(to right, #003300, #004466);" +
                                    "-fx-text-fill: #ffffff;"
                    );
                    return;
                }

                boolean even = getIndex() % 2 == 0;
                String baseEven = "#162536";
                String baseOdd  = "#1a2d3f";
                String baseBg   = even ? baseEven : baseOdd;

                // Content-type-based accent on the bottom border
                String borderColor = "#00ff88"; // default

                if (item.getContentType() != null) {
                    if ("POST".equalsIgnoreCase(item.getContentType())) {
                        borderColor = "#00ff88"; // green-ish for posts
                    } else if ("COMMENT".equalsIgnoreCase(item.getContentType())) {
                        borderColor = "#ff8844"; // orange-ish for comments
                    }
                }

                setStyle(
                        "-fx-background-color: " + baseBg + ";" +
                                "-fx-border-color: transparent transparent " + borderColor + " transparent;" +
                                "-fx-border-width: 0 0 1 0;"
                );
            }
        });
    }
}
