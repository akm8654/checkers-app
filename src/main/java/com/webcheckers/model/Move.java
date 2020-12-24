package com.webcheckers.model;


import java.util.Objects;

/**
 * Uses a getStart and getEnd for a position model.
 *
 * @author Austin Miller 'akm8654'
 * @author Mikayla Wishart 'mcw7246'
 */
public class Move
{
  //Valid also is regular move.
  public enum MoveStatus
  {
    INVALID_SPACE, VALID, OCCUPIED, TOO_FAR, SAME_SPACE,
    INVALID_BACKWARDS, JUMP_OWN, JUMP, ALREADY_MOVED
  }

  private Position start;
  private Position end;
  //Holds valid or jump depending on the move type.
  private MoveStatus regStatus;

  public Move(Position start, Position end)
  {
    this.start = start;
    this.end = end;
    this.regStatus = MoveStatus.VALID;
  }

  /**
   * Constructor to set the style of moves.
   *
   * @param start  the starting position
   * @param end    the ending position
   * @param status the status to set it too.
   */
  public Move(Position start, Position end, MoveStatus status)
  {
    this.start = start;
    this.end = end;
    this.regStatus = status;
  }

  /**
   * A getter function for the status. Should only return Valid or Jump!
   *
   * @return valid or jump depending on what type of move it is.
   */
  public MoveStatus getStatus()
  {
    return regStatus;
  }

  /**
   * Gets the starting position for the move.
   *
   * @return a position object.
   */
  public Position getStart()
  {
    return start;
  }

  /**
   * Gets the ending position for the given move.
   *
   * @return the ending position for the end.
   */
  public Position getEnd()
  {
    return end;
  }

  /**
   * Validates that a move is correct.
   *
   * @param game the game that is being played
   * @return a status based on what was found.
   */
  public MoveStatus validateMove(CheckerGame game, Space startSpace, Space endSpace)
  {
    Board board = game.getBoard();
    //Space startSpace = board.getSpaceAt(start.getRow(), start.getCell());
    //Space endSpace = board.getSpaceAt(end.getRow(), end.getCell());

    Piece piece = startSpace.getPiece();
    boolean king = piece.getType().equals(Piece.Type.KING);

    Objects.requireNonNull(piece, "piece cannot be null at the start of a " +
            "move");

    int colorFactor = 1;
    if (piece.getColor() == Piece.Color.WHITE)
    {
      colorFactor = -colorFactor;
    }

    int rowDiff = (end.getRow() - start.getRow());
    int colDiff = (end.getCell() - start.getCell());

    MoveStatus status = null;

    if (!endSpace.isValidSpace()) //is the space the correct color?
    {
      status = MoveStatus.INVALID_SPACE;
    } else if (startSpace.equals(endSpace)) //is the space
    // the same as the start?
    {
      status = MoveStatus.SAME_SPACE;
    } else
    {
      if (endSpace.getPiece() != null) // there's already a piece there!
      {
        status = MoveStatus.OCCUPIED;
      }
      if (Math.abs(rowDiff) > 1 || Math.abs(colDiff) > 1) //more than 1 by 1
      // away.
      {
        if (Math.abs(rowDiff) > 2 || Math.abs(colDiff) > 2) // more than 2
        // away in any direction.
        {
          status = MoveStatus.TOO_FAR;
        }
        Space jumpSpace = board.getSpaceAt(start.getRow() + (rowDiff / 2),
                start.getCell() + (colDiff / 2));
        Piece jumpPiece = jumpSpace.getPiece();
        if (jumpPiece != null)
        {
          if (jumpPiece.getColor().equals(piece.getColor()))
          {
            status = MoveStatus.JUMP_OWN;
          } else if (king)
          {
            status = MoveStatus.JUMP;
            game.addJumpedPieces(jumpSpace);
            this.regStatus = status;
            return status;
          } else if (colorFactor * rowDiff > 0)
          {
            status = MoveStatus.INVALID_BACKWARDS;
          } else
          {
            status = MoveStatus.JUMP;
            game.addJumpedPieces(jumpSpace);
          }
        } else
        {
          status = MoveStatus.TOO_FAR;
        }
      }
      if (king)
      {
        status = game.hasMoved() ? MoveStatus.ALREADY_MOVED : MoveStatus.VALID;
      } else if (colorFactor * rowDiff > 0)
      {
        status = MoveStatus.INVALID_BACKWARDS;
      } else if (status == null)
      {
        status = game.hasMoved() ? MoveStatus.ALREADY_MOVED : MoveStatus.VALID;
      }
    }
    this.regStatus = status;
    return status;
  }

  /**
   * Used to determine if a moves is the same as another
   *
   * @param o the object to check against
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    Move move = (Move) o;
    return (move.start.equals(this.start) && move.end.equals(this.end));
  }

  @Override
  public int hashCode()
  {
    return this.start.getCell() * 100 + this.start.getRow() * 1000 +
            this.end.getRow() * 10 + this.end.getCell();
  }
}