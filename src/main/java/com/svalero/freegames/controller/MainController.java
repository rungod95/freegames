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
    private TableColumn<Game, String> genreColumn;

    @FXML
    private TableColumn<Game, String> platformColumn;

    @FXML
    private TableColumn<Game, String> publisherColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> platformCombo;

    @FXML
    private ComboBox<String> genreCombo;

    @FXML
    private javafx.scene.image.ImageView thumbnailImage;

    private final ObservableList<Game> allGames = FXCollections.observableArrayList();
    private final ObservableList<Game> filteredGames = FXCollections.observableArrayList();

    private final FreeToGameService externalService = RetrofitClient.getExternalService();
    private final FreeToGameService localService = RetrofitClient.getLocalService();


    /// Método para inicializar la tabla y cargar los datos
    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        gamesTable.setItems(filteredGames);

        platformCombo.getItems().addAll("All", "PC", "Browser");
        platformCombo.setValue("All");
        genreCombo.getItems().addAll("All", "Shooter", "MMORPG", "Strategy", "Racing", "Sports", "Card Game", "Fighting");
        genreCombo.setValue("All");

        gamesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadThumbnail(newSelection.getThumbnail());
            }
        });



        externalService.getGames()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe(
                this::loadGamesToTable,
                error -> System.err.println("❌ Error cargando juegos: " + error.getMessage())
            );
    }

    /// Método para cargar los juegos en la tabla
    private void loadGamesToTable(List<Game> games) {
        Platform.runLater(() -> {
            allGames.clear();
            allGames.addAll(games);
            applyFilters();
        });
    }

    /// Método para buscar juegos
    @FXML
    public void onSearchClicked() {
        applyFilters();
    }
/// Método para filtrar por plataforma
    @FXML
    public void onPlatformSelected() {
        String selected = platformCombo.getValue();

        if (selected.equals("All")) {
            externalService.getGames()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                    this::loadGamesToTable,
                    error -> System.err.println("❌ Error al cargar todos los juegos: " + error.getMessage())
                );
        } else {
            localService.getGamesFromMicroservice(selected.toLowerCase())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                    this::loadGamesToTable,
                    error -> System.err.println("❌ Error al filtrar por plataforma desde microservicio: " + error.getMessage())
                );
        }
    }

    @FXML
    public void onGenreSelected() {
        applyFilters();
    }

    /// Método para aplicar los filtros de búsqueda y género
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedGenre = genreCombo.getValue();

        filteredGames.setAll(
            allGames.stream()
                .filter(game -> game.getTitle().toLowerCase().contains(searchText))
                .filter(game -> selectedGenre.equals("All") ||
                (game.getGenre() != null && game.getGenre().toLowerCase().contains(selectedGenre.toLowerCase())))

            .toList()
        );
    }


    /// Método para cargar la miniatura
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


    /// Método para exportar a CSV y comprimirlo
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

        // Exporta a CSV
        try (FileWriter writer = new FileWriter(csvFile);
        CSVPrinter csvPrinter = new CSVPrinter( writer, CSVFormat.DEFAULT.withHeader("Title", "Genre", "Platform", "Publisher"))) {
            for (Game game : filteredGames) {
                csvPrinter.printRecord(game.getTitle(), game.getGenre(), game.getPlatform(), game.getPublisher());
            }
        }
        // Comprimi el archivo CSV

        try (FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            Path csvPath = Paths.get(csvFile);
            ZipEntry zipEntry = new ZipEntry(csvPath.getFileName().toString());
            zipOut.putNextEntry(zipEntry);
            Files.copy(csvPath, zipOut);
            zipOut.closeEntry();
        }
        // Elimina el archivo CSV original
        Files.deleteIfExists(Paths.get(csvFile));
    }


}


