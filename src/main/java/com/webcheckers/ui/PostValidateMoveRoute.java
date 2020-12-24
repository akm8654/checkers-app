package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.*;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.webcheckers.util.Message.error;
import static com.webcheckers.util.Message.info;
import static java.lang.Math.abs;

/**
 * This action submits a single move for validation. The server must keep
 * track of each proposed move for a single turn in the user's game state. It
 * sends INFO if the move is valid and an ERROR if not. The text should say
 * why the move is invalid.
 * <p>
 * Query's Params for actionData Move (As a class).
 *
 * @author Austin Miller 'akm8654'
 */
public class PostValidateMoveRoute implements Route
{
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

  Gson gson = new Gson();
  public static final String ACTION_DATA = "actionData";
  public static final String MOVE_LIST_ID = "moves";

  /**
   * Adds a move to the list of moves stored in the session.
   *
   * @param move: the move that has been made
   */
  private void addMove(Session session, Move move)
  {
    List<Move> moves = session.attribute(MOVE_LIST_ID);
    if (moves == null)
    {
      moves = new ArrayList<>();
    }
    moves.add(move);
    session.attribute(MOVE_LIST_ID, moves);
  }

  @Override
  public String handle(Request request, Response response)
  {
    final Session session = request.session();
    final String moveStr = request.queryParams(ACTION_DATA);

    LOG.config("Validating move: " + moveStr);

    final Move move = gson.fromJson(moveStr, Move.class);
    final Player player = session.attribute(GetHomeRoute.PLAYER_KEY);
    if (player != null)
    {
      GameManager manager = session.attribute(GetHomeRoute.GAME_MANAGER_KEY);
      String username = player.getUsername();
      int gameID = manager.getGameID(username);
      CheckerGame localGame = manager.getLocalGame(username);
      if (localGame == null)
      {
        //Create the local game if not already made.
        localGame = manager.makeClientSideGame(gameID, username);
        if (localGame == null)
        {
          response.redirect(WebServer.HOME_URL);
          return "Redirected Home";
        }
      }

      Board gameBoard = localGame.getBoard();
      //Creates a log of the board.
      //LOG.config("Validating move from board:\n" + gson.toJson(gameBoard));
      int startCell = abs(move.getStart().getCell());
      int endCell = abs(move.getEnd().getCell());
      Space startSpace = gameBoard.getSpaceAt(move.getStart().getRow(), startCell);
      Space endSpace = gameBoard.getSpaceAt(move.getEnd().getRow(), endCell);
      Move.MoveStatus moveValidity = move.validateMove(localGame, startSpace, endSpace);
      String msg = "ERROR: Server Side ERROR!";
      switch (moveValidity)
      {
        case INVALID_SPACE:
          msg = "Invalid Move: Space is the wrong color!";
          break;
        case ALREADY_MOVED:
          msg = "Already Moved: Already moved a piece!";
          break;
        case VALID:
          localGame.setMoved(true);
          msg = "Valid Move! Click submit to send";
          localGame.makeMove(move);
          addMove(session, move);
          return gson.toJson(info(msg));
        case JUMP:
          msg = "Jump Move! Click submit to send.";
          localGame.setMoved(true);
          localGame.makeMove(move);
          addMove(session, move);
          return gson.toJson(info(msg));
        case OCCUPIED:
          msg = "Invalid Move: Space is already Occupied!";
          break;
        case TOO_FAR:
          msg = "Invalid Move: Space is too far away!";
          break;
        case SAME_SPACE:
          msg = "Invalid Move: The Space you've tried to move to is the same " +
                  "one!";
          break;
        case INVALID_BACKWARDS:
          msg = "Invalid Move: You aren't kinged yet! You must move forward!";
          break;
        case JUMP_OWN:
          msg = "Invalid Move: You're attempting to jump your own piece!";
          break;
      }
      //Must return an invalid move
      return gson.toJson(error(msg));
    } else
    {
      response.redirect(WebServer.HOME_URL);
      return "Redirected Home";
    }
  }
}
