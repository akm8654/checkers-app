package com.webcheckers.ui;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static com.webcheckers.ui.GetHomeRoute.PLAYER_KEY;
import static com.webcheckers.ui.WebServer.*;

/**
 * A route that helps store the theme into the local storage when switching
 * between routes to be read later.
 *
 * @author Austin Miller 'akm8654'
 */
public class PostChangeThemeRoute implements Route
{
  public static String THEME = "theme";
  private ReplayManager replayManager;

  public PostChangeThemeRoute(ReplayManager rManager)
  {
    replayManager = rManager;
  }

  @Override
  public Object handle(Request request, Response response)
  {
    Session session = request.session();
    String theme = session.attribute(THEME);
    GameManager manager = session.attribute(GAME_MANAGER_KEY);
    Player player = session.attribute(PLAYER_KEY);
    if (player != null)
    {
      if (theme == null)
      {
        session.attribute(THEME, "pink");
      } else
      {
        session.attribute(THEME, null);
      }
      if (player.isInGame())
      {
        response.redirect(GAME_URL);
        return "Redirected Game";
      } else if (manager.isSpectator(player.getUsername()))
      {
        response.redirect(SPECTATOR_GAME_URL);
        return "Redirected Spectator";
      } else if (player.inArchive())
      {
        response.redirect(REPLAY_URL);
        return "Redirected Replay Archive";
      } else if (replayManager.isWatching(player.getUsername()))
      {
        response.redirect(REPLAY_GAME);
        return "Redirected Replay";
      } else
      {
        response.redirect(HOME_URL);
        return "Redirected Home";
      }
    } else
    {
      response.redirect(HOME_URL);
      return "Redirected Home";
    }
  }
}
