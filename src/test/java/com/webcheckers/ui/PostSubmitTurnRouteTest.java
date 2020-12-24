package com.webcheckers.ui;


import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.TemplateEngine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import static com.webcheckers.model.Piece.Color.RED;
import static com.webcheckers.model.Piece.Color.WHITE;
import static com.webcheckers.util.Message.error;
import static com.webcheckers.util.Message.info;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test for the {@link PostSubmitTurnRoute}
 *
 * @author Austin Miler 'akm8654'
 * @author Mikayla Wishart 'mcw7246'
 */
@Tag("UI-tier")
public class PostSubmitTurnRouteTest
{
  private static final int GAME_ID = 1;
  private static final String PLAYER1 = "player1";
  public static final String INITIAL_BOARD = "src/test/java/com/webcheckers" +
          "/test-boards/InitialBoard.JSON";
  private static final String ONE_MOVE = "src/test/java/com/webcheckers/test" +
          "-boards/OneMove.JSON";
  private static final String KING_JUMP = "src/test/java/com/webcheckers/test" +
          "-boards/multi-jump-to-king.JSON";
  private static final String KING_JUMP_WHITE = "src/test/java/com" +
          "/webcheckers/test" +
          "-boards/ToBeKingedMultiJumpWhite.JSON";
  private static final String REQUIRE_JUMP = "src/test/java/com/webcheckers" +
          "/test-boards/requireJumpBoard.JSON";

  private static final String NO_MORE_MOVES = "src/test/java/com/webcheckers" +
          "/test-boards/no-more-moves.JSON";
  private static final String MULTI_JUMP_REQUIRE = "src/test/java/com/webcheckers" +
          "/test-boards/multiJumpBoardStillJump.JSON";
  private static final String MULTI_JUMP_KING_REQUIRE = "src/test/java/com/webcheckers" +
          "/test-boards/multi-jump-to-king.JSON";

  private static final String TO_BE_KINGED_RED = "src/test/java/com/webcheckers" +
          "/test-boards/ToBeKingedRed.JSON";
  private static final String TO_BE_KINGED_WHITE = "src/test/java/com/webcheckers" +
          "/test-boards/ToBeKingWhite.JSON";
  private static final String MULTI_JUMP_REQUIRED_RED = "src/test/java/com/webcheckers" +
          "/test-boards/multiJumpRequired.JSON";
  private static final String ONE_JUMP_RED= "src/test/java/com/webcheckers" +
          "/test-boards/OneJumpMadeRed.JSON";
  private static final String KING_MULTI_JUMP_WHITE = "src/test/java/com/webcheckers" +
          "/test-boards/KingMultiJumpWhite.JSON";

  /**
   * The component-under-test (CuT)
   */
  private PostSubmitTurnRoute CuT;

  //friendly attributes
  private Space jumpSpace;
  private CheckerGame game;
  Gson gson = new Gson();

  //attributes holding mock objects
  private GameManager manager;
  private PlayerLobby lobby;
  private Request request;
  private Session session;
  private Response response;
  private TemplateEngine engine;
  private ReplayManager rManager;
  private Player player;
  private Player player2;
  private Board board;
  private RequireMove requireMove;

