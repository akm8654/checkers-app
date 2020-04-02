package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.CheckerGame;
import com.webcheckers.model.Move;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.List;
import java.util.logging.Logger;

import static com.webcheckers.util.Message.error;
import static com.webcheckers.util.Message.info;
import static spark.Spark.halt;

/**
 * Asks the server to remove the last move that was previously validated. The
 * server must update the user's turn in the user's game state.
 *
 * @author Austin Miller 'akm8654'
 */
public class PostBackupMoveRoute implements Route
{
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

  @Override
  public Object handle(Request request, Response response)
  {
    LOG.config("PostBackupMoveRoute invoked");
    final Session session = request.session();
    final Player player = session.attribute(GetHomeRoute.PLAYER_KEY);
    final Gson gson = new Gson();
    GameManager manager = session.attribute(GetHomeRoute.GAME_MANAGER_KEY);
    if(player != null)
    {
      String username = player.getUsername();
      int gameID = manager.getGameID(username);
      CheckerGame game = manager.getLocalGame(username);
      if (game == null)
      {
        game = manager.getGame(gameID);
        if (game == null)
        {
          response.redirect(WebServer.HOME_URL);
          halt();
          return "Redirected Home";
        }
      }
      List<Move> moves = session.attribute(PostValidateMoveRoute.MOVE_LIST_ID);
      if (moves == null || moves.isEmpty())
      {
        return gson.toJson(error("All moves have been backed up!"));
      } else
      {
        for (int i = 0; i < moves.size() - 1; i++)
        {
          game.makeMove(moves.get(i));
        }
        moves.remove(moves.get(moves.size() - 1));
        session.attribute(PostValidateMoveRoute.MOVE_LIST_ID, moves);
        manager.makeClientSideGame(gameID, username);
        return gson.toJson(info("Backed up to last valid move"));
      }
    } else
    {
      response.redirect(WebServer.HOME_URL);
      halt();
      return "Redirected Home";
    }
  }
}