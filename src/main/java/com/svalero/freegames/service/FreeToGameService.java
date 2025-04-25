package com.svalero.freegames.service;

import com.svalero.freegames.model.Game;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface FreeToGameService {

    @GET("games")
    Observable<List<Game>> getGames();

    @GET("games")
    Observable<List<Game>> getGamesByPlatform(@Query("platform") String platform);

    @GET("games")
    Observable<List<Game>> getGamesByGenre(@Query("genre") String genre);

    @GET("filtered-games")
    Observable<List<Game>> getGamesFromMicroservice(@Query("platform") String platform);



}
