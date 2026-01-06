package com.ignite.controller;

import com.ignite.model.AuditLog;
import com.ignite.service.AdminService;
import com.ignite.util.StyleUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;

public class AuditLogController {

    private final AdminService adminService;
    private final ObservableList<AuditLog> logsData;
    private TableView<AuditLog> table;
    private String currentFilter = "RECENT"; // default

    public AuditLogController() {
        this.adminService = new AdminService();
        this.logsData = FXCollections.observableArrayList();
    }

    public VBox createAuditLogTab() {
        VBox root = new VBox(15);
        StyleUtil.applyDarkTheme(root);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        Label title = new Label("Audit Log");
        title.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 22px; -fx-font-weight: bold;");

        HBox controls = createControlsRow();
        VBox tableBox = createTableBox();

        VBox.setVgrow(tableBox, Priority.ALWAYS);

        root.getChildren().addAll(title, controls, tableBox);

        loadCurrentFilter();
        return root;
    }

    private HBox createControlsRow() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);

        Button recentBtn   = createFilterButton("Recent (200)", "RECENT");
        Button allBtn      = createFilterButton("All", "ALL");
        Button refreshBtn  = StyleUtil.createSecondaryButton("Refresh");

        refreshBtn.setOnAction(e -> loadCurrentFilter());

        row.getChildren().addAll(recentBtn, allBtn, refreshBtn);
        return row;
    }

    private Button createFilterButton(String text, String filterType) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #1a2d3f; " +
                        "-fx-text-fill: #00ff88; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 6 14; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #00ff88; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 6 14; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #1a2d3f; " +
                        "-fx-text-fill: #00ff88; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 6 14; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
        ));

        button.setOnAction(e -> {
            currentFilter = filterType;
            loadCurrentFilter();
        });

        return button;
    }

    private VBox createTableBox() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.TOP_CENTER);
        box.setMaxWidth(1000);
        box.setStyle("-fx-padding: 10 0 0 0;");

        table = new TableView<>();
        table.setItems(logsData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(500);
        table.setFocusTraversable(false);

        table.setStyle(
                "-fx-background-color: #1a2d3f; " +
                        "-fx-border-color: #00ff88; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-padding: 6; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.25), 12,0,0,3);"
        );

        // columns
        TableColumn<AuditLog, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getLogId()));
        idCol.setPrefWidth(60);

        TableColumn<AuditLog, String> actorCol = new TableColumn<>("Actor");
        actorCol.setCellValueFactory(c -> {
            AuditLog log = c.getValue();
            if (log.getAdminId() != null) {
                return new SimpleStringProperty("Admin #" + log.getAdminId());
            } else if (log.getUserId() != null) {
                return new SimpleStringProperty("User #" + log.getUserId());
            } else {
                return new SimpleStringProperty("System");
            }
        });
        actorCol.setPrefWidth(120);

        TableColumn<AuditLog, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(c -> new SimpleStringProperty(logSafe(c.getValue().getActionType())));
        actionCol.setPrefWidth(150);

        TableColumn<AuditLog, String> entityCol = new TableColumn<>("Entity");
        entityCol.setCellValueFactory(c -> new SimpleStringProperty(logSafe(c.getValue().getTargetEntity())));
        entityCol.setPrefWidth(120);

        TableColumn<AuditLog, String> targetCol = new TableColumn<>("Target");
        targetCol.setCellValueFactory(c -> new SimpleStringProperty(logSafe(c.getValue().getTargetId())));
        targetCol.setPrefWidth(100);

        TableColumn<AuditLog, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(c -> {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            return new SimpleStringProperty(
                    c.getValue().getTimestamp() != null
                            ? df.format(c.getValue().getTimestamp())
                            : "N/A"
            );
        });
        timeCol.setPrefWidth(180);

        table.getColumns().setAll(idCol, actorCol, actionCol, entityCol, targetCol, timeCol);

        // header style
        for (TableColumn<AuditLog, ?> col : table.getColumns()) {
            col.setStyle(
                    "-fx-background-color: #0d1b2a; " +
                            "-fx-text-fill: #00ff88; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 12px; " +
                            "-fx-border-color: #00ff88; " +
                            "-fx-border-width: 0 0 1 0;"
            );
        }

        // row styling (text color per entity, like ResolvedReportsController)
        installRowStyling();

        // scrollbar styling
        table.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Node vBar = table.lookup(".scroll-bar:vertical");
                if (vBar != null) {
                    vBar.setStyle("-fx-background-color: #0d1b2a; -fx-background-radius: 0;");
                }
                Node thumb = table.lookup(".scroll-bar:vertical .thumb");
                if (thumb != null) {
                    thumb.setStyle("-fx-background-color: #00ff88; -fx-background-radius: 4;");
                }
            }
        });

        box.getChildren().add(table);
        return box;
    }


    private void installRowStyling() {
        table.setRowFactory(tv -> new TableRow<AuditLog>() {
            @Override
            protected void updateItem(AuditLog item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                // selection styling first
                if (isSelected()) {
                    setStyle(
                            "-fx-background-color: linear-gradient(to right, #003300, #004466);" +
                                    "-fx-text-fill: #ffffff;"
                    );
                    return;
                }

                // base dark background (striped)
                boolean even = getIndex() % 2 == 0;
                String baseBg = even ? "#1a2d3f" : "#162536";

                // entity-based text color
                String entity = item.getTargetEntity() == null
                        ? ""
                        : item.getTargetEntity().trim().toUpperCase();

                String textColor;
                switch (entity) {
                    case "USER":
                        textColor = "#00e6c3";   // teal
                        break;
                    case "POST":
                        textColor = "#4dabff";   // blue
                        break;
                    case "COMMENT":
                        textColor = "#ffb74d";   // orange
                        break;
                    case "ALERT":
                        textColor = "#ff5c93";   // pink
                        break;
                    case "INQUIRY":
                        textColor = "#bb86fc";   // purple
                        break;
                    default:
                        textColor = "#e0e0e0";   // default light gray
                        break;
                }

                setStyle(
                        "-fx-background-color: " + baseBg + ";" +
                                "-fx-text-fill: " + textColor + ";"
                );
            }
        });
    }



    private String logSafe(String s) {
        return s == null ? "" : s;
    }

    // ---------- data loading ----------

    private void loadCurrentFilter() {
        switch (currentFilter) {
            case "ALL"    -> logsData.setAll(adminService.getAllAuditLogs());
            case "RECENT" -> logsData.setAll(adminService.getRecentAuditLogs(200));
            default       -> logsData.setAll(adminService.getRecentAuditLogs(200));
        }
    }
}
