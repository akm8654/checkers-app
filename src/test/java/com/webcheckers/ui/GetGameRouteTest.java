package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Board;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Piece;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import spark.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static com.webcheckers.ui.GetGameRoute.ACTIVE_COLOR;
import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static com.webcheckers.ui.GetHomeRoute.PLAYER_KEY;
import static com.webcheckers.ui.GetReplayGameRoute.MODE_OPTIONS_AS_JSON;
import static com.webcheckers.ui.PostRequestResponseRoute.NO_MORE_MOVES;
import static com.webcheckers.ui.PostSubmitTurnRouteTest.INITIAL_BOARD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@Tag("UI-tier")
public class GetGameRouteTest
{
  /**
   * The unit test for the {@link GetGameRoute}
   *
   * @author Zehra Amena Baig 'zab1166'
   * @author Mikayla Wishart 'mcw7246'
   */

  private static final String RED_WINS_BOARD = "src/test/java/com/webcheckers" +
          "/test-boards/NoMoreMovesRed.JSON";

  private static final String USERNAME = "username";
  private static final int gameID = 69;
  Gson gson = new Gson();
  private String RED_PLAYER = "redPlayer";
  //The component under test (CuT)
  private GetGameRoute CuT;
  //Friendly objects
  private PlayerLobby lobby;
  private GameManager manager;
  private Player player1, player2;
  private ReplayManager rManager;
  private Board board;
  TemplateEngineTest testHelper;

  //Mock objects
  private CheckerGame game;
  private Request request;
  private Session session;
  private Response response;
  private TemplateEngine engine;

  /**
   * Setup necessary mock objects and associations for testing
   */
  @BeforeEach
  public void setup()
  {
    testHelper = new TemplateEngineTest();
    lobby = new PlayerLobby();
    rManager = new ReplayManager();
    manager = new GameManager(lobby, rManager);
    player1 = new Player(lobby);
    player1.setUsername("player1");
    player2 = new Player(lobby);
    player2.setUsername("player2");

    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    response = mock(Response.class);
    engine = mock(TemplateEngine.class);
    board = mock(Board.class);

    CuT = new GetGameRoute(engine);
  }

  /**
   * Ensure that the game is retrieved for player one.
   */
  @Test
  public void playerOneTest()
  {
    lobby.newPlayer(player1);
    lobby.newPlayer(player2);
    manager.startGame(player1.getUsername(), player2.getUsername());

    when(request.session()).thenReturn(session);
    when(session.attribute(PLAYER_KEY)).thenReturn(player1);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);

    TemplateEngineTest testHelper = new TemplateEngineTest();
    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

