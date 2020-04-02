package com.webcheckers.model;

import com.webcheckers.ui.PostValidateMoveRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The unit test for the {@link Move} componenet.
 *
 * @author Austin Miller 'akm8654'
 * @author Mikayla Wishart 'mcw7246'
 */
@Tag("Model-tier")
public class MoveTest
{

  /**
   * Component under test, a stateless component that tests the functionality
   * of the {@link Move}
   */
  private Move CuT;

  //Updated objects to represent start and ending indices.
  int startRow = 0;
  int startCell = 0;
  int endRow = 0;
  int endCell = 0;

  //Friendly Objects
  Position start;
  Position end;
  Piece piece;

  // Mocked objects.
  CheckerGame game;
  Board board;
  Space startSpace;
  Space endSpace;

  @BeforeEach
  public void setup()
  {
    game = mock(CheckerGame.class);
    board = mock(Board.class);
    startSpace = mock(Space.class);
    endSpace = mock(Space.class);
    piece = new Piece(Piece.Color.RED);
    when(game.getBoard()).thenReturn(board);
    when(board.getSpaceAt(startRow, startCell)).thenReturn(startSpace);
    when(board.getSpaceAt(endRow, endCell)).thenReturn(endSpace);
    when(startSpace.getPiece()).thenReturn(piece);
  }

  @Test
  public void testValidate_move_invalid_space()
  {
    start = new Position(1,1);
    end = new Position(1,2);
    CuT = new Move(start, end);
    assertFalse(endSpace.isValidSpace());
    }

  @Test
  public void testValidate_move_occupied()
  {
    start = new Position(1, 1);
    end = new Position(2,2);
    CuT = new Move(start, end);

    Space space = new Space(2, 2, true);
    space.setPiece(new Piece(Piece.Color.WHITE, Piece.Type.SINGLE));

    assertEquals(endSpace.isValidSpace(), false);


  }
}