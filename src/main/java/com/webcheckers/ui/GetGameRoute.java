package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameManager;
import com.webcheckers.model.*;
import spark.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.webcheckers.model.Piece.Color.RED;
import static com.webcheckers.model.Piece.Color.WHITE;
import static com.webcheckers.ui.GetReplayGameRoute.NOT_REPLAY;
import static com.webcheckers.ui.PostRequestGameRoute.MESSAGE;
import static com.webcheckers.ui.WebServer.HOME_URL;

/**
 * Used to get the game and render the game.ftl by using the Spark interface
 * for Model and View and the template engine that is defined within the top
 * level WebServer
 *
 * @author Austin Miller 'akm8654'
 * @author Zehra Amena Baig 'zab1166'
 */
public class GetGameRoute implements Route
{
  //Constants
  public static final String VIEW_MODE = "viewMode";
  public static final String RED_PLAYER = "redPlayer";
  public static final String WHITE_PLAYER = "whitePlayer";
  public static final String ACTIVE_COLOR = "activeColor";
  public static final String GAME_BOARD_VIEW = "board";
  public static final String VIEW_NAME = "game.ftl";
  public static final String GAME_BOARD = "board_actual";
  public static final String VIEWERS = "viewers";

  //Attributes
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());
  private Gson gson = new Gson();
  private final TemplateEngine templateEngine;

  public GetGameRoute(TemplateEngine templateEngine)
  {
    //validate
    Objects.requireNonNull(templateEngine,
            "templateEngine must not be null");
    this.templateEngine = templateEngine;
  }

  public synchronized Object handle(Request request, Response response)
  {
    LOG.config("GetGameRoute invoked");
    final Map<String, Object> vm = new HashMap<>();
    vm.put(GetHomeRoute.TITLE_ATTR, "Web Checkers");

    final Session session = request.session();
    final Player player = session.attribute(GetHomeRoute.PLAYER_KEY);

    String theme = session.attribute("theme");
    if (theme != null)
    {
      vm.put("theme", true);
    } else
    {
      vm.put("theme", false);
    }

    if (player != null)
    {
      session.attribute(MESSAGE, null);
      GameManager gameManager = session.attribute(GetHomeRoute.GAME_MANAGER_KEY);
      String CURRENT_PLAYER = "currentUser";
      String username = player.getUsername();
      String oppUsername;
      vm.put(CURRENT_PLAYER, username);

      CheckerGame game;
      int gameIdNum = gameManager.getGameID(player.getUsername());
      game = gameManager.getGame(gameIdNum);
      if (game == null)
      {
        response.redirect(HOME_URL);
        return null;
      }
      vm.put(NOT_REPLAY, true);
      Player redPlayer = game.getRedPlayer();
      Player whitePlayer = game.getWhitePlayer();
      vm.put(VIEW_MODE, Player.ViewMode.PLAY);
      //Reset Moves
      session.attribute(PostValidateMoveRoute.MOVE_LIST_ID, null);
      session.attribute(GAME_BOARD, game.getBoard());
      Piece.Color color;
      vm.put(RED_PLAYER, redPlayer);
      vm.put(WHITE_PLAYER, whitePlayer);
      if (game.getRedPlayer().getUsername().equals(username))
      {
        color = Piece.Color.RED;
        oppUsername = game.getWhitePlayer().getUsername();
        BoardView bV = new BoardView(game.getBoard());
        vm.put(GAME_BOARD_VIEW, bV);
      } else
      {
        color = Piece.Color.WHITE;
        oppUsername = game.getRedPlayer().getUsername();
        BoardView bV = new BoardView(game.getBoard());
        bV.flip();
        vm.put(GAME_BOARD_VIEW, bV);
      }
      vm.put(VIEWERS, gameManager.getViewers(gameIdNum));
      RequireMove requireMove = new RequireMove(game.getBoard(), color);
      Map<Move.MoveStatus, List<Move>> availableMoves = requireMove.getAllMoves();
      //check me then opponent for if opponent can move.
      final Map<String, Object> modeOptions = new HashMap<>(2);

      boolean inGame = player.isInGame();
      vm.put(ACTIVE_COLOR, game.getColor());
      int redPieces = game.getNumRedPieces();
      int whitePieces = game.getNumWhitePieces();

      /*
      If all pieces were taken
       */
      if ((redPieces == 0) || (whitePieces == 0))
      {
        modeOptions.put("isGameOver", true);
        if ((whitePieces == 0 && player.equals(game.getRedPlayer())) ||
                redPieces == 0 && player.equals(game.getWhitePlayer()))
        {
          String gameOverMessage = oppUsername + "'s pieces " +
                  "were all taken! You win!";
          gameManager.setGameOver(gameIdNum, gameOverMessage);
          modeOptions.put("gameOverMessage", gameOverMessage);
          vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
          if (inGame)
          {
            player.hasEnteredGame();
          }
          player.endGame(true);
          gameManager.getOpponent(username).endGame(false);
          return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
        } else
        {
          String gameOverMessage = username + ", Your pieces " +
                  "were all taken! You lose!";
          modeOptions.put("gameOverMessage", gameOverMessage);
          gameManager.setGameOver(gameIdNum, gameOverMessage);
          vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
          if (inGame)
          {
            player.hasEnteredGame();
          }
          player.endGame(false);
          gameManager.getOpponent(username).endGame(true);
          return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
        }
      }

      /*
      End game if no more moves are left.
       */
      if (availableMoves.get(Move.MoveStatus.JUMP).isEmpty() &&
              availableMoves.get(Move.MoveStatus.VALID).isEmpty())
      {
        modeOptions.put("isGameOver", true);
        String gameOverMessage = oppUsername + " has stopped you " +
                "from moving! You've lost!";
        modeOptions.put("gameOverMessage", gameOverMessage);
        gameManager.setGameOver(gameIdNum, gameOverMessage);
        vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
        if (inGame)
        {
          player.hasEnteredGame();
        }
        player.endGame(false);
        gameManager.getOpponent(username).endGame(true);
        return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
      } else
      {
        if (color == RED)
        {
          color = WHITE;
        } else
        {
          color = RED;
        }
        requireMove.updateColor(color);
        availableMoves = requireMove.getAllMoves();
        if (availableMoves.get(Move.MoveStatus.JUMP).isEmpty() &&
                availableMoves.get(Move.MoveStatus.VALID).isEmpty())
        {
          modeOptions.put("isGameOver", true);
          String gameOverMessage = oppUsername + " cannot " +
                  "move! You win!";
          modeOptions.put("gameOverMessage", gameOverMessage);
          gameManager.setGameOver(gameIdNum, gameOverMessage);
          vm.put(ACTIVE_COLOR, game.getColor());
          vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
          if (inGame)
          {
            player.hasEnteredGame();
          }
          player.endGame(true);
          gameManager.getOpponent(username).endGame(false);
          return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
        }
      }

      /*
      Did someone resign?
       */
      String gameOverStatus = gameManager.getGameOverStatus(gameIdNum);
      if (gameOverStatus.contains("resigned"))
      {
        modeOptions.put("isGameOver", true);
        modeOptions.put("gameOverMessage", gameOverStatus);
        if (inGame)
        {
          player.hasEnteredGame();
        }
        if (gameOverStatus.contains(username))
        {
          player.endGame(false);
          gameManager.getOpponent(username).endGame(true);
        } else
        {
          player.endGame(true);
          try
          {
            gameManager.getOpponent(username).endGame(false);
          } catch(NullPointerException e)
          {
            //
          }
        }
        vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
        return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
      }
      return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
    } else

    {
      response.redirect(HOME_URL);
      return "Home Redirect";
    }
  }
}
