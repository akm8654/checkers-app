package com.webcheckers.application;

import com.google.gson.Gson;
import com.webcheckers.model.Board;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Holds and manages all games once they start
 *
 * @author Austin Miller 'akm8654'
 * @author Mikayla Wishart 'mcw7246'
 */
public class GameManager
{

  private static Map<String, Integer> gameID = new HashMap<>();
  private static int gameIDNum = 1;
  //map where the gameID is the key and the Return is a HashMap with the pair of
  // players.
  private Map<Integer, HashMap<String, String>> pairs = new HashMap<>();
  //map of the current games going on where the key is the GameID and the value is the CheckerGame associated with it
  private static Map<Integer, CheckerGame> games = new HashMap<>();
  private static Set<String> inGame = new HashSet<>();
  private static Map<String, CheckerGame> clientSideGames = new HashMap<>();
  private static PlayerLobby playerLobby;
  private static Map<String, Integer> spectators = new HashMap<>();
  private static Map<Integer, Integer> spectatorNum = new HashMap<>();
  private static Map<Integer, String> gameOver = new HashMap<>();
  private static Set<Integer> activeGames = new HashSet<>();
  private final ReplayManager rManager;

  public GameManager(PlayerLobby lobby, ReplayManager rManager)
  {
    playerLobby = lobby;
    this.rManager = rManager;
  }

  public Set<String> getInGame()
  {
    return inGame;
  }

  /**
   * Starts games, where p1 is the person starting the challenge and p2 is the
   * person accepting
   *
   * @param challenger player starting (challenger)
   * @param victim     player accepting (victim)
   */
  public void startGame(String challenger, String victim)
  {
    playerLobby.removeChallenger(challenger);
    Player player1 = playerLobby.getPlayers().get(challenger);
    Player player2 = playerLobby.getPlayers().get(victim);
    player2.makePlayer2();
    player1.hasEnteredGame();
    player2.hasEnteredGame();
    inGame.add(challenger);
    inGame.add(victim);
    gameIDNum += 1;
    gameID.put(challenger, gameIDNum);
    gameID.put(victim, gameIDNum);
    spectatorNum.put(gameIDNum, 0);
    HashMap<String, String> pairToAdd = new HashMap<>();
    pairToAdd.put(challenger, victim);
    this.pairs.put(gameIDNum, pairToAdd);
    CheckerGame game = new CheckerGame(player1, player2, new Board());
    game.setGameID(gameIDNum);
    games.put(gameIDNum, game);
    gameOver.put(gameIDNum, "No");
    activeGames.add(gameIDNum);
    rManager.addMove(gameIDNum, game);
  }

  /**
   * Determines if the username is a spectator or not.
   *
   * @param username the username to check
   * @return true if it is a spectator or not.
   */
  public boolean isSpectator(String username)
  {
    return spectators.containsKey(username);
  }

  /**
   * A function used to end the game in the player lobby.
   *
   * @param gameIDNum the gameID to end.
   */
  public void endGame(int gameIDNum)
  {
    Map<String, String> pair = pairs.get(gameIDNum);
    Player player1 =
            playerLobby.getPlayers().get(pair.keySet().toArray()[0]);
    Player player2 = playerLobby.getPlayers().get(pair.values().toArray()[0]);
    if (player1 != null)
    {
      String challenger = player1.getUsername();
      inGame.remove(challenger);
      removeClientSideGame(challenger);
    }
    if (player2 != null)
    {
      String victim = player2.getUsername();
      inGame.remove(victim);
      removeClientSideGame(victim);
    }

    spectatorNum.remove(gameIDNum);
    activeGames.remove(gameIDNum);
  }

  public void removeFromGame(String username)
  {
    gameID.remove(username);
  }

  /**
   * Sets the game over state.
   *
   * @param gameIDNum the id number to change
   * @param status the status to set it to.
   */
  public void setGameOver(int gameIDNum, String status)
  {
    gameOver.replace(gameIDNum, status);
  }

  /**
   * Returns the status of the game over status.
   *
   * @param gameIDNum the game to get
   * @return the status of the game.
   */
  public String getGameOverStatus(int gameIDNum)
  {
    return gameOver.get(gameIDNum);
  }

  /**
   * Adds a spectator to a game id using the hashmap stored in the
   * GameManager object.
   *
   * @param username the username to add to the game
   * @param gameId the id of the game watching
   */
  public void addSpectator(String username, int gameId)
  {
    if(!spectators.containsKey(username))
    {
      spectators.put(username, gameId);
      int viewers = spectatorNum.get(gameId);
      spectatorNum.put(gameId, viewers + 1);
    }
  }

  /**
   * Returns the amount of viewers currently watching the game
   *
   * @param gameID the game that is being watched
   * @return the number of active viewers.
   */
  public int getViewers(int gameID)
  {
    if(spectatorNum.get(gameID) != null)
    {
      return spectatorNum.get(gameID);
    } else
    {
      return 0;
    }
  }

