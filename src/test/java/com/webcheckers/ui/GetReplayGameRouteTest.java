package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Board;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import java.util.HashMap;
import java.util.Map;

import static com.webcheckers.ui.GetReplayGameRoute.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The GetReplayGameRouteTest, used to simulate actually entering the game
 * .ftl using the replay function
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class GetReplayGameRouteTest
{
  private String PLAYER1 = "player1";
  private String RED_PLAYER = "redPlayer";
  private String WHITE_PLAYER = "whitePlayer";
  private int GAME_ID = 1;
  private String GAME_ID_STR = "1";

  GetReplayGameRoute CuT;

  //Friendly classes
  Gson gson = new Gson();
  TemplateEngineTest testHelper;
  CheckerGame game;

  //Mocked Objects
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

    when(watcher.getUsername()).thenReturn(PLAYER1);
    game = new CheckerGame(redPlayer, whitePlayer, new Board());

    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    engine = mock(TemplateEngine.class);
    testHelper = new TemplateEngineTest();
    rManager = mock(ReplayManager.class);

    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(watcher);

    CuT = new GetReplayGameRoute(engine, rManager);
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
   * Tests as if it is the first move that is being loaded.
   */
  @Test
  public void first_request_and_theme()
  {
    when(session.attribute("theme")).thenReturn("Theme Here");
    when(request.queryParams("replayRequest")).thenReturn(GAME_ID_STR);
    when(rManager.getMove(PLAYER1, GAME_ID)).thenReturn(0);
    when(rManager.maxMoves(GAME_ID)).thenReturn(20);
    when(rManager.getGameAtMove(GAME_ID, 0)).thenReturn(game);

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    Map<String, Object> modeOptions = new HashMap<>();
    modeOptions.put(HAS_NEXT, true);
    modeOptions.put(HAS_PREVIOUS, false);
    testHelper.assertViewModelAttribute(MODE_OPTIONS_AS_JSON,
            gson.toJson(modeOptions));
    testHelper.assertViewModelAttribute("theme", true);
    testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, redPlayer);
    testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, whitePlayer);
  }

  /**
   * Tests for a request later in the game, with the white player, at the max
   * move.
   */
  @Test
  public void later_request_white_player_max()
  {
    when(session.attribute(GetReplayGameRoute.GAME_ID)).thenReturn(GAME_ID);
    game.updateTurn(); //set turn to white player
    when(rManager.getMove(PLAYER1, GAME_ID)).thenReturn(20);
    when(rManager.maxMoves(GAME_ID)).thenReturn(20);
    when(rManager.getGameAtMove(GAME_ID, 20)).thenReturn(game);

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    Map<String, Object> modeOptions = new HashMap<>();
    modeOptions.put(HAS_NEXT, false);
    modeOptions.put(HAS_PREVIOUS, true);
    testHelper.assertViewModelAttribute(MODE_OPTIONS_AS_JSON,
            gson.toJson(modeOptions));
    testHelper.assertViewModelAttribute("theme", false);
    testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, redPlayer);
    testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER, whitePlayer);
  }
}
