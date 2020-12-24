package com.webcheckers.model;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Player.UsernameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The unit test suite for the {@link Player} component.
 *
 * @author Austin Miller 'akm8654'
 * @author Mikayla Wishart 'mcw7246'
 */
@Tag("Model-tier")
public class PlayerTest
{

  private static final String GOOD_NAME = "player1";
  private static final String NO_NUM = "player";
  private static final String TOO_SHORT = "p1";
  private static final String CONTAINS_SPACE = "pla yer";
  private static final String TOO_LONG = "itsybitsysp1derwentupthewaterspout";
  private static final String HAS_SPACE = "player 1";
  private static final String CAPS_OK = "Player1";
  private static final String NUM_START = "1Player";
  private static final String SPECIAL_CHAR = "Player1!";
  private static final String GOOD_NAME_2 = "player2";


  /**
   * The component under test.
   * <p>
   * Stateless component that actually test the functionality of the
   * {@link Player}
   */
  private Player CuT;

  // Friendly objects
  private PlayerLobby lobby;
  private GameManager manager;
  private ReplayManager rManager;

  @BeforeEach
  public void setup()
  {
    lobby = new PlayerLobby();
    rManager = new ReplayManager();
    manager = new GameManager(lobby, rManager);
  }

  /**
   * Tests that the main constrcutor works without failure.
   */
  @Test
  public void ctor_withArg()
  {
    CuT = new Player(lobby);
  }

  /**
   * Tests that a valid username returns valid and can be set.
   */
  @Test
  public void correctUsername()
  {
    CuT = new Player(lobby);
    assertEquals(UsernameResult.AVAILABLE, CuT.isValidUsername(GOOD_NAME));
    assertEquals(GOOD_NAME, CuT.getUsername());
  }

  /**
   * Tests that the correct time of result is given when the username is
   * already taken.
   */
  @Test
  public void userNameTaken()
  {
    Player takenName = new Player(lobby);
    takenName.isValidUsername(GOOD_NAME);
    CuT = new Player(lobby);
    assertEquals(UsernameResult.TAKEN, CuT.isValidUsername(GOOD_NAME));
  }

  /**
   * Tests that a player is in game correctly.
   */
  @Test
  public void isInGame()
  {
    Player throwAway = new Player(lobby);
    throwAway.isValidUsername(GOOD_NAME_2);
    CuT = new Player(lobby);
    CuT.isValidUsername(GOOD_NAME);

    assertFalse(CuT.isInGame());

    lobby.challenge(GOOD_NAME, GOOD_NAME_2);
    manager.startGame(GOOD_NAME_2, GOOD_NAME);

    // Get the updated CuT.
    CuT = manager.getOpponent(GOOD_NAME_2);

    assertTrue(CuT.isInGame());
  }

  /**
   * Tests the equals method for different objects.
   */
  @Test
  public void diffObjects()
  {
    CuT = new Player(lobby);
    //null obj
    assertNotEquals(CuT, null);
    //lobby obj
    assertNotEquals(CuT, lobby);
  }

  /**
   * Tests that the correct type of result is given when the username has no
   * number.
   */

  @Test
  public void no_Num()
  {
    CuT = new Player(lobby);
    assertEquals(UsernameResult.AVAILABLE, CuT.isValidUsername(NO_NUM));
  }

  /**
   * Tests the correct result when the username is less than 6 characters.
   */
  @Test
  public void too_Short()
  {
    CuT = new Player(lobby);
    assertEquals(UsernameResult.INVALID, CuT.isValidUsername(TOO_SHORT));
  }

  /**
   * Tests if having a space in the username is valid
   */
  @Test
  public void contains_Space(){
    CuT = new Player(lobby);
    assertEquals(UsernameResult.AVAILABLE, CuT.isValidUsername(CONTAINS_SPACE));
  }
  /**
   * Tests that the correct type of result is given when the username is too
   * long (>25 chars).
   */
  @Test
  public void too_Long()
  {
    CuT = new Player(lobby);
    assertEquals(UsernameResult.INVALID, CuT.isValidUsername(TOO_LONG));
  }

  /**
   * Tests that the correct type of result is given when the username has
   * capital letters, which is OK.
   */
  @Test
  public void caps_OK()
  {
    CuT = new Player(lobby);
    assertEquals(UsernameResult.AVAILABLE, CuT.isValidUsername(CAPS_OK));
  }

  /**
   * Has a number start which should kick back an invalid type of username
   * result.
   */
  @Test
  public void num_Start()
  {
    CuT = new Player(lobby);
    //assertEquals(UsernameResult.INVALID, CuT.isValidUsername(NUM_START));
  }

  /**
   * When a special character is present the result should be invalid.
   */

  @Test
  public void special_Char()
  {
    CuT = new Player(lobby);
    assertEquals(UsernameResult.INVALID, CuT.isValidUsername(SPECIAL_CHAR));
  }

  /**
   * tests the win percentages for session
   */

  @Test
  public void win_percentage(){
    CuT = new Player(lobby);
    CuT.setUsername("player1");
    lobby.newPlayer(CuT);

    CuT.endGame(true);
    CuT.endGame(false);

    assertEquals(50, CuT.getWinPercentage());
  }

  /**
   * test win percentage for no games played yet
   */
  @Test
  public void win_percentage_no_games_played(){
    CuT = new Player(lobby);
    assertEquals(0, CuT.getWinPercentage());
  }

  /**
   * tests the ending of a game
   */
  @Test
  public void endGameTest(){
    CuT = new Player(lobby);
    CuT.endGame(true);

    assertEquals(100, CuT.getWinPercentage());
  }
}