  /**
   * Removes a spectator from the map of spectators / id.
   *
   * @param username the username that  must be removed.
   */
  public void removeSpectator(String username)
  {
    int gameID = spectators.get(username);
    spectators.remove(username);
    int viewers = spectatorNum.get(gameID);
    spectatorNum.put(gameID, viewers-1);

  }

  /**
   * Just starts a test game
   *
   * @param challenger the name of the challenger (most likely player1)
   * @param victim the name of the victim (most likely player2)
   * @param filename the name of the file that holds the test board.
   */
  public void startTestGame(String challenger, String victim, String filename
          , int turn)
  {
    playerLobby.removeChallenger(challenger);
    Player player1 = playerLobby.getPlayers().get(challenger);
    Player player2 = playerLobby.getPlayers().get(victim);
    player2.makePlayer2();
    player1.hasEnteredGame();
    player2.hasEnteredGame();
    inGame.add(challenger);
    inGame.add(victim);
    gameIDNum += 1;
    gameID.put(challenger, gameIDNum);
    gameID.put(victim, gameIDNum);
    HashMap<String, String> pairToAdd = new HashMap<>();
    pairToAdd.put(challenger, victim);
    this.pairs.put(gameIDNum, pairToAdd);
    spectatorNum.put(gameIDNum, 0);
    Gson gson = new Gson();
    Board board;
    try
    {
      board = gson.fromJson(new FileReader(filename), Board.class);
    } catch (FileNotFoundException e) {
      System.err.println("ERROR: FILE NOT FOUND STARTING GAME FROM SCRATCH");
      board = new Board();
    }
    CheckerGame game = new CheckerGame(player1, player2, board);
    if(turn > 1)
    {
      game.updateTurn();
    }
    game.setGameID(gameIDNum);
    games.put(gameIDNum, game);
    gameOver.put(gameIDNum, "No");
    activeGames.add(gameIDNum);
  }

  /**
   * Produces a list of all active games
   *
   * @return all active games in a list form.
   */
  public List<CheckerGame> getGames()
  {
    List<CheckerGame> gameArrayList = new ArrayList<>();
    for(int gameID : activeGames){
      gameArrayList.add(games.get(gameID));
    }
    return gameArrayList;
  }

  /**
   * Returns the game id from a username
   *
   * @param username: the username to get the Game ID
   * @return the gameID number.
   */
  public int getGameID(String username)
  {
    if(gameID.containsKey(username))
    {
      return gameID.get(username);
    } else return spectators.getOrDefault(username, -1);
  }

  /**
   * Returns the game of the currentGameID
   *
   * @param currentGameID the id to check
   * @return the CheckerGame at that Id.
   */
  public CheckerGame getGame(int currentGameID)
  {
    return games.get(currentGameID);
  }

  /**
   * Holds all the beginning client games do that only one person has a copy
   * of it.
   *
   * @param gameID   the unique gameID of the game being played
   * @param username the client that needs to hold the game
   * @return the checkerGame copy.
   */
  public CheckerGame makeClientSideGame(int gameID, String username)
  {
    if (clientSideGames.containsKey(username))
    {
      removeClientSideGame(username);
    }
    CheckerGame serverGame = new CheckerGame(getGame(gameID));
    clientSideGames.put(username, serverGame);
    return clientSideGames.get(username);
  }

  /**
   * Removes the client side game
   *
   * @param username: the key to be removed.
   */
  public void removeClientSideGame(String username)
  {
    clientSideGames.remove(username);
  }

  /**
   * Gets the local game for the designated player.
   *
   * @param username the players name that has the game.
   * @return the client side CheckerGame.
   */
  public CheckerGame getLocalGame(String username)
  {
    return clientSideGames.get(username);
  }

  /**
   * Updates the local game
   *
   * @param username the username to change
   * @param game the game to be changed to.
   */
  public void updateLocalGame(String username, CheckerGame game)
  {
    clientSideGames.replace(username, game);
  }

  /**
   * A move has been made! Update the game.
   *
   * @param id   the gameID
   * @param game the new game.
   */
  public void updateGame(int id, CheckerGame game)
  {
    game.setMoved(false);
    games.replace(id, game);
  }

  /**
   * Finds the pair for the given gameID
   *
   * @param gameID an int of the gameID
   * @return a Pair object with the strings of the victim and challengers.
   */
  public HashMap<String, String> getPair(int gameID)
  {
    return pairs.get(gameID);
  }

  /**
   * Returns the opponent of a player.
   *
   * @param username the username of the player
   * @return the opponent as a Player Object.
   */
  public Player getOpponent(String username)
  {
    int id = getGameID(username);
    HashMap<String, String> pair = pairs.get(id);
    //if the
    if (pair.containsKey(username))
    {
      return playerLobby.getPlayers().get(pair.get(username));
    } else
    {
      for (String key : pair.keySet())
      {
        if (pair.get(key).equals(username))
        {
          return playerLobby.getPlayers().get(key);
        }
      }
      return null;
    }
  }

}
