package com.webcheckers.model;

import java.util.*;

/**
 * Determines whether a move is required or not, mostly produces methods but
 * stores some values to value again quickly.
 *
 * @author Austin Miller 'akm8654'
 * @author Mikayla Wishart 'mcw7246'
 */
public class RequireMove
{
  private final Board board;
  private Piece.Color color;

  public RequireMove(Board board, Piece.Color color)
  {
    this.board = board;
    this.color = color;
  }

  /**
   * Adds all the moves that it can in one column direction to the stack.
   *
   * @param moves       the moves already created in the stack
   * @param board       the board that is being checked
   * @param row         the initial row
   * @param col         the inital column
   * @param rowCheck    the row being checked
   * @param colCheck    the column being checked
   * @param colorFactor the direction
   * @return the updated moves stack.
   */
  public Stack<Move> addMovesColOneDirection(Stack<Move> moves, Board board,
                                             int row, int col, int rowCheck,
                                             int colCheck, int colorFactor,
                                             Space checkSpace)
  {
    Position startPos = new Position(row, col);
    //Is the space empty? If so valid move.

    SpaceStatus status = isAvailableSpace(checkSpace, color);
    switch (status)
    {
      case OCC_SAME:
        break;
      case OCC_DIFF:
        //Check to see if the jump is possible
        if (validDimension(rowCheck += colorFactor))
        {
          //Check Correct Direction
          if (validDimension(colCheck += (colCheck-col)))
          {
            checkSpace = board.getSpaceAt(rowCheck, colCheck);
            if (isAvailableSpace(checkSpace, color).
                    equals(SpaceStatus.FREE))
            {
              Position endPos = new Position(rowCheck, colCheck);
              moves.push(new Move(startPos, endPos, Move.MoveStatus.JUMP));
            }
          }
        }
        break;
      case FREE:
        Position endPos = new Position(rowCheck, colCheck);
        moves.push(new Move(startPos, endPos));
        break;
    }
    return moves;
  }

  /**
   * Changes the color for the RequireMove object
   *
   * @param color the color to change to.
   */
  public void updateColor(Piece.Color color)
  {
    this.color = color;
  }

  //Stores all valid moves, all the possible jumps are stored under the jump
  // key, all the possible regular moves are stored as another.
  public Map<Move.MoveStatus, List<Move>> validMoves;

  /**
   * Returns a map of all the valid moves that exist on the board
   *
   * @return a map where the status is the key and a list of moves that fall
   * under that type on the board exist.
   */
  public Map<Move.MoveStatus, List<Move>> getAllMoves()
  {
    //reset the current hashmap of valid moves (in case something went wrong)
    validMoves = new HashMap<>();
    List<Move> regList = new ArrayList<>();
    List<Move> jumpList = new ArrayList<>();
    //Begin iterating through the board.
    for (int row = 0; row < Board.DIMENSIONS; row++)
    {
      for (int col = 0; col < Board.DIMENSIONS; col++)
      {
        Space checkSpace = board.getSpaceAt(row, col);
        Stack<Move> moves = getValidMoves(board, checkSpace, color);
        //There are moves to add.
        if (moves != null && !moves.isEmpty())
        {
          Move move;
          do
          {
            move = moves.pop();
            if (move.getStatus().equals(Move.MoveStatus.VALID))
            {
              regList.add(move);
            } else
            {
              jumpList.add(move);
            }
          } while (!moves.isEmpty());
        }
      }
    }
    validMoves.put(Move.MoveStatus.JUMP, jumpList);
    validMoves.put(Move.MoveStatus.VALID, regList);
    return validMoves;
  }

  /**
   * Creates a stack of all valid moves for the given space.
   *
   * @param board the board where the moves are being checked
   * @param space the space to be checked
   * @return a stack of all moves.
   */
  public Stack<Move> getValidMoves(Board board, Space space, Piece.Color color)
  {
    Piece piece = space.getPiece();
    Stack<Move> moves = new Stack<>();
    if (piece == null || !space.isBlackSpace())
    {
      return null;
      //Doesn't need to check if the piece is not the color we're checking!
    } else if (!piece.getColor().equals(color))
    {
      return null;
    } else
    {
      int colorFactor = -1;
      moves = addMovesRowOneDirection(moves, board, piece, space, colorFactor);
      boolean king = piece.getType().equals(Piece.Type.KING);
      if (king)
      {
        //Well now I have to check the other direction.
        colorFactor = -colorFactor;
        moves = addMovesRowOneDirection(moves, board, piece, space, colorFactor);
      }
      return moves;
    }
  }

  /**
   * Quickly gets all the valid moves already calculated in the object.
   *
   * @return the map of valid moves.
   */
  public Map<Move.MoveStatus, List<Move>> getQuickValidMoves()
  {
    return validMoves;
  }

  /**
   * Returns what is going on with the space that is being checked
   *
   * @param space the space to be checked
   * @param color the color of the piece attempting to move their
   * @return the status of the space compared to the piece color provided.
   */
  public SpaceStatus isAvailableSpace(Space space, Piece.Color color)
  {
    Piece piece = space.getPiece();
    if (piece == null)
    {
      return SpaceStatus.FREE;
    } else if (piece.getColor().equals(color))
    {
      return SpaceStatus.OCC_SAME;
    } else
    {
      return SpaceStatus.OCC_DIFF;
    }
  }

  /**
   * A helper function that add moves in one direction (saves from typing it
   * all again since all you have to do is switch the colorFactor.
   *
   * @param moves       the stack of moves to be added to.
   * @param board       the board that is being checked
   * @param piece       the piece located in the space to be checked
   * @param space       the space where all the moves originate from.
   * @param colorFactor the direction of movement
   * @return the updated list!
   */
  public Stack<Move> addMovesRowOneDirection(Stack<Move> moves, Board board,
                                             Piece piece, Space space,
                                             int colorFactor)
  {
    Piece.Color color = piece.getColor();
    //determines what direction the pieces are going.
    if (color == Piece.Color.WHITE)
    {
      colorFactor = -colorFactor;
    }
    int row = space.getRowIndex();
    int col = space.getColumnIndex();
    int rowCheck = row + colorFactor;

    //Checks the 2 spaces in front of it. (if possible)
    if (validDimension(rowCheck))
    {
      int colCheck = col + colorFactor;
      for(int i = 0; i < 2; i ++)
      {
        if (validDimension(colCheck))
        {
          Space checkSpace = board.getSpaceAt(rowCheck,
                  colCheck);
          moves = addMovesColOneDirection(moves, board, row, col, rowCheck,
                  colCheck, colorFactor, checkSpace);
        }
        colCheck = colCheck-2*colorFactor;
      }
    }
    return moves;
  }

  /**
   * Simple helper function to determine if the given dimension is valid and
   * will produce something on the checkerboard.
   *
   * @param dim the dimension to be checked
   * @return true if greater than 0 and less than
   */
  public boolean validDimension(int dim)
  {
    return dim >= 0 && dim < Board.DIMENSIONS;
  }

  public enum SpaceStatus
  {OCC_SAME, OCC_DIFF, FREE}

}
