package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import static com.webcheckers.ui.PostValidateMoveRoute.ACTION_DATA;
import static com.webcheckers.util.Message.error;
import static com.webcheckers.util.Message.info;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test for the {@link PostValidateMoveRoute}
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class PostValidateMoveRouteTest
{
  private static final String PLAYER1 = "player1";
  private static final int GAMEID = 1;
  private static final String INVALID_SPACE = "Invalid Move: Space is the " +
          "wrong color!";
  private static final String ALREADY_MOVED = "Already Moved: Already moved a" +
          " piece!";
  private static final String VALID = "Valid Move! Click submit to send";
  private static final String JUMP = "Jump Move! Click submit to send.";
  private static final String OCCUPIED = "Invalid Move: Space is already " +
          "Occupied!";
  private static final String TOO_FAR = "Invalid Move: Space is too far away!";
  private static final String SAME_SPACE = "Invalid Move: The Space you've " +
          "tried to move to is the same one!";
  private static final String INVALID_BACKWARDS = "Invalid Move: You aren't " +
          "kinged yet! You must move forward!";
  private static final String JUMP_OWN = "Invalid Move: You're attempting to " +
          "jump your own piece!";
  private static final int START_ROW = 5;
  private static final int START_COL = 0;
  private static final Piece.Color color = Piece.Color.RED;
  private static final Piece.Color oppColor = Piece.Color.WHITE;

  /*
  The component under test (CuT)
   */
  private PostValidateMoveRoute CuT;

  //friendly objects
  private int endRow;
  private int endCol;
  private int startRow;
  private int startCol;
  private Space startSpace;
  private Space endSpace;
  Gson gson = new Gson();

  // attributes holding mock objects
  private GameManager manager;
  private Request request;
  private Session session;
  private Response response;
  private Player player;
  private CheckerGame game;
  private Board board;

  @BeforeEach
  public void setup()
  {
    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    player = mock(Player.class);
    String MOVE_STR = "{\"start\":{\"row\":5,\"cell\":0}," +
            "\"end\":{\"row\":4,\"cell\":1}}";
    game = mock(CheckerGame.class);
    board = mock(Board.class);
    when(request.queryParams(ACTION_DATA)).thenReturn(MOVE_STR);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);
    manager = mock(GameManager.class);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(player.getUsername()).thenReturn(PLAYER1);
    when(manager.getGameID(PLAYER1)).thenReturn(GAMEID);
    when(manager.getLocalGame(PLAYER1)).thenReturn(game);
    Space startSpace = new Space(START_ROW, START_COL, true,
            new Piece(color));
    when(game.getBoard()).thenReturn(board);
    when(board.getSpaceAt(START_ROW, START_COL)).thenReturn(startSpace);
    endSpace = new Space(4, 1, true);
    CuT = new PostValidateMoveRoute();
  }

  /**
   * Updates the MoveStr to a new JSON as needed.
   *
   * @param endRow the row to be changed
   * @param endCol the col to be changed.
   */
  public void changeMoveStr(int endRow, int endCol)
  {
    String change = "{\"start\":{\"row\":5,\"cell\":0}," +
            "\"end\":{\"row\":" + endRow + ",\"cell\":" + endCol + " }}";
    when(request.queryParams(ACTION_DATA)).thenReturn(change);
  }

  @Test
  public void home_redirect()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(null);
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

  @Test
  public void home_redirect_local_game()
  {
    when(manager.getLocalGame(player.getUsername())).thenReturn(null);
    when(manager.makeClientSideGame(GAMEID, player.getUsername())).thenReturn(null);
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

  @Test
  public void make_Game_and_Valid()
  {

    when(manager.getLocalGame(player.getUsername())).thenReturn(null);
    when(manager.makeClientSideGame(GAMEID, player.getUsername())).thenReturn(game);
    System.out.println(game);
    endRow = 4;
    endCol = 1;
    endSpace = new Space(endRow, endCol, true);
    when(board.getSpaceAt(endRow, endCol)).thenReturn(endSpace);
    assertEquals(gson.toJson(info(VALID)), CuT.handle(request,response));
  }

  @Test
  public void already_moved()
  {
    when(game.hasMoved()).thenReturn(true);
    endRow = 4;
    endCol = 1;
    endSpace = new Space(endRow, endCol, true);
    when(board.getSpaceAt(endRow, endCol)).thenReturn(endSpace);
    assertEquals(gson.toJson(error(ALREADY_MOVED)), CuT.handle(request,
            response));
  }

  @Test
  public void invalid_Space()
  {
    endRow = 4;
    endCol = 1;
    endSpace = new Space(endRow, endCol, false);
    when(board.getSpaceAt(endRow, endCol)).thenReturn(endSpace);
    assertEquals(gson.toJson(error(INVALID_SPACE)), CuT.handle(request,
            response));
  }

  @Test
  public void jump_no_king()
  {
    endRow = 3;
    endCol = 2;
    endSpace = new Space(endRow, endCol, true);
    changeMoveStr(endRow, endCol);
    Space jumpSpace = new
            Space(endRow + 1, endCol - 1, true);
    when(board.getSpaceAt(endRow, endCol)).thenReturn(endSpace);
    when(board.getSpaceAt(endRow + 1, endCol - 1)).thenReturn(jumpSpace);
    //TOO FAR
    assertEquals(gson.toJson(error(TOO_FAR)), CuT.handle(request, response));
    //ADD PIECE OWN
    jumpSpace.setPiece(new Piece(color));
    assertEquals(gson.toJson(error(JUMP_OWN)), CuT.handle(request, response));
    //ADD PIECE ENEMY
    jumpSpace.setPiece(new Piece(oppColor));
    assertEquals(gson.toJson(info(JUMP)), CuT.handle(request, response));
  }

  @Test
  public void occupied()
  {
    endRow = 4;
    endCol = 1;
    endSpace = new Space(endRow, endCol, true, new Piece(color));
    when(board.getSpaceAt(endRow, endCol)).thenReturn(endSpace);
    assertEquals(gson.toJson(error(OCCUPIED)), CuT.handle(request,
            response));
  }

  @Test
  public void same_space()
  {
    endRow = START_ROW;
    endCol = START_COL;
    endSpace = new Space(endRow, endCol, true, new Piece(color));
    changeMoveStr(endRow, endCol);
    when(board.getSpaceAt(endRow, endCol)).thenReturn(endSpace);
    assertEquals(gson.toJson(error(SAME_SPACE)), CuT.handle(request,
            response));
  }

  @Test
  public void invalid_backwards()
  {
    endRow = 6;
    endCol = 1;
    endSpace = new Space(endRow, endCol, true);
    changeMoveStr(endRow, endCol);
    when(board.getSpaceAt(endRow, endCol)).thenReturn(endSpace);
    assertEquals(gson.toJson(error(INVALID_BACKWARDS)), CuT.handle(request,
            response));
  }
}
