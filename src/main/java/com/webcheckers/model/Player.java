package com.webcheckers.model;

import com.webcheckers.application.PlayerLobby;

import java.util.regex.Pattern;

//import static jdk.vm.ci.meta.JavaKind.Char;

/**
 * The player that each logged in user creates. this is what is manipulated
 * throughout the code.
 *
 * @author Austin Miller 'akm8654' Mikayla Wishart 'mcw7246'
 */

public class Player
{
  public static UsernameResult result = null;

  public enum ViewMode
  {
    PLAY, SPECTATOR, REPLAY
  }

  private String username;
  private PlayerLobby playerLobby;
  private boolean inGame = false;
  private int playerNum = 1;
  private int gamesWon = 0;
  private int gamesPlayed = 0;
  private boolean inArchive = false;

  /**
   * constructor for Player
   *
   * @param playerLobby the lobby that the user is going into
   */
  public Player(PlayerLobby playerLobby)
  {
    this.playerLobby = playerLobby;
  }

  /**
   * Updates the player into the second player.
   */
  public void makePlayer2()
  {
    this.playerNum = 2;
  }

  /**
   * A getter for the player number.
   *
   * @return either 1 or 2 depending on if they challenged or were the victim.
   */
  public int getPlayerNum()
  {
    return playerNum;
  }

  /**
   * Toggles that the player is in game.
   */
  public void hasEnteredGame()
  {
    inGame = !inGame;
  }

  /**
   * Returns whether the player is actively in a game or not.
   *
   * @return true or false for in game.
   */
  public boolean isInGame()
  {
    return inGame;
  }

  /**
   * @param name the username that the user submits
   */
  public void setUsername(String name)
  {
    username = name;
  }
  //constructor: saves the username that the player wants

  /**
   * Returns how good at winnning this player is.
   *
   * @return the win percentage.
   */
  public double getWinPercentage()
  {
    if (gamesPlayed != 0)
    {
      return ((double) Math.round(((double) gamesWon / (double) gamesPlayed)*10000))/100;
    } else
    {
      return 0;
    }
  }

  /**
   * Return whether they are looking for replay or not.
   *
   * @return whether the user is in the archive.
   */
  public boolean inArchive()
  {
    return inArchive;
  }

  /**
   * determines whether or enter the archive.
   * @param enter to enter or exit the archive.
   */
  public void enterOrExitArchive(boolean enter)
  {
    inArchive = enter;
  }

  /**
   * End a game for a player.
   *
   * @param won the game has been played.
   */
  public void endGame(boolean won)
  {
    if (won)
    {
      gamesWon++;
    }
    gamesPlayed++;
  }

  /**
   * checks if username is valid
   *
   * @param username the username that the user inputs
   * @return the enum for UsernameResult
   */
  public synchronized UsernameResult isValidUsername(String username)
  {
    /*
     * make sure that the username contains letters and numbers and spaces only
     */

    boolean userContains = Pattern.matches("[a-zA-Z0-9\\s]+", username);
    boolean containsSpace = Pattern.compile(" ").matcher(username).find();
    boolean numContains = Pattern.compile("[0-9]").matcher(username).find();
    if (!userContains || username.length() < 6 || username.length() > 25 ||
            username.matches("^[0-9].*$"))
    {
      result = UsernameResult.INVALID;
      return result;
    }

    /*
     * if the arrayList.size() == 0 then there are no users in the playerLobby
     * and the username can be any username
     * if there are already people in playerlobby
     */
    else
    {
      if (containsSpace)
      {
        String trimmedUsername = username.trim();
        if (playerLobby.getUsernames().contains(trimmedUsername))
        {
          result = UsernameResult.TAKEN;
        } else
        {
          setUsername(trimmedUsername);
          playerLobby.newPlayer(this);
          result = UsernameResult.AVAILABLE;
        }
      }

      //username already exists
      else if (playerLobby.getUsernames().stream().anyMatch(p1 -> p1.equals(username)))
      {
        result = UsernameResult.TAKEN;
      } else
      {
        //if there are people in the lobby but chose an acceptable username
        setUsername(username);
        playerLobby.newPlayer(this);
        result = UsernameResult.AVAILABLE;
      }
    }
    return result;
  }

  /**
   * @param o object that you are compete
   *
   * @return whether the two objects are equal or not.
   */
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    Player player = (Player) o;
    return username.equals(player.username);
  }

  /**
   * Gets the username for the specified player.
   *
   * @return the username of the player.
   */
  public synchronized String getUsername()
  {
    return username;
  }

  public enum UsernameResult
  {TAKEN, AVAILABLE, INVALID}
}
