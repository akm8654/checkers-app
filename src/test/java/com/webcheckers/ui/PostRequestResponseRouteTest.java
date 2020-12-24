package com.webcheckers.ui;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import java.util.ArrayList;
import java.util.Arrays;

import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static com.webcheckers.ui.PostRequestResponseRoute.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test for the {@link PostRequestResponseRoute}
 *
 * @author Mario Castano 'mac3186'
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class PostRequestResponseRouteTest
{
  //Strings representing two players, one challenging the other
  private static final String PLAYER1 = "player1Username";
  private static final String PLAYER2 = "player2Username";

  //Friendly Object (assumed to work 100% as expected)
  private PlayerLobby lobby;

  /**
   * Mock Object Attributes for testing.
   * <p>
   * Direct testing of these is unnecessary in this route's test,
   * but interactions between them must work as expected.
   */
  private Request request;
  private Session session;
  private Response response;
  private TemplateEngine engine;
  private Player challengerP1;
  private Player opponentP2;
  private GameManager manager;

  /**
   * The Component-Under-Test (CuT).
   * <p>
   * This route is a stateless component, so we only need one.
   * The {@link Player} component is considered a "friendly" dependency.
   */
  private PostRequestResponseRoute CuT;

  @BeforeEach
  public void setup()
  {
    //Mock the initialization step before the route's handler method is used and mock unfriendly interacting classes
    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    engine = mock(TemplateEngine.class);
    response = mock(Response.class);
    challengerP1 = mock(Player.class);
    opponentP2 = mock(Player.class);
    manager = mock(GameManager.class);

    //initialize the gameManager
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    //Initialization directly with class is safe because PlayerLobby is friendly
    lobby = new PlayerLobby();
    //Add the players to the lobby
    lobby.newPlayer(opponentP2);
    lobby.newPlayer(challengerP1);
    //Store information in the mock session
    when(session.attribute(GetHomeRoute.PLAYER_LOBBY_KEY)).thenReturn(lobby);
    //Setup mock calls to player classes for retrieving usernames
    when(challengerP1.getUsername()).thenReturn(PLAYER1);
    when(opponentP2.getUsername()).thenReturn(PLAYER2);
    //Setup a game request between the two players
    lobby.challenge(PLAYER2, PLAYER1);

    //Create a unique CuT for each test.
    CuT = new PostRequestResponseRoute(lobby);
  }

  /**
   * Tests that when there is no player that it is redirected home.
   */
  @Test
  public void noPlayer()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(null);
    assertEquals("Home Redirect", CuT.handle(request, response));
  }

  /**
   * Test that when the player responds "yes" to a challenge, that they and their opponent
   * are put into a game, and removed from the available list of challengers.
   */
  @Test
  public void opponent_accepts_challenge()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(challengerP1);
    when(challengerP1.getUsername()).thenReturn(PLAYER1);
    when(request.queryParams(PostRequestResponseRoute.GAME_ACCEPT)).thenReturn("yes");
    when(session.attribute(GetHomeRoute.CHALLENGE_USER_KEY)).thenReturn(PLAYER2);

    final TemplateEngineTest testHelper = new TemplateEngineTest();
    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

    //Test is invoked
    assertEquals("Game redirect", CuT.handle(request, response));

    //Ensure that player acceptance was handled properly
    assertFalse(lobby.getChallengers().contains(PLAYER1));
    assertFalse(lobby.getChallengers().contains(PLAYER2));
  }


  /**
   * Test that when a player declines a challenge, both players are redirected to the homepage,
   * and are able to challenge another open player.
   */
  @Test
  public void opponent_declines_challenge()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(challengerP1);
    when(request.queryParams(PostRequestResponseRoute.GAME_ACCEPT)).thenReturn("no");
    when(session.attribute(GetHomeRoute.CHALLENGE_USER_KEY)).thenReturn(PLAYER2);

    final TemplateEngineTest testHelper = new TemplateEngineTest();
    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

    //Test is invoked
    assertEquals("Home Redirect", CuT.handle(request, response));

    //Ensure that player denial was handled properly
    assertFalse(lobby.getChallengers().contains(PLAYER1));
    assertFalse(lobby.getChallengers().contains(PLAYER2));
  }

  @Test
  public void start_test_games()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(challengerP1);
    when(request.queryParams(PostRequestResponseRoute.GAME_ACCEPT)).thenReturn("yes");
    ArrayList<String> game_names = new ArrayList<String>(
            Arrays.asList(MULTI_KING, REQUIRE_JUMP_, NEC_WHITE, NO_MOVES,
                    MULTI_JUMP_REQ, ABOUTA_JUMP, ABOUT_NO_MORE_MOVES,
                    TEST_DEM1, KING_BACK));
    for (String username : game_names)
    {
      when(session.attribute(GetHomeRoute.CHALLENGE_USER_KEY)).thenReturn(username);
      assertEquals("Game redirect", CuT.handle(request, response));
    }
  }
}