package com.svalero.freegames.controller;

import com.svalero.freegames.model.Game;
import com.svalero.freegames.service.FreeToGameService;
import com.svalero.freegames.service.RetrofitClient;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainController {

    @FXML
    private TableView<Game> gamesTable;

    @FXML
    private TableColumn<Game, String> titleColumn;

    @FXML
    private TableColumn<Game, String> categoryColumn;

    @FXML
    private TableColumn<Game, String> platformColumn;

    @FXML
    private TableColumn<Game, String> publisherColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> platformCombo;

    @FXML
    private ComboBox<String> categoryCombo;

    @FXML
    private javafx.scene.image.ImageView thumbnailImage;

    private final ObservableList<Game> allGames = FXCollections.observableArrayList();
    private final ObservableList<Game> filteredGames = FXCollections.observableArrayList();

    private final FreeToGameService service = RetrofitClient.getService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        gamesTable.setItems(filteredGames);

        platformCombo.getItems().addAll("All", "PC", "Browser");
        platformCombo.setValue("All");
        categoryCombo.getItems().addAll("All", "Shooter", "MMORPG", "Strategy", "Racing", "Sports", "Card Game", "Fighting");
        categoryCombo.setValue("All");

        gamesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadThumbnail(newSelection.getThumbnail());
            }
        });


        // Carga inicial de todos los juegos
        service.getGames()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe(
                this::loadGamesToTable,
                error -> System.err.println("❌ Error cargando juegos: " + error.getMessage())
            );
    }

    private void loadGamesToTable(List<Game> games) {
        Platform.runLater(() -> {
            allGames.clear();
            allGames.addAll(games);
            applyFilters();
        });
    }

    @FXML
    public void onSearchClicked() {
        applyFilters();
    }

    @FXML
    public void onPlatformSelected() {
        String selected = platformCombo.getValue();

        if (selected.equals("All")) {
            // Obtener todos los juegos
            service.getGames()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                    this::loadGamesToTable,
                    error -> System.err.println("❌ Error al cargar todos los juegos: " + error.getMessage())
                );
        } else {
            // Obtener juegos por plataforma desde la API
            service.getGamesByPlatform(selected.toLowerCase())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                    this::loadGamesToTable,
                    error -> System.err.println("❌ Error al filtrar por plataforma: " + error.getMessage())
                );
        }
    }
    @FXML
    public void onCategorySelected() {
        applyFilters();
    }


    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryCombo.getValue();

        filteredGames.setAll(
            allGames.stream()
                .filter(game -> game.getTitle().toLowerCase().contains(searchText))
                .filter(game -> selectedCategory.equals("All") ||
                    game.getCategory().toLowerCase().contains(selectedCategory.toLowerCase()))
                .toList()
        );
    }

    private void loadThumbnail(String imageUrl) {
        Platform.runLater(() -> {
            try {
                javafx.scene.image.Image image = new javafx.scene.image.Image(imageUrl, true);
                thumbnailImage.setImage(image);
            } catch (Exception e) {
                System.err.println("❌ Error cargando imagen: " + e.getMessage());
            }
        });
    }

    @FXML
    public void onExportClicked() {
        CompletableFuture.runAsync(() -> {
            try {
                exportToCSVandZip();
                Platform.runLater(() -> System.out.println("✅ Exportación completada"));
            } catch (IOException e) {
                Platform.runLater(() -> System.err.println("❌ Error exportando: " + e.getMessage()));
            }
        });
    }

    private void exportToCSVandZip() throws IOException {
        String csvFile = "games.csv";
        String zipFile = "games.zip";

        // Exportar a CSV
        try (FileWriter writer = new FileWriter(csvFile);
        CSVPrinter csvPrinter = new CSVPrinter( writer, CSVFormat.DEFAULT.withHeader("Title", "Category", "Platform", "Publisher"))) {
            for (Game game : filteredGames) {
                csvPrinter.printRecord(game.getTitle(), game.getCategory(), game.getPlatform(), game.getPublisher());
            }
        }
        // Comprimir el archivo CSV

        try (FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            Path csvPath = Paths.get(csvFile);
            ZipEntry zipEntry = new ZipEntry(csvPath.getFileName().toString());
            zipOut.putNextEntry(zipEntry);
            Files.copy(csvPath, zipOut);
            zipOut.closeEntry();
        }
        // Eliminar el archivo CSV original
        Files.deleteIfExists(Paths.get(csvFile));
    }


}


