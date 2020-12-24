package com.webcheckers.ui;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static com.webcheckers.ui.PostChangeThemeRoute.THEME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A unit test for changing the theme.
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class PostChangeThemeRouteTest
{
  private static String username = "player1";

  private PostChangeThemeRoute CuT;

  //Mocked Objects
  private Request request;
  private Session session;
  private Response response;
  private Player player;
  private GameManager manager;
  private ReplayManager rManager;

  @BeforeEach
  public void setup()
  {
    player = mock(Player.class);
    when(player.getUsername()).thenReturn(username);

    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    manager = mock(GameManager.class);
    rManager = mock(ReplayManager.class);
    when(player.isInGame()).thenReturn(false);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);

    //defaults to get to the bottom.
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.isSpectator(username)).thenReturn(false);
    when(player.inArchive()).thenReturn(false);
    when(rManager.isWatching(username)).thenReturn(false);

    CuT = new PostChangeThemeRoute(rManager);
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
  public void in_game_theme_null()
  {
    when(player.isInGame()).thenReturn(true);
    assertEquals("Redirected Game", CuT.handle(request, response));
  }

  @Test
  public void spectating_theme_not_null()
  {
    when(session.attribute(THEME)).thenReturn("pink");
    when(manager.isSpectator(username)).thenReturn(true);
    assertEquals("Redirected Spectator", CuT.handle(request, response));
  }

  @Test
  public void replay_archive()
  {
    when(player.inArchive()).thenReturn(true);
    assertEquals("Redirected Replay Archive", CuT.handle(request, response));
  }

  @Test
  public void replay_game()
  {
    when(rManager.isWatching(username)).thenReturn(true);
    assertEquals("Redirected Replay", CuT.handle(request, response));
  }

  @Test
  public void all_default()
  {
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

}
