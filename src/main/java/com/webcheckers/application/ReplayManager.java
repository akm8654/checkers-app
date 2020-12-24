package com.webcheckers.application;

import com.webcheckers.model.Board;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Move;

import java.util.*;

/**
 * A class designed to manage all players and their interactions with players
 * and the game. Helps to reach the required software design principles
 *
 * @author Austin Miller 'akm8654'
 */
public class ReplayManager
{
  private Map<Integer, List<CheckerGame>> gameMoves = new HashMap<>();
  private Set<Integer> pastGames = new HashSet<>();
  private Map<String, Map<Integer, Integer>> activeReplays = new HashMap<>();

  /**
   * adds a move to the list for whatever game.
   *
   * @param gameID the game ID to get
   * @param game   the game to add
   */
  public void addMove(int gameID, CheckerGame game)
  {
    if (gameMoves.containsKey(gameID))
    {
      List<CheckerGame> currentList = gameMoves.get(gameID);
      currentList.add(game);
      gameMoves.replace(gameID, currentList);
    } else
    {
      List<CheckerGame> currentList = new ArrayList<>();
      currentList.add(game);
      gameMoves.put(gameID, currentList);
    }
  }

  /**
   * Used to have a player start watching the game.
   *
   * @param username the username of the player watching
   * @param gameID   the gameID they're watching
   * @return 0 as it is the first move of the game
   */
  public int startWatching(String username, int gameID)
  {
    Map<Integer, Integer> atMove = new HashMap<>();
    atMove.put(gameID, 0);
    activeReplays.put(username, atMove);
    return 0;
  }

  /**
   * A helper method to represent that someone has stopped watching a replay.
   *
   * @param username the username of the person to stop watching
   */
  public void stopWatching(String username)
  {
    activeReplays.remove(username);
  }

  /**
   * Used to get the move that a current player is looking at. Starting to
   * watch if they aren't currently watching that game.
   *
   * @param username the username of the player watching
   * @param gameID   the gameId of the game they're watching
   * @return the move currently at
   */
  public int getMove(String username, int gameID)
  {
    if (activeReplays.containsKey(username))
    {
      return activeReplays.get(username).get(gameID);
    } else
    {
      return startWatching(username, gameID);
    }
  }

  /**
   * Determines the furthest move that can be made in the game.
   *
   * @param gameID the ID of the game to add
   * @return the maximum value as an Integer.
   */
  public int maxMoves(int gameID)
  {
    List<CheckerGame> games = gameMoves.get(gameID);
    return (games.size());
  }

  /**
   * Determines if the specified user is watching a replay at the moment or not
   *
   * @param username the username to check
   * @return if the map contains the username.
   */
  public boolean isWatching(String username)
  {
    return activeReplays.containsKey(username);
  }

  /**
   * Advances the player to watch the next turn
   *
   * @param username the player who is watching
   * @param gameID   the game they are watching
   */
  public void nextMove(String username, int gameID)
  {
    int move = getMove(username, gameID) + 1;
    activeReplays.get(username).replace(gameID, move);
  }

  /**
   * Takes the player back to the move that they decide to be at.
   *
   * @param username the username of the player watching
   * @param gameID   the game they are watching.
   */
  public void previousMove(String username, int gameID)
  {
    int move = getMove(username, gameID) - 1;
    activeReplays.get(username).replace(gameID, move);
  }

  /**
   * Returns the board at the current move value.
   *
   * @param gameID the game to get
   * @param move   the move value to have
   * @return the CheckerGame at the given move index.
   */
  public CheckerGame getGameAtMove(int gameID, int move)
  {
    List<CheckerGame> currentList = gameMoves.get(gameID);
    if (currentList != null)
    {
      return currentList.get(move);
    } else
    {
      return null;
    }
  }

  /**
   * Ending the game with the given id.
   *
   * @param gameID the id to end.
   */
  public void endGame(int gameID)
  {
    pastGames.add(gameID);
  }

  /**
   * Returns all of the games that have ended.
   *
   * @return the list of all games that are over.
   */
  public List<Integer> getGameOverIDs()
  {
    List<Integer> list = new ArrayList<>();
    Object[] array = pastGames.toArray();
    Integer[] intArray = new Integer[array.length];
    System.arraycopy(array, 0, intArray, 0, array.length);
    Collections.addAll(list, intArray);
    return list;
  }


}
