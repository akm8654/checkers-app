package com.webcheckers.model;

import java.util.*;

import static com.webcheckers.model.Board.DIMENSIONS;

/**
 * A view component for the board that allows flipping without changing any
 * board contents.
 *
 * @author Austin Miller 'akm8654'
 */
public class BoardView implements Iterable<Row>
{
  // The board itself
  private List<Row> board;
  private boolean flipped = false;

  public BoardView(Board boardActual)
  {
    this.board = makeCopyOfBoard(boardActual.getBoard());
  }

  public List<Row> makeCopyOfBoard(List<Row> board)
  {
    List<Row> newBoard = new ArrayList<>();
    for (int i = 0; i < DIMENSIONS; i++)
    {
      newBoard.add(new Row(board.get(i)));
    }
    return newBoard;
  }

  /**
   * Updates the board so that the other player is on the bottom.
   */
  public void flip()
  {
    //reverses the board along the x-axis (puts the red at the top and white on the bottom)
    //pieces are now in the white spaces
    Collections.reverse(board);

    //reverses the board along the y-axis(puts the pieces back in the black spaces)
    for (Row r : board)
    {
      Collections.reverse(r.getRow());
    }
    flipped = !flipped;
  }

  /**
   * Get's the abstract stuff.
   *
   * @return the list form of board.
   */
  public List<Row> getBoard()
  {
    return board;
  }

  @Override
  public Iterator<Row> iterator()
  {
    return board.iterator();
  }
}
