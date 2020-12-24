package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.ui.GetReplayGameRoute.GAME_ID;
import static com.webcheckers.ui.WebServer.HOME_URL;
import static com.webcheckers.util.Message.info;

/**
 * The route to go to the next move when inside the replay mode enhancement.
 * Returns a JSON object of the info message true when successful.
 *
 * @author Austin Miller 'akm8654'
 */
public class PostReplayNextTurnRoute implements Route
{
  private final ReplayManager rManager;

  public PostReplayNextTurnRoute(ReplayManager rManager)
  {
    this.rManager = rManager;
  }

  @Override
  public Object handle(Request request, Response response)
  {
    final Session session = request.session();
    final Player player = session.attribute(GetHomeRoute.PLAYER_KEY);
    final Gson gson = new Gson();
    if (player != null)
    {
      String username = player.getUsername();
      int gameID = session.attribute(GAME_ID);
      rManager.nextMove(username, gameID);
      return gson.toJson(info("true"));
    } else
    {
      response.redirect(HOME_URL);
      return gson.toJson(info("ERROR! Not signed in."));
    }
  }
}
