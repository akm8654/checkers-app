package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import static com.webcheckers.ui.GetReplayGameRoute.GAME_ID;
import static com.webcheckers.util.Message.info;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for going to the next turn during a replay.
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class PostReplayNextTurnRouteTest
{
  private static String username = "player1";
  private int gameID = 69;

  //Component under Test
  PostReplayNextTurnRoute CuT;

  //friendly objects
  Gson gson = new Gson();

  //Mocked objects
  private Request request;
  private Session session;
  private Response response;
  private ReplayManager rManager;
  private Player player;

  @BeforeEach
  public void setup()
  {
    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    rManager = mock(ReplayManager.class);

    player = mock(Player.class);
    when(player.getUsername()).thenReturn(username);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);
    when(session.attribute(GAME_ID)).thenReturn(gameID);

    CuT = new PostReplayNextTurnRoute(rManager);
  }

  /**
   * Tests that when there is no player that it is redirected home.
   */
  @Test
  public void noPlayer()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(null);
    assertEquals(gson.toJson(info("ERROR! Not signed in.")),
            CuT.handle(request, response));
  }

  /**
   * Tests when there is a player.
   */
  @Test
  public void withPlayer()
  {
    assertEquals(gson.toJson(info("true")), CuT.handle(request, response));
  }

}
