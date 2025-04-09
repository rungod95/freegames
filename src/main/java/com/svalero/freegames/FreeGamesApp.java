package com.svalero.freegames;

import com.svalero.freegames.model.Game;
import com.svalero.freegames.service.FreeToGameService;
import com.svalero.freegames.service.RetrofitClient;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class FreeGamesApp extends Application {

    @Override
    public void start(Stage stage){
        System.out.println("Initializing FreeGamesApp...");

    FreeToGameService service = RetrofitClient.getService();
        Observable<List<Game>> observable = service.getGames();

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                    games -> {
                    System.out.println("Games retrieved successfully:");
                    for (Game game : games) {
                        System.out.println("\uD83C\uDFAE" + game.getTitle());
                    }
                }, throwable -> {
                    System.err.println("❌ Error: " + throwable.getMessage()
                    );
                }
                );
    }

        public static void main(String[] args) {
            launch(args);

    }
    }

