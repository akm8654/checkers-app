package com.webcheckers.ui;

import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.ui.GetHomeRoute.PLAYER_KEY;
import static com.webcheckers.ui.GetReplayGameRoute.GAME_ID;
import static com.webcheckers.ui.WebServer.HOME_URL;

/**
 * The method that is called when the user decides they no longer want to
 * watch the replay.
 *
 * @author Austin Miller 'akm8654'
 */
public class PostReplayStopWatchingRoute implements Route
{
  private final ReplayManager rManager;

  public PostReplayStopWatchingRoute(ReplayManager replayManager)
  {
    this.rManager = replayManager;
  }

  @Override
  public Object handle(Request request, Response response)
  {
    final Session session = request.session();
    final Player player = session.attribute(PLAYER_KEY);
    if (player != null)
    {
      session.attribute(GAME_ID, null);
      rManager.stopWatching(player.getUsername());
    }
    response.redirect(HOME_URL);
    return "Redirect Home";
  }
}
