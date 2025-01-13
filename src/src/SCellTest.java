import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashSet;

import static org.junit.Assert.*;

public class SCellTest {

    @Test
    public void testIsNumber() {
        SCell cell = new SCell("1.2");
        assertTrue(cell.isNumber("1.2"));
        assertFalse(cell.isNumber("A1"));
    }

    @Test
    public void testIsText() {
        SCell cell = new SCell("Hello");
        assertTrue(cell.isText("Hello"));
        assertFalse(cell.isText("1.2"));
    }



    @Test
    public void testEvaluateCellFormula() throws CycleException {
        // Assuming Ex2Sheet and other dependencies are properly set up
        Ex2Sheet sheet = new Ex2Sheet(3, 3);  // Example initialization of the sheet
        SCell cell = new SCell("=A0+B1");
        sheet.set(0, 0, String.valueOf(new SCell("2")));
        sheet.set(1, 1, String.valueOf(new SCell("3")));
        String result = cell.evaluateCellFormula(sheet);
        assertEquals("5.0", result); // Assuming 2+3 = 5
    }



    @Test
    public void testareParenthesesBalanced() {
        SCell cell = new SCell("(1+2)");
        assertTrue(cell.areParenthesesBalanced("(1+2)"));
        assertFalse(cell.areParenthesesBalanced("(1+2"));
    }

    @Test
    public void testFindMainOp() {
        SCell cell = new SCell("1+2*3");
        int index = cell.findMainOp("1+2*3", "+");
        assertEquals(1, index);
    }

    @Test
    public void testcalculateOrder() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        SCell cell = new SCell("=A0+B1");
        sheet.set(0, 0, String.valueOf(new SCell("3")));
        sheet.set(1, 1, String.valueOf(new SCell("4")));
        int order = cell.calculateOrder("=A0+B1", sheet);
        assertTrue(order > 0); // Order should be greater than 0
    }

    @Test
    public void testEquals() {
        SCell cell1 = new SCell("1.0");
        SCell cell2 = new SCell("1.0");
        assertTrue(cell1.equals(cell2));  // Same values should be equal
    }
}
