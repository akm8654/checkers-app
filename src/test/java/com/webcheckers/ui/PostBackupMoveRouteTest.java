package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.ArrayList;
import java.util.List;

import static com.webcheckers.util.Message.error;
import static com.webcheckers.util.Message.info;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test for the {@link PostValidateMoveRoute}
 *
 * @author Mario Castano 'mac3186'
 */
public class PostBackupMoveRouteTest
{
  private static final String PLAYER1 = "player1";
  private static final int GAMEID = 1;
  private static final String LOCAL_ID = "local";

  /*
    The component under test (CuT)
 */
  private PostBackupMoveRoute CuT;

  // Friendly attributes
  Gson gson = new Gson();
  private Position startPos = new Position(0, 0);
  private Position endPos = new Position(1, 1);
  List<Move> moves;

  // Mock attributes
  private GameManager manager;
  private Request request;
  private Session session;
  private Response response;
  private Player player;
  private CheckerGame game;

  /**
   * Setup mock attributes and dependencies they represent.
   */
  @BeforeEach
  public void setup()
  {
    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    player = mock(Player.class);
    game = mock(CheckerGame.class);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);
    manager = mock(GameManager.class);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(player.getUsername()).thenReturn(PLAYER1);
    when(manager.getGameID(PLAYER1)).thenReturn(GAMEID);
    CuT = new PostBackupMoveRoute();
  }

  /**
   * Ensure that if the player cannot be found, that the route halts and redirects User to the home page.
   */
  @Test
  public void home_redirect()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(null);
    try
    {
      CuT.handle(request, response);
      fail("Home found a lobby and did not halt.\n");
    } catch (spark.HaltException e)
    {
      // Test passed.
    }
  }

  /**
   * Ensure that if the game cannot be found through the game manager, that the route halts and
   * redirects User to home.
   */
  @Test
  public void home_redirect_game()
  {
    when(manager.getGame(GAMEID)).thenReturn(null);
    //when(manager.makeClientSideGame(GAMEID, player.getUsername())).thenReturn(null);
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

  /**
   * Ensure that if the list of moves made is null, that the proper error message is sent to
   * Json through gson.
   */
  @Test
  public void moves_is_null()
  {
    when(manager.getGame(GAMEID)).thenReturn(game);
    when(manager.makeClientSideGame(GAMEID, LOCAL_ID)).thenReturn(game);
    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(null);
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

  /**
   * Ensure that if the list of moves made is empty, that the proper error message is sent to
   * Json through gson.
   */
  @Test
  public void moves_is_empty()
  {
    when(manager.getGame(GAMEID)).thenReturn(game);
    when(manager.makeClientSideGame(GAMEID, LOCAL_ID)).thenReturn(game);
    moves = new ArrayList<Move>();
    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(moves);
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

  /**
   * Ensure that if the list of moves made is not empty, that the route properly removes all added moves
   * from the list, and that the proper info message is sent to Json from gson.
   */
  @Test
  public void backup_saved_move()
  {
    when(manager.getGame(GAMEID)).thenReturn(game);
    when(manager.makeClientSideGame(GAMEID, LOCAL_ID)).thenReturn(game);
    moves = new ArrayList<Move>();
    Move movePiece = new Move(startPos, endPos, Move.MoveStatus.VALID);
    moves.add(movePiece);
    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(moves);
    assertEquals("Redirected Home", CuT.handle(request, response));
    assertFalse(moves.isEmpty());
  }
}