    CuT.handle(request, response);
    testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, player1);
  }

  /**
   * Ensure that the game is retrieved for player 2.
   */
  @Test
  public void playerTwoTest()
  {
    lobby.newPlayer(player1);
    lobby.newPlayer(player2);
    manager.startGame(player1.getUsername(), player2.getUsername());

    when(request.session()).thenReturn(session);
    when(session.attribute(PLAYER_KEY)).thenReturn(player2);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);

    TemplateEngineTest testHelper = new TemplateEngineTest();
    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

    CuT.handle(request, response);
    testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER, player1);
  }

  /**
   * Ensure that if the player object is null, that the user is redirected home.
   */
  @Test
  public void nullPlayerTest()
  {
    lobby.newPlayer(player1);
    lobby.newPlayer(player2);
    manager.startGame(player1.getUsername(), player2.getUsername());

    when(request.session()).thenReturn(session);
    when(session.attribute(PLAYER_KEY)).thenReturn(null);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);

    TemplateEngineTest testHelper = new TemplateEngineTest();
    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

    assertEquals("Home Redirect", CuT.handle(request, response));
  }

  /**
   * Ensure that if the checker game object is null, that the user is redirected
   * to the homepage,
   */
  @Test
  public void null_game_test()
  {
    when(request.session()).thenReturn(session);
    when(session.attribute(PLAYER_KEY)).thenReturn(player1);
    GameManager MANAGER = mock(GameManager.class);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(MANAGER);
    when(MANAGER.getGame(any(Integer.class))).thenReturn(null);

    assertNull(CuT.handle(request, response));
  }

  /**
   * Test if red won doing something random
   */
  @Test
  public void test_red_won()
  {
    try
    {
      board = gson.fromJson(new FileReader(RED_WINS_BOARD), Board.class);
    } catch (FileNotFoundException e)
    {
      fail("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      board = new Board();
    }

    GameManager manager = mock(GameManager.class);
    game = new CheckerGame(player1, player2, board);

    int redPieces = game.getNumRedPieces();
    when(session.attribute(PLAYER_KEY)).thenReturn(player1);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGame(gameID)).thenReturn(game);
    when(manager.getOpponent(player1.getUsername())).thenReturn(player2);
    assertEquals(0, redPieces);


    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    //now go as other player

  }

  /**
   * Jump the last white piece as a red piece.
   */
  @Test
  public void jump_last_red()
  {
    GameManager manager = mock(GameManager.class);
    game = mock(CheckerGame.class);
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);
    when(session.attribute(PLAYER_KEY)).thenReturn(player1);
    when(manager.getGame(gameID)).thenReturn(game);
    when(manager.getOpponent(player1.getUsername())).thenReturn(player2);

    try
    {
      board = gson.fromJson(new FileReader(INITIAL_BOARD), Board.class);
    } catch (FileNotFoundException e)
    {
      fail("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      board = new Board();
    }

    when(game.getColor()).thenReturn(Piece.Color.RED);
    when(game.getWhitePlayer()).thenReturn(player2);
    when(game.getRedPlayer()).thenReturn(player1);
    when(game.getBoard()).thenReturn(board);

    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);
    when(session.attribute("theme")).thenReturn("pink");

    when(game.getNumRedPieces()).thenReturn(4);
    when(game.getNumWhitePieces()).thenReturn(0);

    player1.hasEnteredGame();

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelExists();
    testHelper.assertViewModelAttribute(ACTIVE_COLOR, Piece.Color.RED);
    final Map<String, Object> modeOptions = new HashMap<>(2);
    String gameOverMessage = player2.getUsername() + "'s pieces " +
            "were all taken! You win!";
    modeOptions.put("isGameOver", true);
    modeOptions.put("gameOverMessage", gameOverMessage);

    testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
  }

  /**
   * Jump the last white piece
   */
  @Test
  public void jump_last_white()
  {
    GameManager manager = mock(GameManager.class);
    game = mock(CheckerGame.class);
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);
    when(session.attribute(PLAYER_KEY)).thenReturn(player1);
    when(manager.getGame(gameID)).thenReturn(game);
    when(manager.getOpponent(player1.getUsername())).thenReturn(player2);

    try
    {
      board = gson.fromJson(new FileReader(INITIAL_BOARD), Board.class);
    } catch (FileNotFoundException e)
    {
      fail("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      board = new Board();
    }

    when(game.getColor()).thenReturn(Piece.Color.WHITE);
    when(game.getWhitePlayer()).thenReturn(player2);
    when(game.getRedPlayer()).thenReturn(player1);
    when(game.getBoard()).thenReturn(board);

    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);

    when(game.getNumRedPieces()).thenReturn(0);
    when(game.getNumWhitePieces()).thenReturn(4);

    player1.hasEnteredGame();

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelExists();
    testHelper.assertViewModelAttribute(ACTIVE_COLOR, Piece.Color.WHITE);
    final Map<String, Object> modeOptions = new HashMap<>(2);
    String gameOverMessage = player1.getUsername() + ", Your pieces were all " +
            "taken! You lose!";
    modeOptions.put("isGameOver", true);
    modeOptions.put("gameOverMessage", gameOverMessage);
    testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
  }

  /**
   * Test for no moves left for the opponent
   */
  @Test
  public void test_no_more_moves()
  {
    GameManager manager = mock(GameManager.class);
    game = mock(CheckerGame.class);
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);
    when(session.attribute(PLAYER_KEY)).thenReturn(player1);
    when(manager.getGame(gameID)).thenReturn(game);
    when(manager.getOpponent(player1.getUsername())).thenReturn(player2);

    try
    {
      board = gson.fromJson(new FileReader(NO_MORE_MOVES), Board.class);
    } catch (FileNotFoundException e)
    {
      fail("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      board = new Board();
    }

    when(game.getColor()).thenReturn(Piece.Color.WHITE);
    when(game.getWhitePlayer()).thenReturn(player2);
    when(game.getRedPlayer()).thenReturn(player1);
    when(game.getBoard()).thenReturn(board);

    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);

    when(game.getNumRedPieces()).thenReturn(3);
    when(game.getNumWhitePieces()).thenReturn(4);

    player1.hasEnteredGame();

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelExists();
    testHelper.assertViewModelAttribute(ACTIVE_COLOR, Piece.Color.WHITE);
    final Map<String, Object> modeOptions = new HashMap<>(2);
    String gameOverMessage = player2.getUsername() + " cannot move! You win!";
    modeOptions.put("isGameOver", true);
    modeOptions.put("gameOverMessage", gameOverMessage);
    testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
  }

  /**
   * Creates tests for no moves being left for the current user.
   */
  @Test
  public void test_no_more_moves_white()
  {
    GameManager manager = mock(GameManager.class);
    game = mock(CheckerGame.class);
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGameID(player2.getUsername())).thenReturn(gameID);
    when(session.attribute(PLAYER_KEY)).thenReturn(player2);
    when(manager.getGame(gameID)).thenReturn(game);
    when(manager.getOpponent(player2.getUsername())).thenReturn(player1);

    try
    {
      board = gson.fromJson(new FileReader(NO_MORE_MOVES), Board.class);
    } catch (FileNotFoundException e)
    {
      fail("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      board = new Board();
    }

    when(game.getColor()).thenReturn(Piece.Color.RED);
    when(game.getWhitePlayer()).thenReturn(player2);
    when(game.getRedPlayer()).thenReturn(player1);
    when(game.getBoard()).thenReturn(board);

    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);

    when(game.getNumRedPieces()).thenReturn(3);
    when(game.getNumWhitePieces()).thenReturn(4);

    player1.hasEnteredGame();

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelExists();
    testHelper.assertViewModelAttribute(ACTIVE_COLOR, Piece.Color.RED);
    final Map<String, Object> modeOptions = new HashMap<>(2);
    String gameOverMessage = player1.getUsername() + " has stopped you from " +
            "moving! You've lost!";
    modeOptions.put("isGameOver", true);
    modeOptions.put("gameOverMessage", gameOverMessage);
    testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
  }

  /**
   * Produces a test for both users resigning (one at a time though)
   */
  @Test
  public void test_resign()
  {
    GameManager manager = mock(GameManager.class);
    game = mock(CheckerGame.class);
    when(session.attribute(GAME_MANAGER_KEY)).thenReturn(manager);
    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);
    when(session.attribute(PLAYER_KEY)).thenReturn(player1);
    when(manager.getGame(gameID)).thenReturn(game);
    when(manager.getOpponent(player1.getUsername())).thenReturn(player2);

    try
    {
      board = gson.fromJson(new FileReader(INITIAL_BOARD), Board.class);
    } catch (FileNotFoundException e)
    {
      fail("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      board = new Board();
    }

    when(game.getColor()).thenReturn(Piece.Color.WHITE);
    when(game.getWhitePlayer()).thenReturn(player2);
    when(game.getRedPlayer()).thenReturn(player1);
    when(game.getBoard()).thenReturn(board);

    when(manager.getGameID(player1.getUsername())).thenReturn(gameID);

    when(game.getNumRedPieces()).thenReturn(4);
    when(game.getNumWhitePieces()).thenReturn(4);

    when(manager.getGameOverStatus(gameID)).thenReturn(player1.getUsername() +
            " has resigned.");
    player1.hasEnteredGame();

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelExists();
    testHelper.assertViewModelAttribute(ACTIVE_COLOR, Piece.Color.WHITE);
    Map<String, Object> modeOptions = new HashMap<>(2);
    String gameOverMessage = player1.getUsername() + " has resigned.";
    modeOptions.put("isGameOver", true);
    modeOptions.put("gameOverMessage", gameOverMessage);
    testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));

    when(manager.getGameOverStatus(gameID)).thenReturn(player2.getUsername() +
            " has resigned.");

    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
    CuT.handle(request, response);

    testHelper.assertViewModelExists();
    modeOptions = new HashMap<>(2);
    gameOverMessage = player2.getUsername() + " has resigned.";
    modeOptions.put("isGameOver", true);
    modeOptions.put("gameOverMessage", gameOverMessage);
    testHelper.assertViewModelAttribute("modeOptionsAsJSON", gson.toJson(modeOptions));
  }
}