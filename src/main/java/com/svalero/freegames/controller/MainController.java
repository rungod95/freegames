package com.svalero.freegames.controller;

import com.svalero.freegames.model.Game;
import com.svalero.freegames.service.FreeToGameService;
import com.svalero.freegames.service.RetrofitClient;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;

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

    private ObservableList<Game> gamesList = FXCollections.observableArrayList();
    private ObservableList<Game> filteredGames= FXCollections.observableArrayList();


    @FXML

    public void initialize() {

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        gamesTable.setItems(filteredGames);

        //Cargar plataformas
        platformCombo.getItems().addAll("All", "PC (Windows)", "Web Browser", "PC (Windows), Web Browser");
        platformCombo.setValue("All");

        // Load games from the API
        FreeToGameService service = RetrofitClient.getService();
        service.getGames()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                    this::loadGamestoTable,
                    error ->
                        System.out.println("❌ Error " + error.getMessage())
                );
    }




                private void loadGamestoTable(List<Game> games) {
                            Platform.runLater(() -> {
                            gamesList.clear();
                            gamesList.addAll(games);
                            applyFilters();
                            });
                        }
                        @FXML
                        public void onSearchClicked() {
                            applyFilters();
                        }

                        private void applyFilters() {
                            String searchText = searchField.getText().toLowerCase().trim();
                            String selectedPlatform = platformCombo.getValue();

                            filteredGames.setAll(
                                gamesList.stream()
                                    .filter(game -> game.getTitle().toLowerCase().contains(searchText))
                                    .filter(game -> selectedPlatform.equals("All") || game.getPlatform().equals(selectedPlatform))
                                    .toList()
                            );
                        }

                }

