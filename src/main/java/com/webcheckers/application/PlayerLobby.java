package com.webcheckers.application;

import com.webcheckers.model.Player;

import java.util.*;

/**
 * A site-wide holding for all of the player's in the lobby.
 *
 * @author Austin Miller 'akm8654'
 * @author Mikayla Wishart 'mcw7246'
 */
public class PlayerLobby
{



  // Attributes
  //Player Hashmap where the username is the key.
  private static Map<String, Player> players = new HashMap<>();
  //A map of all pending challenges, the victim is the key.
  private static Map<String, String> challenges = new HashMap<>();
  //A set full of all people who challenged another
  private static Set<String> challengers = new HashSet<>();
  //A map of all games where the key is the challenger and values are
  // CheckerGames

  /**
   * Adds a new player to the lobby.
   *
   * @param player a player object.
   */
  public synchronized void newPlayer(Player player)
  {
    players.put(player.getUsername(), player);
  }

  /**
   * Adds a challenge to the current hashMap.
   *
   * @param challenger the person challenging
   * @param victim     the person challenged.
   * @return Whether the challenge can be issued, if the victim already is
   * targetted returns false.
   */
  public boolean challenge(String victim, String challenger)
  {
    if (challenges.containsKey(victim) || challengers.contains(victim))
    {
      return false;
    } else
    {
      challenges.put(victim, challenger);
      challengers.add(challenger);
      return true;
    }
  }

  /**
   * Gets the challenges ongoing in the lobby.
   *
   * @return the map of challengers and victims.
   */
  public Map<String, String> getChallenges()
  {
    return challenges;
  }

  /**
   * Determines whether someone is challenging another or not.
   *
   * @param challenger is the player actively challenging another.
   * @return whether the player is currently challenging or not. (Also false
   * if they are challenging the correct victim
   */

  public boolean challenging(String challenger, String victim)
  {
    return challenges.containsKey(victim) &&
            challenges.get(victim).equals(challenger) &&
            challengers.contains(challenger);
  }

  /**
   * Returns the set of all challengers
   *
   * @return challengers in a set of Strings.
   */
  public Set<String> getChallengers()
  {
    return challengers;
  }


  /**
   * Removes a potential challenger from the list of challengees.
   */
  public synchronized void removeChallenger(String victim)
  {
    challengers.remove(challenges.get(victim));
    challenges.remove(victim);
  }

  /**
   * Getter for the list of usernames.
   *
   * @return returns the list of usernames
   */
  public synchronized List<String> getUsernames()
  {
    return new ArrayList<>(players.keySet());
  }

  /**
   * Gets the Players hashmap.
   *
   * @return the hashmap of all players.
   */
  public synchronized Map<String, Player> getPlayers()
  {
    return players;
  }

  /**
   * public function to remove a player for sign out
   *
   * @param player -> player to be removed
   */
  public synchronized void removePlayer(Player player)
  {
    if(player != null)
    players.remove(player.getUsername());
  }

}
