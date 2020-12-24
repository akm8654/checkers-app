package com.webcheckers.ui;

import com.webcheckers.application.GameManager;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.application.ReplayManager;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.TemplateEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mikayla Wishart
 */
public class PostSignOutRouteTest
{

  private PlayerLobby playerLobby;
  private GameManager manager;
  private ReplayManager replayManager;
  private Player player;
  private Player player2;

  private TemplateEngine templateEngine;
  private Session session;
  private Request request;
  private Response response;

  private PostSignOutRoute CuT;

  @BeforeEach
  public void setup(){
    playerLobby = new PlayerLobby();
    replayManager = new ReplayManager();
    manager = new GameManager(playerLobby, replayManager);
    player = new Player(playerLobby);
    player.setUsername("player");
    player2 = new Player(playerLobby);
    player2.setUsername("player2");
    playerLobby.newPlayer(player);
    playerLobby.newPlayer(player2);

    templateEngine = mock(TemplateEngine.class);
    session = mock(Session.class);
    request = mock(Request.class);
    response = mock(Response.class);
    when(request.session()).thenReturn(session);

    CuT = new PostSignOutRoute(templateEngine, playerLobby);
  }

  @Test
  public void testPlayer_is_in_game(){
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);
    when(session.attribute(GetHomeRoute.GAME_MANAGER_KEY)).thenReturn(manager);

    playerLobby.challenge(player2.getUsername(), player.getUsername());
    manager.startGame(player.getUsername(), player2.getUsername());
    int gameID = manager.getGameID(player.getUsername());
    assertNotNull(manager.getGame(gameID));
    CuT.handle(request, response);
    assertFalse(playerLobby.getUsernames().contains(player.getUsername()));
  }
}
