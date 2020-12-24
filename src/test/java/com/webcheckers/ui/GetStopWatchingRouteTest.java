package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.TemplateEngine;

import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the spectator to stop watching the game in spectator view
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class GetStopWatchingRouteTest
{
  private GetStopWatchingRoute CuT;

  //Friendly classes
  Gson gson = new Gson();
  TemplateEngineTest testHelper;

  //Mocked Objects
  Player watcher;
  private Request request;
  private Session session;
  private Response response;
  TemplateEngine engine;
  GameManager manager;

  @BeforeEach
  public void setup()
  {
    watcher = mock(Player.class);

    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    engine = mock(TemplateEngine.class);
    testHelper = new TemplateEngineTest();
    manager = mock(GameManager.class);

    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(watcher);

    CuT = new GetStopWatchingRoute();
  }

  /**
   * Tests when there is a spectator.
   */
  @Test
  public void spectator_exists()
  {
    assertEquals("Redirected Home", CuT.handle(request, response));
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
}
