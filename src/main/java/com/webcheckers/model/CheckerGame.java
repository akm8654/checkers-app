package com.webcheckers.model;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Wrapper class for model
 *
 * @author Zehra Amena Baig (zab1166)
 */

public class CheckerGame
{
  /**
   * Red Player
   */
  private Player PLAYER_ONE;
  /**
   * White Player
   */
  private Player PLAYER_TWO;

  /**
   * The board
   */
  private Board board;

  /**
   * Who's turn it is, 1 or 2.
   */
  private int turn = 1;

  private Stack<Space> jumpedPieces;

  private boolean moved = false;

  private int numRedPieces, numWhitePieces;

  private int gameID;

  /**
   * Constructor
   */
  public CheckerGame(Player PLAYER_ONE, Player PLAYER_TWO, Board board)
  {
    this.PLAYER_ONE = PLAYER_ONE;
    this.PLAYER_TWO = PLAYER_TWO;
    this.board = board;
    jumpedPieces = new Stack<Space>();
    countInitialPieces();
  }

  /**
   * Creates a copy of the checkergame from another.
   *
   * @param original: The original CheckerGame
   */
  public CheckerGame(CheckerGame original)
  {
    this.PLAYER_ONE = original.PLAYER_ONE;
    this.PLAYER_TWO = original.PLAYER_TWO;
    this.board = new Board(original.board);
    this.turn = original.turn;
    this.gameID = original.getGameID();
    jumpedPieces = new Stack<Space>();
    countInitialPieces();
  }

  /**
   * Gets the game ID.
   *
   * @return the integer gameID.
   */
  public int getGameID()
  {
    return gameID;
  }

  /**
   * Sets the game ID to the specified value.
   *
   * @param gameID the game Id to be set to.
   */
  public void setGameID(int gameID)
  {
    this.gameID = gameID;
  }

  /**
   * Get whose turn it is to go.
   *
   * @return the int of players turn.
   */
  public String getTurn()
  {
    if (turn == 1)
    {
      return PLAYER_ONE.getUsername();
    } else
    {
      return PLAYER_TWO.getUsername();
    }
  }

  /**
   * Get active color
   *
   * @return the piece color of red or white if the turn is 1 or 2 respectively.
   */
  public Piece.Color getColor()
  {
    if (turn == 1)
    {
      return Piece.Color.RED;
    } else
    {
      return Piece.Color.WHITE;
    }
  }

  /**
   * Returns first (red) player
   *
   * @return the red player
   */
  public Player getRedPlayer()
  {
    return PLAYER_ONE;
  }

  /**
   * Makes a move for the game
   *
   * @param move the move to be made
   */
  public void makeMove(Move move)
  {
    Position start = move.getStart();
    Position end = move.getEnd();

    Piece piece = board.getSpaceAt(start.getRow(), start.getCell()).getPiece();
    board.getSpaceAt(end.getRow(), end.getCell()).setPiece(piece);
    board.getSpaceAt(start.getRow(), start.getCell()).setPiece(null);
  }

  /**
   * Switches the turn between 2 and 1.
   */
  public void updateTurn()
  {
    turn = (turn % 2) + 1; //Switches between 2 and 1.
  }

  /**
   * Returns second (white) player
   *
   * @return the white player
   */
  public Player getWhitePlayer()
  {
    return PLAYER_TWO;
  }

  /**
   * Returns the average of the players who are playing.
   *
   * @return the average of the two.
   */
  public double winningAverage()
  {
    return (PLAYER_ONE.getWinPercentage() + PLAYER_TWO.getWinPercentage())/2;
  }

  /**
   * Gets the actual board (not just the view)
   *
   * @return the board.
   */
  public Board getBoard()
  {
    return board;
  }

  public void addJumpedPieces(Space space)
  {
    jumpedPieces.push(space);
  }

  public Space getJumpedPiece()
  {
    try
    {
        Space sp = jumpedPieces.pop();
        Piece piece = sp.getPiece();
        if(piece.getColor().equals(Piece.Color.RED))
        {
          numRedPieces--;
        }
        else if(piece.getColor().equals(Piece.Color.WHITE))
        {
          numWhitePieces--;
        }
        return sp;
    }catch(EmptyStackException | NullPointerException ese) {
      return null;
    }
  }

  private void countInitialPieces()
  {
    numRedPieces = 0;
    numWhitePieces = 0;

    for(Row r : board)
    {
      for(Space s : r)
      {
        if(s.isValidSpace())
        {
          if(s.getPiece() != null)
          {
            if(s.getPiece().getColor().equals(Piece.Color.RED))
            {
              numRedPieces++;
            }
            else if(s.getPiece().getColor().equals(Piece.Color.WHITE))
            {
              numWhitePieces++;
            }
          }
        }
      }
    }
  }

  public boolean hasMoved()
  {
    return moved;
  }

  public void setMoved(boolean moved)
  {
    this.moved = moved;
  }

  public int getNumRedPieces()
  {
    return numRedPieces;
  }

  public int getNumWhitePieces()
  {
    return numWhitePieces;
  }
}
