package com.webcheckers.application;

import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * The unit test for the {@link ReplayManager}
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("Application-tier")
public class ReplayManagerTest
{
  private static String PLAYER1 = "player1";
  int gameID;

  //Mocked Classes
  Player player;
  CheckerGame game1;
  CheckerGame game2;
  CheckerGame game;


  private ReplayManager CuT;

  @BeforeEach
  public void setup()
  {
    player = mock(Player.class);
    gameID = 1;
    CuT = new ReplayManager();
    game = mock(CheckerGame.class);
    game1 = mock(CheckerGame.class);
    game2 = mock(CheckerGame.class);
  }

  /**
   * used to see if the replay starts the test
   */
  @Test
  public void startWatchingTest()
  {
    //No one is currently in.
    int move = CuT.getMove(PLAYER1, gameID);
    assertEquals(move, 0);
    assertTrue(CuT.isWatching(PLAYER1));
  }

  /**
   * Adds the move and ensures that the move isn't updated in the manager
   */
  @Test
  public void add_move_and_get()
  {
    int move = CuT.getMove(PLAYER1, gameID);
    assertEquals(move, 0);
    CuT.addMove(gameID, game1);
    move = CuT.getMove(PLAYER1, gameID);
    assertEquals(move, 0);
    assertEquals(game1, CuT.getGameAtMove(gameID, move));
  }

  /**
   * Stops a player from watching the test.
   */
  @Test
  public void stop_watching_test()
  {
    int move = CuT.getMove(PLAYER1, gameID);
    assertEquals(move, 0);
    assertTrue(CuT.isWatching(PLAYER1));
    CuT.stopWatching(PLAYER1);
    assertFalse(CuT.isWatching(PLAYER1));
  }

  /**
   * Determines if the maximum is returned correctly.
   */
  @Test
  public void max_moves()
  {
    int move = CuT.getMove(PLAYER1, gameID);
    assertEquals(move, 0);
    CuT.addMove(gameID, game1);
    CuT.addMove(gameID, game2);
    CuT.addMove(gameID, game);
    assertEquals(3, CuT.maxMoves(gameID));
  }

  /**
   * Tells the user to go to the next move
   */
  @Test
  public void next_and_previous_move()
  {
    int move = CuT.getMove(PLAYER1, gameID);
    assertEquals(move, 0);
    CuT.addMove(gameID, game1);
    CuT.addMove(gameID, game2);
    CuT.addMove(gameID, game);
    CuT.nextMove(PLAYER1, gameID);
    move = CuT.getMove(PLAYER1, gameID);
    assertEquals(move, 1);
    CuT.previousMove(PLAYER1, gameID);
    move = CuT.getMove(PLAYER1, gameID);
    assertEquals(move, 0);
  }

  /**
   * Tries to break the program and ensures it is caught
   */
  @Test
  public void bad_list_test()
  {
    assertNull(CuT.getGameAtMove(gameID, 0));
  }

  /**
   * Simulates an end of game called and that the ids are successfully casted
   * to a list.
   */
  @Test
  public void end_game_and_IDS()
  {
    assertTrue(CuT.getGameOverIDs().isEmpty());
    CuT.endGame(gameID);
    assertFalse(CuT.getGameOverIDs().isEmpty());
  }
}
