package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.BoardView;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.webcheckers.ui.GetGameRoute.*;
import static com.webcheckers.ui.WebServer.HOME_URL;
import static com.webcheckers.util.Message.info;

/**
 * Used to retrieve the game.ftl for the requested game starting at the very
 * beginnning.
 *
 * @author Austin Miller 'akm8654'
 */
public class GetReplayGameRoute implements Route
{
  public static final String GAME_ID = "gameID";
  private static final String RED_PLAYER = "redPlayer";
  public static final String MODE_OPTIONS_AS_JSON = "modeOptionsAsJSON";
  public static final String HAS_NEXT = "hasNext";
  public static final String HAS_PREVIOUS = "hasPrevious";
  public static final String NOT_REPLAY = "notReplay";
  private final ReplayManager rManager;
  private Gson gson = new Gson();
  private final TemplateEngine engine;

  /**
   * Constructor method
   *
   * @param templateEngine the engine for rendering the view
   * @param replayManager the manager of all replays for the server.
   */
  public GetReplayGameRoute(TemplateEngine templateEngine,
                            ReplayManager replayManager)
  {
    this.rManager = replayManager;
    Objects.requireNonNull(templateEngine,
            "templateEngine must not be null");
    this.engine = templateEngine;
  }

  @Override
  public Object handle(Request request, Response response)
  {
    final Map<String, Object> vm = new HashMap<>();
    vm.put(GetHomeRoute.TITLE_ATTR, "Web Checkers");

    final Session session = request.session();
    final Player watcher = session.attribute(GetHomeRoute.PLAYER_KEY);

    String theme = session.attribute("theme");
    if (theme != null)
    {
      vm.put("theme", true);
    } else
    {
      vm.put("theme", false);
    }

    if (watcher != null)
    {
      watcher.enterOrExitArchive(false);
      String gameIDStr = request.queryParams("replayRequest");
      int gameID;
      if (gameIDStr != null)
      {
        gameID = Integer.parseInt(gameIDStr);
        session.attribute(GAME_ID, gameID);
      } else
      {
        gameID = session.attribute(GAME_ID);
      }
      String CURRENT_PLAYER = "currentUser";
      String username = watcher.getUsername();
      vm.put(CURRENT_PLAYER, username);
      vm.put(GetGameRoute.VIEW_MODE, Player.ViewMode.REPLAY);

      int move = rManager.getMove(username, gameID);
      boolean hasPrevious = move > 0;
      boolean hasNext = move < rManager.maxMoves(gameID)-1;
      Map<String, Object> modeOptions = new HashMap<>();
      modeOptions.put(HAS_NEXT, hasNext);
      modeOptions.put(HAS_PREVIOUS, hasPrevious);
      vm.put(MODE_OPTIONS_AS_JSON, gson.toJson(modeOptions));

      CheckerGame game = rManager.getGameAtMove(gameID, move);
      vm.put(RED_PLAYER, game.getRedPlayer());
      vm.put(WHITE_PLAYER, game.getWhitePlayer());
      vm.put(ACTIVE_COLOR, game.getColor());
      BoardView bV = new BoardView(game.getBoard());
      vm.put(GAME_BOARD_VIEW, bV);
      if (gameIDStr != null)
      {
        vm.put("message",
                info("You have started viewing " + game.getRedPlayer().getUsername() +
                        " v. " + game.getWhitePlayer().getUsername() +
                        "!"));
      } else {
        vm.put("message",
                info("You're on move " + move + " of " +
                        (rManager.maxMoves(gameID)-1) + "."));
      }
      vm.put(NOT_REPLAY, false);
      vm.put(VIEWERS, "No viewers in replay mode!");
      return engine.render(new ModelAndView(vm, VIEW_NAME));
    } else
    {
      response.redirect(HOME_URL);
      return "Home Redirect";
    }
  }
}
