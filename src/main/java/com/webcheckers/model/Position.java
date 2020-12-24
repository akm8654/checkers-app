package com.webcheckers.model;


/**
 * Holds the position of a piece.
 *
 * @author Austin Miller 'akm8654'
 */
public class Position
{
  private int row; // from 0 to 7
  private int cell; //col from 0 to 7
  public static final int BOARD_SIZE = 8;
  private static final int MAX_ROW = BOARD_SIZE - 1;
  private static final int MAX_CELL = BOARD_SIZE - 1;

  /**
   * Creates a position object using a given row and cell, if they are out of
   * bounds then
   *
   * @param row  the row index of the position.
   * @param cell the cell index of the position.
   */
  public Position(int row, int cell)
  {
    if (row > MAX_ROW)
    {
      this.row = -1; //error
    } else
    {
      this.row = row;
    }

    if (cell > MAX_CELL)
    {
      this.cell = -1;
    } else
    {
      this.cell = cell;
    }
  }

  /**
   * Gets the row.
   *
   * @return the row index for the position
   */
  public int getRow()
  {
    return row;
  }

  /**
   * Gets the column index for the position.
   *
   * @return an int representing the column. (0-based)
   */
  public int getCell()
  {
    return cell;
  }

  /**
   * Used to compare two position objects.
   * @param other The other (should be) position object to compare to this one.
   * @return true only if the compared object is not null, is an instance of Position,
   * and the row and cell attributes of both positions are equal in value.
   */
  @Override
  public boolean equals(Object other)
  {
    if(other != null)
    {
      if(other instanceof Position)
      {
        return this.getRow() == ((Position) other).getRow() && this.getCell() == ((Position) other).getCell();
      }
    }
    return false;
  }
}
