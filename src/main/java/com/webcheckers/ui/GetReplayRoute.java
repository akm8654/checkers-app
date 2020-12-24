package com.webcheckers.ui;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;
import spark.*;

import java.util.*;

import static com.webcheckers.ui.WebServer.GAME_URL;
import static com.webcheckers.ui.WebServer.HOME_URL;

/**
 * The route that is called when the player wants to enter the replay archive.
 *
 * @author Austin Miller 'akm8654'
 */
public class GetReplayRoute implements Route
{
  private final ReplayManager rManager;
  private final TemplateEngine engine;
  private static final String TITLE = "Replay Lobby";
  private static final String REPLAY_GAMES = "replayGames";
  private static final String VIEW_NAME = "replay.ftl";

  public GetReplayRoute(TemplateEngine engine, ReplayManager rManager)
  {
    this.engine = Objects.requireNonNull(engine, "templateEngine is " +
            "required");
    this.rManager = rManager;
  }

  /**
   * nested class to sort the games.
   */
  static class SortByPercentage implements Comparator<CheckerGame>
  {
    public int compare(CheckerGame a, CheckerGame b)
    {
      return (int) b.winningAverage() * 100 - (int) a.winningAverage() * 100;
    }
  }

  @Override
  public Object handle(Request request, Response response)
  {
    final Session session = request.session();
    Map<String, Object> vm = new HashMap<>();
    GameManager manager = session.attribute(GetHomeRoute.GAME_MANAGER_KEY);
    vm.put(GetHomeRoute.TITLE_ATTR, TITLE);
    Player watcher = session.attribute(GetHomeRoute.PLAYER_KEY);
    if (watcher != null)
    {
      vm.put(GetHomeRoute.CURRENT_USER_ATTR, watcher.getUsername());
      vm.put(GetHomeRoute.SIGN_IN_KEY, true);
      watcher.enterOrExitArchive(true);
      if (watcher.isInGame())
      {
        response.redirect(GAME_URL);
        return "Game Redirect";
      }
      List<Integer> oldGames = rManager.getGameOverIDs();
      List<CheckerGame> games = new ArrayList<>();
      for (Integer gameID : oldGames)
      {
        games.add(manager.getGame(gameID));
      }
      games.sort(new SortByPercentage());
      vm.put(REPLAY_GAMES, games);
      return engine.render(new ModelAndView(vm, VIEW_NAME));
    } else
    {
      response.redirect(HOME_URL);
      return "Home Redirect";
    }
  }
}
