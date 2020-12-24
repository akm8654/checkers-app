package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import java.util.ArrayList;
import java.util.List;

import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The test for the replay archive page. Rendering the archive as it goes.
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class GetReplayRouteTest
{
  private String PLAYER1 = "player1";
  private String RED_PLAYER = "redPlayer";
  private String WHITE_PLAYER = "whitePlayer";

  GetReplayRoute CuT;

  //Friendly classes
  Gson gson = new Gson();
  TemplateEngineTest testHelper;

  //Mocked objects
  CheckerGame game1;
  CheckerGame game2;
  GameManager manager;
  ReplayManager rManager;
  private Request request;
  private Session session;
  private Response response;
  TemplateEngine engine;
  Player watcher;
  Player redPlayer;
  Player whitePlayer;

  @BeforeEach
  public void setup()
  {
    watcher = mock(Player.class);
    redPlayer = mock(Player.class);
    whitePlayer = mock(Player.class);

    game1 = mock(CheckerGame.class);
    game2 = mock(CheckerGame.class);

    when(watcher.getWinPercentage()).thenReturn(50.0);
    when(redPlayer.getWinPercentage()).thenReturn(25.0);
    when(whitePlayer.getWinPercentage()).thenReturn(75.0);

    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    engine = mock(TemplateEngine.class);
    testHelper = new TemplateEngineTest();
    rManager = mock(ReplayManager.class);
    manager = mock(GameManager.class);

    when(watcher.isInGame()).thenReturn(false);
    when(watcher.getUsername()).thenReturn(PLAYER1);
    when(redPlayer.getUsername()).thenReturn(RED_PLAYER);
    when(whitePlayer.getUsername()).thenReturn(WHITE_PLAYER);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(watcher);
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    CuT = new GetReplayRoute(engine, rManager);
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
   * Tests the redirect if the player is in a game.
   */
  @Test
  public void inGame()
  {
    when(watcher.isInGame()).thenReturn(true);
    assertEquals("Game Redirect", CuT.handle(request, response));
  }

  /**
   * Testing with no games
   */
  @Test
  public void browse_empty()
  {
    List<Integer> gameIDs = new ArrayList<>();
    when(rManager.getGameOverIDs()).thenReturn(gameIDs);

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);
    testHelper.assertViewModelExists();
  }

  /**
   * Games exists.
   */
  @Test
  public void game_exists()
  {
    List<Integer> gameIDs = new ArrayList<>();
    gameIDs.add(1);
    gameIDs.add(2);
    when(game1.winningAverage()).thenReturn(50.0);
    when(game2.winningAverage()).thenReturn(62.5);
    when(manager.getGame(1)).thenReturn(game1);
    when(manager.getGame(2)).thenReturn(game2);
    when(rManager.getGameOverIDs()).thenReturn(gameIDs);

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelExists();
  }
}
