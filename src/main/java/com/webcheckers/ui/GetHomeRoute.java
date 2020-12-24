package com.webcheckers.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;
import spark.*;

import com.webcheckers.util.Message;

import static com.webcheckers.ui.PostRequestGameRoute.MESSAGE;
import static com.webcheckers.ui.PostResignRoute.RESIGN_ATTR;

/**
 * The UI Controller to GET the Home page.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author Austin Miller 'akm8654@rit.edu'
 */
public class GetHomeRoute implements Route
{
  static final String TITLE_ATTR = "title";

  //private static final Message WELCOME_MSG = Message.info("Welcome to the world of online Checkers.");
  static final String NEW_PLAYER_ATTR = "newPlayer";
  static final String VIEW_NAME = "home.ftl";
  static final String TITLE = "Welcome to WebCheckers!";
  static final String PLAYER_KEY = "player";
  static final String PLAYER_LOBBY_KEY = "player-lobby";
  static final String SIGN_IN_KEY = "signIn";
  static final String PLAYER_NUM_KEY = "playerNum";
  static final String GAME_MANAGER_KEY = "gameManager";
  static final String CHALLENGE_USER_KEY = "challengeUser";
  static final String CURRENT_USER_ATTR = "currentUser";
  static final String CHALLENGED_KEY = "pendingChallenge";
  static final String ERROR_MESSAGE_KEY = "errorMessage";
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

  private final TemplateEngine templateEngine;
  private final PlayerLobby lobby;
  private final GameManager manager;
  private final ReplayManager rManager;

  /**
   * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
   *
   * @param templateEngine the HTML template rendering engine
   */
  public GetHomeRoute(final TemplateEngine templateEngine,
                      PlayerLobby playerLobby, GameManager gameManager,
                      ReplayManager rManager)
  {
    this.lobby = playerLobby;
    this.manager = gameManager;
    this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
    //
    this.rManager = rManager;
    LOG.config("GetHomeRoute is initialized.");
  }

  /**
   * Render the WebCheckers Home page.
   *
   * @param request  the HTTP request
   * @param response the HTTP response
   * @return the rendered HTML for the Home page
   */
  @Override
  public Object handle(Request request, Response response)
  {
    // retrieve the HTTP session
    final Session session = request.session();

    // start building the View-Model
    final Map<String, Object> vm = new HashMap<>();

    vm.put(TITLE_ATTR, TITLE);
    Player player = session.attribute(PLAYER_KEY);

    String msg = session.attribute(MESSAGE);
    if (msg != null)
    {
      vm.put(MESSAGE, Message.info(msg));
    }
    String errorMsg = session.attribute(ERROR_MESSAGE_KEY);
    if (errorMsg != null)
    {
      if (msg != null)
      {
        vm.replace(MESSAGE, Message.error(errorMsg));
      } else
      {
        vm.put(MESSAGE, Message.error(errorMsg));
      }
    }
    session.attribute(PLAYER_LOBBY_KEY, lobby);
    session.attribute(GAME_MANAGER_KEY, manager);
    // if this is a brand new browser session or a session that timed out
    if (player == null)
    {
      vm.replace(TITLE_ATTR, TITLE + " Please sign-in.");
      vm.put(SIGN_IN_KEY, false);
      vm.put(PLAYER_NUM_KEY, lobby.getUsernames().size());
      vm.put("gameNum", manager.getGames().size());
      // get the object that will provide client-specific services for this player
      vm.put(NEW_PLAYER_ATTR, true);
    } else
    {
      player.enterOrExitArchive(false);
      // If the player is currently in a game, then they need to be redirected
      // to the game screen.
      if (player.isInGame())
      {
        response.redirect(WebServer.GAME_URL);
        return null;
      } else
      {
        int gameID = manager.getGameID(player.getUsername());
        if(gameID != -1)
        {
          rManager.endGame(gameID);
          manager.endGame(gameID);
          manager.removeFromGame(player.getUsername());
        }
        vm.put(SIGN_IN_KEY, true);
        session.attribute(RESIGN_ATTR, false);
        vm.put(CURRENT_USER_ATTR, player.getUsername());
        Map<String, String> challenges = lobby.getChallenges();
        if (challenges.containsKey(player.getUsername()))
        {
          session.attribute(CHALLENGE_USER_KEY,
                  challenges.get(player.getUsername()));
          vm.put(CHALLENGED_KEY, true);
          vm.put(CHALLENGE_USER_KEY, challenges.get(player.getUsername()));
        }
        List<String> usernames = lobby.getUsernames();
        List<CheckerGame> games = manager.getGames();
        vm.put("games", games);
        usernames.remove(player.getUsername());
        vm.put("usernames", usernames);
      }
    }
    return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
  }
}
