package com.webcheckers.ui;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Board;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Piece;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import static com.webcheckers.model.Player.ViewMode.SPECTATOR;
import static com.webcheckers.ui.GetGameRoute.*;
import static com.webcheckers.ui.GetReplayGameRoute.NOT_REPLAY;
import static com.webcheckers.util.Message.info;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A test for rendering the game.ftl for the spectator.
 *
 * @author Austin Miller 'akm8654'
 * @author Mikayla Wishart
 */
@Tag("UI-tier")
public class GetSpectatorGameRouteTest
{
  private GetSpectatorGameRoute CuT;

  private GameManager manager;
  private Player player1;
  private Player player2;
  private ReplayManager rManager;
  TemplateEngineTest testHelper;

  private TemplateEngine engine;
  private CheckerGame game;
  private PlayerLobby lobby;
  private Session session;
  private Request request;
  private Response response;
  private Board board;

  @BeforeEach
  public void setUp()
  {
    rManager = new ReplayManager();
    player1 = new Player(lobby);
    player1.setUsername("player1");
    player2 = new Player(lobby);
    player2.setUsername("player2");
    board = new Board();
    game = new CheckerGame(player1, player2, board);

    testHelper = new TemplateEngineTest();
    engine = mock(TemplateEngine.class);
    manager = mock(GameManager.class);
    session = mock(Session.class);
    lobby = mock(PlayerLobby.class);
    request = mock(Request.class);
    response = mock(Response.class);
    when(request.session()).thenReturn(session);
    when(request.queryParams("watchGameRequest")).thenReturn(player1.getUsername());

    CuT = new GetSpectatorGameRoute(engine);
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

  @Test
  public void testSpectator()
  {
    int gameID = manager.getGameID(player1.getUsername());

    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);
    when(manager.getGame(gameID)).thenReturn(game);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player1);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGameOverStatus(gameID)).thenReturn("No");

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);
    assertNotNull(player1);
    testHelper.assertViewModelExists();
    testHelper.assertViewModelAttribute(VIEWERS, 0);
    testHelper.assertViewModelAttribute(NOT_REPLAY, true);
  }

  /**
   * Tests when the game has the white players turn and the theme is selected.
   */
  @Test
  public void test_white_turn_theme()
  {
    when(session.attribute("theme")).thenReturn("True");

    int gameID = manager.getGameID(player1.getUsername());

    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);
    when(manager.getGame(gameID)).thenReturn(game);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player1);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGameOverStatus(gameID)).thenReturn("No");
    game.updateTurn();

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelAttribute("theme", true);
    testHelper.assertViewModelAttribute(ACTIVE_COLOR, Piece.Color.WHITE);
    testHelper.assertViewModelAttribute(VIEW_MODE, SPECTATOR);
  }

  /**
   * Tests what happens when the game has ended.
   */
  @Test
  public void game_over()
  {
    when(session.attribute("theme")).thenReturn("True");

    int gameID = manager.getGameID(player1.getUsername());

    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);
    when(manager.getGame(gameID)).thenReturn(game);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player1);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGameOverStatus(gameID)).thenReturn("Austin has resigned.");
    game.updateTurn();

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelAttribute("theme", true);
    testHelper.assertViewModelAttribute(ACTIVE_COLOR, Piece.Color.WHITE);
    testHelper.assertViewModelAttribute(VIEW_MODE, SPECTATOR);
  }
}