  @BeforeEach
  public void setup()
  {
    //game = new CheckerGame(player, player2, board);

    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    engine = mock(TemplateEngine.class);
    response = mock(Response.class);
    player = mock(Player.class);
    player2 = mock(Player.class);
    rManager = mock(ReplayManager.class);
    //board = mock(Board.class);
    when(player.getUsername()).thenReturn(PLAYER1);
    lobby = mock(PlayerLobby.class);
    manager = mock(GameManager.class);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);
    when(manager.getGameID(player.getUsername())).thenReturn(GAME_ID);
    //game = new CheckerGame(player, player2, board);
    requireMove = mock(RequireMove.class);
    CuT = new PostSubmitTurnRoute(rManager);
    //when(manager.getLocalGame(player.getUsername())).thenReturn(game);
  }

  @Test
  public void home_redirect_noGame()
  {
    when(manager.getLocalGame(player.getUsername())).thenReturn(null);
    when(manager.getGame(GAME_ID)).thenReturn(null);
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

  @Test
  public void home_redirect_noPlayer()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(null);
    String returned = "";
    assertEquals("Redirected Home", CuT.handle(request, response));
  }

  @Test
  void local_game_no_jump()
  {

    try
    {
      this.board = gson.fromJson(new FileReader(ONE_MOVE), Board.class);
      game = new CheckerGame(player, player2, board);
    }
    catch (FileNotFoundException e)
    {
      fail("Initial Board was not found from given path");
      board = new Board();
    }
    int gameID = manager.getGameID(player.getUsername());
    when(manager.getLocalGame(player.getUsername())).thenReturn(game);
    when(manager.getGameID(player.getUsername())).thenReturn(gameID);
    List<Move> moves = new ArrayList<>();
    moves.add(new Move(new Position(5, 0), new Position(4, 1),
            Move.MoveStatus.VALID));
    when(manager.getGame(gameID)).thenReturn(game);
    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(moves);
    when(manager.getLocalGame(player.getUsername())).thenReturn(game);

    assertEquals(CuT.handle(request, response),
            gson.toJson(info("Valid Move")));
  }

  /**
   * tests a jump being made
   */
  @Test
  void local_game_jump()
  {
    try
    {
      this.board = gson.fromJson(new FileReader(REQUIRE_JUMP), Board.class);
      game = new CheckerGame(player, player2, board);
    }
    catch (FileNotFoundException e)
    {
      fail("Initial Board was not found from given path");
    }
    CheckerGame game = new CheckerGame(player, player2, board);
    int gameID = manager.getGameID(player.getUsername());
    when(player.getPlayerNum()).thenReturn(1);
    when(manager.getLocalGame(player.getUsername())).thenReturn(game);
    when(manager.getGameID(player.getUsername())).thenReturn(gameID);
    when(manager.getGame(gameID)).thenReturn(game);
    List<Move> moves = new ArrayList<>();
    moves.add(new Move(new Position(4, 1), new Position(2, 3),
            Move.MoveStatus.JUMP));

    Space endSpace = new Space(2, 3, true, new Piece(RED));
    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(moves);
    game.makeMove(moves.get(0));
    when(manager.getLocalGame(player.getUsername())).thenReturn(game);
    assertEquals(gson.toJson(info("Valid Move")), CuT.handle(request,
            response));
  }

  /**
   * tests a king making a multijump
   */
  @Test
  public void test_king_multi_jump()
  {
    Board setBoard;
    CheckerGame game;
    try
    {
      setBoard = gson.fromJson(new FileReader(REQUIRE_JUMP), Board.class);
    }
    catch (FileNotFoundException e)
    {
      fail("Initial Board was not found from given path");
      setBoard = new Board();
    }
    game = new CheckerGame(player, player2, setBoard);
    game.updateTurn();
    int gameID = manager.getGameID(player2.getUsername());
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player2);
    when(manager.getLocalGame(player2.getUsername())).thenReturn(game);
    when(manager.getGame(gameID)).thenReturn(game);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getLocalGame(player.getUsername())).thenReturn(game);
    when(manager.getGame(gameID)).thenReturn(game);

    Piece redPiece = new Piece(WHITE, Piece.Type.KING);
    Piece piece = new Piece(RED, Piece.Type.SINGLE);
    Space jumpedSpace = setBoard.getSpaceAt(6, 1);
    Space start = setBoard.getSpaceAt(7, 0);
    start.setPiece(redPiece);
    jumpedSpace.setPiece(piece);

    List<Move> movesList = new ArrayList<>();
    Move moveMade = new Move(new Position(7, 0), new Position(5, 2));
    movesList.add(moveMade);
    game.makeMove(moveMade);
    game.addJumpedPieces(jumpedSpace);

    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(movesList);

    assertEquals(gson.toJson(error("There is still an available jump. You must make this move before you end your turn.")), CuT.handle(request, response));
  }

  /**
   * tests a red piece being made a king when needed
   */
  @Test
  public void red_king()
  {
    try
    {
      this.board = gson.fromJson(new FileReader(TO_BE_KINGED_RED), Board.class);
      game = new CheckerGame(player, player2, board);
    }
    catch (FileNotFoundException e)
    {
      fail("Initial Board was not found from given path");
    }

    int gameID = manager.getGameID(player.getUsername());
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGame(gameID)).thenReturn(game);

    Space start = board.getSpaceAt(1, 0);
    Space end = board.getSpaceAt(0,1);

    Move moveMade = new Move(new Position(1, 0), new Position(0, 1), Move.MoveStatus.VALID);
    List<Move> movesList = new ArrayList<>();

    moveMade.validateMove(game, start, end);

    movesList.add(moveMade);
    game.makeMove(moveMade);

    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(movesList);



    assertEquals(gson.toJson(info("Valid Move")), CuT.handle(request, response));
    assertEquals(Piece.Type.KING, board.getSpaceAt(0, 1).getPiece().getType());
  }

  /**
   * Tests a white piece being made a king when necessary
   */
  @Test
  public void white_king()
  {
    try
    {
      this.board = gson.fromJson(new FileReader(TO_BE_KINGED_WHITE), Board.class);
      game = new CheckerGame(player, player2, board);
    }
    catch (FileNotFoundException e)
    {
      fail("Initial Board was not found from given path");
    }


    int gameID = manager.getGameID(player.getUsername());
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGame(gameID)).thenReturn(game);

    game.updateTurn();

    Space start = board.getSpaceAt(6, 5);
    Space end = board.getSpaceAt(7, 6);

    Move moveMade = new Move(new Position(6, 5), new Position(7, 6), Move.MoveStatus.VALID);
    List<Move> movesList = new ArrayList<>();

    moveMade.validateMove(game, start, end);

    movesList.add(moveMade);

    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(movesList);


    game.makeMove(moveMade);

    assertEquals(gson.toJson(info("Valid Move")), CuT.handle(request, response));
    assertEquals(Piece.Type.KING, board.getSpaceAt(7, 6).getPiece().getType());
  }

  /**
   * tests if an opponent cannot move
   */
  @Test
  public void opponent_cannot_move()
  {
    Board gameBoard;
    try
    {
      gameBoard = gson.fromJson(new FileReader(NO_MORE_MOVES), Board.class);
    }
    catch (FileNotFoundException e)
    {
      fail("Initial Board was not found from given path");
      gameBoard = new Board();
    }

    CheckerGame newGame = new CheckerGame(player, player2, gameBoard);
    System.out.println(game);
    Piece piece = new Piece(RED, Piece.Type.SINGLE);

    List<Move> moves = new ArrayList<>();
    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(moves);
//
    CuT.handle(request, response);
   // assertEquals(gson.toJson("Redirected Home"), CuT.handle(request, response));

  }

  /**
   * tests if there is a required move but it was not made
   */
  @Test
  public void required_move_not_made()
  {
    Board notMockedBoard;
    try
    {
      notMockedBoard = gson.fromJson(new FileReader(KING_JUMP_WHITE), Board.class);
    }  catch (FileNotFoundException e) {
      fail("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      notMockedBoard = new Board();
    }
    CheckerGame newGame = new CheckerGame(player, player2, notMockedBoard);
    int gameID = manager.getGameID(player.getUsername());
    when(manager.getGameID(player.getUsername())).thenReturn(gameID);
    when(manager.getGame(gameID)).thenReturn(newGame);
    when(player.getPlayerNum()).thenReturn(2);

    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);

    List<Move> jumpMoves = new ArrayList<>();
    Map<Move.MoveStatus, List<Move>> requiredMoves = new HashMap<>();
    Piece whitePiece = new Piece(Piece.Color.WHITE, Piece.Type.SINGLE);
    Space jumpStart = new Space(0, 1, true, whitePiece);
    Space jumpEnd = new Space(2, 3, true);

    Stack<Move> validMoves = new Stack<>();
    Move requiredJumpMove = new Move(new Position(jumpStart.getRowIndex(), jumpStart.getColumnIndex()), new Position(jumpEnd.getRowIndex(), jumpEnd.getColumnIndex()));
    jumpMoves.add(requiredJumpMove);
    validMoves.push(requiredJumpMove);
    requiredMoves.put(Move.MoveStatus.JUMP, jumpMoves);

    Piece movedWhite = new Piece(Piece.Color.WHITE,Piece.Type.SINGLE);
    Space startSpace = new Space(4, 5, true, movedWhite);
    Space endSpace = new Space(5, 6, true);

    Move moveMade = new Move(new Position(startSpace.getRowIndex(), startSpace.getColumnIndex()), new Position(endSpace.getRowIndex(), endSpace.getColumnIndex()));

    List<Move> movesMadeList = new ArrayList<>();
    movesMadeList.add(moveMade);

    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(movesMadeList);
    when(requireMove.getAllMoves()).thenReturn(requiredMoves);

    assertEquals(gson.toJson(error("There is still an available jump. You must make this move before you end your turn.")), CuT.handle(request, response));

  }

  /**
   * tests a multijump that is required by a king
   */
  @Test
  public void king_multi_jump_required(){
    Board setBoard;
    try
    {
      setBoard = gson.fromJson(new FileReader(MULTI_JUMP_KING_REQUIRE), Board.class);
    }  catch (FileNotFoundException e) {
      fail("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      setBoard = new Board();
    }

    int gameID = manager.getGameID(player.getUsername());
    when(player.getPlayerNum()).thenReturn(1);

    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(player.getPlayerNum()).thenReturn(1);
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);
    CheckerGame newGame = new CheckerGame(player, player2, setBoard);
    when(manager.getGame(gameID)).thenReturn(newGame);
    RequireMove newRequireMove = new RequireMove(setBoard, RED);
    List<Move> listMoves = new ArrayList<>();
    Map<Move.MoveStatus, List<Move>> moves = newRequireMove.getAllMoves();
    System.out.println(moves.get(Move.MoveStatus.JUMP));
    Piece whitePiece = new Piece(Piece.Color.WHITE, Piece.Type.SINGLE);
    //Space jumpedSpace = new Space(4, 5, true, whitePiece);

    Move moveMade = new Move(new Position(0, 1), new Position(1, 0), Move.MoveStatus.VALID);
    listMoves.add(moveMade);
    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(listMoves);
    CuT.handle(request, response);
    assertEquals(gson.toJson(error("There is still an available jump. You must make this move before you end your turn.")), CuT.handle(request, response));

  }

  /**
   * tests to make sure the jumped space is null
   */
  @Test
  public void jump_space_null(){
    Board setBoard;
    try
    {
      setBoard = gson.fromJson(new FileReader(ONE_JUMP_RED), Board.class);
    }
    catch (FileNotFoundException e)
    {
      fail("Initial Board was not found from given path");
      setBoard = new Board();
    }
    CheckerGame game = new CheckerGame(player, player2, setBoard);
    int gameID = manager.getGameID(player.getUsername());
    when(player.getPlayerNum()).thenReturn(1);
    when(manager.getLocalGame(player.getUsername())).thenReturn(game);
    when(manager.getGameID(player.getUsername())).thenReturn(gameID);
    when(manager.getGame(gameID)).thenReturn(game);

    Piece jumpedPiece = new Piece(WHITE, Piece.Type.SINGLE);
    Space jumpedSpace = setBoard.getSpaceAt(6,3);
    jumpedSpace.setPiece(jumpedPiece);
    Move jumpMade = new Move(new Position(7,2), new Position(5, 4), Move.MoveStatus.JUMP);
    List<Move> movesList = new ArrayList<>();
    game.addJumpedPieces(jumpedSpace);
    movesList.add(jumpMade);
    game.makeMove(jumpMade);
    when(session.attribute(PostValidateMoveRoute.MOVE_LIST_ID)).thenReturn(movesList);

    CuT.handle(request, response);
    assertNull(setBoard.getSpaceAt(jumpedSpace.getRowIndex(), jumpedSpace.getColumnIndex()).getPiece());

  }
}


