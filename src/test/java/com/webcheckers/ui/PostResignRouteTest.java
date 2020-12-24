package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import static com.webcheckers.util.Message.error;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test for the {@link PostRequestResponseRoute}
 *
 * @author Sean Bergen 'sdb2139'
 */
public class PostResignRouteTest
{
  private static final String PLAYER1 = "player1Username";
  private static int gameID = 69;

  private static final String VALID_RESIGN = "{\"text\":\"Resign Successful\",\"type\":\"INFO\"}";

  private Request request;
  private Session session;
  private Response response;

  private GameManager manager;

  private Session badSession;

  private Gson gson = new Gson();

  /**
   * the component under testing
   */
  private PostResignRoute CuT;

  @BeforeEach
  public void setup()
  {
    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    Player player1 = mock(Player.class);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player1);
    manager = mock(GameManager.class);
    when(manager.getGameOverStatus(gameID)).thenReturn("No");
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(player1.getUsername()).thenReturn(PLAYER1);
    when(manager.getGameID(PLAYER1)).thenReturn(gameID);
    this.CuT = new PostResignRoute();
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
   * Tests if the constructor works
   */
  @Test
  public void ctor_withArg()
  {
    CuT = new PostResignRoute();
  }


  /**
   * Test for the handle method for resign routes
   * Tests that the return from handle is a valid resign message
   */
  @Test
  public void testHandle()
  {
    try
    {
      //System.out.println(CuT.handle(request,response).toString());
      //System.out.println(VALID_RESIGN);
      assertEquals(CuT.handle(request, response).toString(), VALID_RESIGN);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Tests that the valid resign message will not be given
   * if the player is null
   */
  @Test
  public void testNoPlayer()
  {
    Request badRequest = mock(Request.class);
    when(badRequest.session()).thenReturn(badSession);
    badSession = mock(Session.class);
    when(badSession.attribute(PLAYER1)).thenReturn(null);
    try
    {
      assertNotEquals(CuT.handle(badRequest, response), VALID_RESIGN);
    } catch (Exception e)
    {
      //
    }
  }

  /**
   * Tests if the opponent has already resigned.
   */
  @Test
  public void alreadyResigned()
  {
    when(manager.getGameOverStatus(gameID)).thenReturn("Resign Happened.");
    assertEquals(gson.toJson(error("You can't resign: Resign Happened. " +
            "\nRefresh page to update!")), CuT.handle(request, response));
  }
}
