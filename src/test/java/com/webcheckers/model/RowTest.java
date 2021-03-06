package com.webcheckers.model;

import com.webcheckers.application.PlayerLobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
/**
 * The unit test suite for the {@link Row} component.
 *
 * @author Zehra Amena Baig 'zab1166'
 */
@Tag("Model-tier")
public class RowTest
{
    private static final Piece WHITE = new Piece(Piece.Color.WHITE, Piece.Type.SINGLE);

    @Test
    public void testInitializeTopRow()
    {
        /**
         * The component under test.
         * <p>
         * Stateless component that actually test the functionality of the
         * {@link Row}
         */
        int rowInd = 0;
        Row CuT = new Row(rowInd);
        ArrayList<Space> spaces = CuT.getRow();
        assertEquals(new Space(rowInd, 0, false), spaces.get(0));
        assertEquals(new Space(rowInd, 1, true, WHITE), spaces.get(1));
        assertEquals(new Space(rowInd, 2, false), spaces.get(2));
        assertEquals(new Space(rowInd, 3, true, WHITE), spaces.get(3));
        assertEquals(new Space(rowInd, 4, false), spaces.get(4));
        assertEquals(new Space(rowInd, 5, true, WHITE), spaces.get(5));
        assertEquals(new Space(rowInd, 6, false), spaces.get(6));
        assertEquals(new Space(rowInd, 7, true, WHITE), spaces.get(7));
    }

    @Test
    public void testForNull()
    {
        Row CuT = new Row(0);
        assertNotNull(CuT);
    }

    @Test
    public void testIndex()
    {
        Row CuT = new Row(0);
        assertEquals(0, CuT.getIndex());
    }

    @Test
    public void testValidSpaceAtIndex()
    {
        Row CuT = new Row(0);
        ArrayList<Space> spaces = CuT.getRow();
        assertFalse(spaces.get(2).isValidSpace());
        assertTrue(spaces.get(7).isValidSpace());
    }

}
