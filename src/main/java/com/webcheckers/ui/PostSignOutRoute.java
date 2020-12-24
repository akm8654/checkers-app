package com.webcheckers.ui;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import spark.*;

import java.util.Objects;

import static com.webcheckers.ui.GetHomeRoute.GAME_MANAGER_KEY;
import static com.webcheckers.ui.GetHomeRoute.PLAYER_KEY;

/**
 * UI controller to POST a sign out
 *
 * @author Mikayla Wishart 'mcw7246', Sean Bergen 'sdb2139'
 **/

public class PostSignOutRoute implements Route
{

  private PlayerLobby playerLobby;

  //
  // Constructor
  //
  public PostSignOutRoute(TemplateEngine templateEngine, PlayerLobby playerLobby)
  {
    // validation
    Objects.requireNonNull(playerLobby, "playerLobby must not be null");
    Objects.requireNonNull(templateEngine, "templateEngine must not be null");
    //
    this.playerLobby = playerLobby;
  }

  @Override
  public String handle(Request request, Response response)
  {
    // retrieve the game object
    final Session session = request.session();

    //
    // Attributes
    //
    Player player = session.attribute(GetHomeRoute.PLAYER_KEY);
    if (player != null)
    {
      if (player.isInGame())
      {
        GameManager manager = session.attribute(GAME_MANAGER_KEY);
        int gameID = manager.getGameID(player.getUsername());
        manager.setGameOver(gameID, player.getUsername() + " has resigned.");
      }
      playerLobby.removeChallenger(player.getUsername());
      playerLobby.removePlayer(player);
      session.attribute(PLAYER_KEY, null);
    }
    response.redirect(WebServer.HOME_URL);
    return "Redirect Home";
  }
}