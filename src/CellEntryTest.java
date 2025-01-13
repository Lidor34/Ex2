
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CellEntryTest {

    @Test
    public void testParseCellCoordinates_ValidInput() {
        // Test a valid input string "A1"
        CellEntry cell = CellEntry.parseCellCoordinates("A1");
        assertNotNull(cell); // Ensure the result is not null
        assertEquals(0, cell.getX()); // Column 'A' -> x = 0
        assertEquals(1, cell.getY()); // Row '1' -> y = 1
    }

    @Test
    public void testParseCellCoordinates_InvalidInput_EmptyString() {
        // Test an invalid empty input string
        CellEntry cell = CellEntry.parseCellCoordinates("");
        assertNull(cell); // Ensure the result is null
    }

    @Test
    public void testParseCellCoordinates_InvalidInput_InvalidRow() {
        // Test an invalid input string with an invalid row (non-numeric)
        CellEntry cell = CellEntry.parseCellCoordinates("Aabc");
        assertNull(cell); // Ensure the result is null
    }

    @Test
    public void testParseCellCoordinates_InvalidInput_OutOfBounds() {
        // Test an out-of-bounds column (e.g., column 'Z' -> 25, but row '100' -> 100 is out of range)
        CellEntry cell = CellEntry.parseCellCoordinates("Z100");
        assertNull(cell); // Ensure the result is null (row out of bounds)
    }

    @Test
    public void testParseCellCoordinates_InvalidInput_ColumnTooLarge() {
        // Test an invalid column (e.g., more than one letter, e.g., "AA1")
        CellEntry cell = CellEntry.parseCellCoordinates("AA1");
        assertNull(cell); // Ensure the result is null (column out of bounds)
    }

    @Test
    public void testIsValid_ValidCoordinates() {
        // Test valid coordinates
        CellEntry cell = new CellEntry(0, 1); // Column 'A' and Row '1'
        assertTrue(cell.isValid()); // Should be valid (within bounds)
    }

    @Test
    public void testIsValid_InvalidCoordinates() {
        // Test invalid coordinates (out of bounds)
        CellEntry cell = new CellEntry(100, 1); // Invalid row '100'
        assertFalse(cell.isValid()); // Should not be valid (row out of bounds)
    }

    @Test
    public void testToString() {
        // Test the string representation of the cell
        CellEntry cell = new CellEntry(0, 1); // Column 'A' and Row '1'
        assertEquals("A1", cell.toString()); // Should return "A1"
    }

    @Test
    public void testEquals_SameCoordinates() {
        // Test if two CellEntry objects with the same coordinates are equal
        CellEntry cell1 = new CellEntry(0, 1); // "A1"
        CellEntry cell2 = new CellEntry(0, 1); // "A1"
        assertTrue(cell1.equals(cell2)); // Should be true
    }

    @Test
    public void testEquals_DifferentCoordinates() {
        // Test if two CellEntry objects with different coordinates are not equal
        CellEntry cell1 = new CellEntry(0, 1); // "A1"
        CellEntry cell2 = new CellEntry(1, 1); // "B1"
        assertFalse(cell1.equals(cell2)); // Should be false
    }

    @Test
    public void testEquals_DifferentObjectType() {
        // Test equality with a different object type
        CellEntry cell = new CellEntry(0, 1); // "A1"
        String str = "A1";
        assertFalse(cell.equals(str)); // Should be false (different object type)
    }
}
