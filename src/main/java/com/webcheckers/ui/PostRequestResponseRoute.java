package com.webcheckers.ui;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.logging.Logger;

import static com.webcheckers.ui.GetHomeRoute.CHALLENGE_USER_KEY;

/**
 * The {@code POST /requestResponse} route handler.
 *
 * @author Mario Castano
 */
public class PostRequestResponseRoute implements Route
{

  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());
  static final String GAME_ACCEPT = "gameAccept";
  private final PlayerLobby lobby;
  private static final String KING_JUMP = "src/test/java/com/webcheckers/test" +
          "-boards/multiJumpLongAfterKing.JSON";
  private static final String REQUIRE_JUMP = "src/test/java/com/webcheckers" +
          "/test-boards/requireJumpBoard.JSON";
  private static final String NECESSARY_JUMP_WHITE = "src/test/java/com" +
          "/webcheckers/test-boards/necesssaryJumpWhite.JSON";
  private static final String MULTI_JUMP_STILL_REQUIRED_WRONG = "src/test" +
          "/java/com/webcheckers/test-boards/multiJumpBoardStillJump.JSON";
  public static final String NO_MORE_MOVES = "src/test/java/com/webcheckers" +
          "/test-boards/no-more-moves.JSON";
  private static final String ABOUT_JUMP_ALL = "src/test/java/com/webcheckers" +
          "/test-boards/about-to-all-pieces-jumped.JSON";
  private static final String DEMO_1 = "src/test/java/com/webcheckers/test" +
          "-boards/test-demo-1.JSON";
  private static final String ABOUT_MOVES_NONE = "src/test/java/com" +
          "/webcheckers/test-boards/about-to-no-more-moves";
  private static final String KING_BACK_AGAIN = "src/test/java/com" +
          "/webcheckers/test-boards/kingBackOnSelf.JSON";

  /*
  This is all of the names to start preloaded games.
   */
  public static final String MULTI_KING = "TEST MULTI KING JUMP";
  public static final String REQUIRE_JUMP_ = "TEST REQUIRE JUMP";
  public static final String NEC_WHITE = "TEST NECESSARY WHITE";
  public static final String NO_MOVES = "TEST NO MORE MOVES";
  public static final String MULTI_JUMP_REQ = "TEST MULTI JUMP REQUIRE";
  public static final String ABOUTA_JUMP = "ABOUT TO JUMP ALL";
  public static final String ABOUT_NO_MORE_MOVES = "ABOUT NO MOVES";
  public static final String TEST_DEM1 = "TEST DEMO 1";
  public static final String KING_BACK = "KING BACK AGAIN";


  /**
   * Constructor for the {@code GET/game} route handler.
   *
   * @param lobby: the player lobby for the Response.
   */
  PostRequestResponseRoute(final PlayerLobby lobby)
  {
    //validation
    this.lobby = lobby;
  }

  private void startGameOrTest(String username, String opponent,
                               GameManager manager)
  {
    switch (username)
    {
      case MULTI_KING:
        manager.startTestGame(username, opponent, KING_JUMP, 1);
        break;
      case REQUIRE_JUMP_:
        manager.startTestGame(username, opponent, REQUIRE_JUMP, 1);
        break;
      case NEC_WHITE:
        manager.startTestGame(username, opponent, NECESSARY_JUMP_WHITE, 2);
        break;
      case NO_MOVES:
        manager.startTestGame(username, opponent, NO_MORE_MOVES, 1);
        break;
      case MULTI_JUMP_REQ:
        manager.startTestGame(username, opponent,
                MULTI_JUMP_STILL_REQUIRED_WRONG, 1);
        break;
      case ABOUTA_JUMP:
        manager.startTestGame(username, opponent, ABOUT_JUMP_ALL, 1);
        break;
      case TEST_DEM1:
        manager.startTestGame(username, opponent, DEMO_1, 1);
        break;
      case ABOUT_NO_MORE_MOVES:
        manager.startTestGame(username, opponent, ABOUT_MOVES_NONE, 1);
        break;
      case KING_BACK:
        manager.startTestGame(username, opponent, KING_BACK_AGAIN, 1);
        break;
      default:
        manager.startGame(username, opponent);
        break;
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * The handler for player response to game request that has been sent. First, a user challenges another user,
   * and a request is sent consists of the player's username and the challengee's username from
   * the list of users logged onto the playerLobby.
   */
  @Override
  public String handle(Request request, Response response)
  {
    LOG.config("Post Request Response has been invoked.");
    //retrieve the playerLobby object to verify that no time out has occurred
    final Session httpSession = request.session();
    final Player player = httpSession.attribute(GetHomeRoute.PLAYER_KEY);

    /* A null playerLobby indicates a timed out session or an illegal request on this URL.
     * In either case, we will redirect back to home.
     */
    if (player != null)
    {
      final String usernameStr = player.getUsername();
      final String accept = request.queryParams(GAME_ACCEPT);
      String oppPlayer = httpSession.attribute(CHALLENGE_USER_KEY);
      oppPlayer = oppPlayer.replace('-', ' ');
      GameManager gameManager = httpSession.attribute(GetHomeRoute.GAME_MANAGER_KEY);
      LOG.config("Response to: " + oppPlayer);
      switch (accept)
      {
        case "yes":
          lobby.removeChallenger(oppPlayer);
          lobby.removeChallenger(usernameStr);
          startGameOrTest(oppPlayer, usernameStr, gameManager);
          response.redirect(WebServer.GAME_URL);
          return "Game redirect";
        case "no":
          lobby.removeChallenger(oppPlayer);
          lobby.removeChallenger(usernameStr);
          response.redirect(WebServer.HOME_URL);
          return "Home Redirect";
        //Act upon the player's response to a game request
      }
    } else
    {
      response.redirect(WebServer.HOME_URL);
      return "Home Redirect";
    }
    return null;
  }

}

