package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static com.webcheckers.ui.GetHomeRoute.PLAYER_KEY;
import static com.webcheckers.ui.WebServer.HOME_URL;
import static com.webcheckers.util.Message.error;
import static com.webcheckers.util.Message.info;

/**
 * A route that handles resigning the game during a game being played.
 *
 * @author Austin Miller 'akm8654'
 * @author Sean Bergen 'sdb2139'
 */
public class PostResignRoute implements Route
{
  final public static String RESIGN_ATTR = "resigned";
  Gson gson = new Gson();

  /**
   * Handle the player resignation request by removing them from the game and sending a notification
   * via Json to the server/opponent.
   *
   * @param request  The Request object
   * @param response The Response object
   * @return the Gson.toJson() message indicating a successful resignation, or Null if the resignation fails
   * (which should only occur due to timed-out session).
   */
  @Override
  public Object handle(Request request, Response response)
  {
    final Session session = request.session();
    final Player player = session.attribute(PLAYER_KEY);
    if (player != null)
    {
      session.attribute(RESIGN_ATTR, true);
      GameManager manager = session.attribute(GAME_MANAGER_KEY);
      int gameID = manager.getGameID(player.getUsername());
      String status = manager.getGameOverStatus(gameID);
      if (status.equals("No"))
      {
        manager.setGameOver(gameID, player.getUsername() + " has resigned" +
                ".");
        return gson.toJson(info("Resign Successful"));
      } else
      {
        return gson.toJson(error("You can't resign: " + status + " \nRefresh " +
                "page to update!"));
      }
    } else
    {
      response.redirect(HOME_URL);
      return "Home Redirect";
    }
  }

}
