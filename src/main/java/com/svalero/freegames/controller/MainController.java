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

import java.util.List;

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
    private ComboBox<String> categoryCombo;

    @FXML
    private javafx.scene.image.ImageView thumbnailImage;

    private final ObservableList<Game> allGames = FXCollections.observableArrayList();
    private final ObservableList<Game> filteredGames = FXCollections.observableArrayList();

    private final FreeToGameService service = RetrofitClient.getService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
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
                    game.getGenre().toLowerCase().contains(selectedCategory.toLowerCase()))
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


}


