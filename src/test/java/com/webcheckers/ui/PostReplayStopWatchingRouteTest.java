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

import static com.webcheckers.util.Message.info;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The test for when a user wishes to exit the replay. This mocks the replay
 * manager as it is easier than implementing it.
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class PostReplayStopWatchingRouteTest
{
  private static String username = "player1";

  //Component under Test
  PostReplayStopWatchingRoute CuT;

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
    CuT = new PostReplayStopWatchingRoute(rManager);
  }

  @Test
  public void test_Stop()
  {
    assertEquals("Redirect Home", CuT.handle(request, response));
  }

  /**
   * Tests that when there is no player that it is redirected home.
   */
  @Test
  public void noPlayer()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(null);
    assertEquals("Redirect Home", CuT.handle(request, response));
  }
}
