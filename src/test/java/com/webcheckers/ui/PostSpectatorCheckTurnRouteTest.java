package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static com.webcheckers.ui.GetSpectatorGameRoute.TURN;
import static com.webcheckers.util.Message.info;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the check turn route for the spectator enhancement
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class PostSpectatorCheckTurnRouteTest
{
  private static String username = "player1";
  private int gameID = 69;

  private PostSpectatorCheckTurnRoute CuT;

  //friendly objects
  Gson gson = new Gson();

  //Mocked Objects
  private Request request;
  private Session session;
  private Response response;
  private GameManager manager;
  private Player player;
  private CheckerGame game;

  @BeforeEach
  public void setup()
  {
    player = mock(Player.class);
    when(player.getUsername()).thenReturn(username);
    game = mock(CheckerGame.class);

    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    manager = mock(GameManager.class);
    when(player.isInGame()).thenReturn(false);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);

    when(manager.getGameID(username)).thenReturn(gameID);
    when(manager.getGame(gameID)).thenReturn(game);

    //defaults to get to the bottom.
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.isSpectator(username)).thenReturn(true);
    when(player.inArchive()).thenReturn(false);

    CuT = new PostSpectatorCheckTurnRoute();
  }

  /**
   * Tests that when there is no player that it is redirected home.
   */
  @Test
  public void noPlayer()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(null);
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

  @Test
  public void newTurn()
  {
    when(session.attribute(TURN)).thenReturn("turn2");
    when(game.getTurn()).thenReturn("turn");
    assertEquals(gson.toJson(info("true")), CuT.handle(request, response));
  }

  @Test
  public void sameTurn()
  {
    when(session.attribute(TURN)).thenReturn("turn");
    when(game.getTurn()).thenReturn("turn");
    assertEquals(gson.toJson(info("false")), CuT.handle(request, response));
  }
}
