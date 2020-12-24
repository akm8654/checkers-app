package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.util.Message.info;
import static spark.Spark.halt;

/**
 * A route that checks who's turn it is during the game
 *
 * @author Austin Miller 'akm8654'
 */
public class PostCheckTurnRoute implements Route
{
  @Override
  public Object handle(Request request, Response response)
  {
    final Session httpSession = request.session();
    final Player player = httpSession.attribute(GetHomeRoute.PLAYER_KEY);
    final Gson gson = new Gson();

    if (player != null)
    {
      GameManager manager = httpSession.attribute(GetHomeRoute.GAME_MANAGER_KEY);
      int gameID = manager.getGameID(player.getUsername());
      final CheckerGame game = manager.getGame(gameID);
      if (game.getTurn().equals(player.getUsername()))
      {
        return gson.toJson(info("true"));
      } else
      {
        return gson.toJson(info("false"));
      }
    } else
    {
      response.redirect(WebServer.HOME_URL);
      halt();
      return "Redirected Home";
    }
  }
}
