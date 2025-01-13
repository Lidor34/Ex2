import java.io.*;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private Cell[][] table;



    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    /**
     * Returns the value of a cell at the specified coordinates.
     * @param x The row index.
     * @param y The column index.
     * @return The value of the cell, or an empty cell if out of bounds or uninitialized.
     */
    @Override
    public String value(int x, int y) {
        // Check if the coordinates are valid
        if (!isIn(x, y)) {
            return Ex2Utils.EMPTY_CELL;
        }

        // Get the cell at the specified coordinates
        Cell c = get(x, y);

        // Return empty cell if the cell is uninitialized
        if (c == null) {
            return Ex2Utils.EMPTY_CELL;
        }

        // Switch case to handle different cell types
        String cellValue = Ex2Utils.EMPTY_CELL; // Default value

        switch (c.getType()) {
            case Ex2Utils.TEXT:
                // For TEXT type, return the shown value
                cellValue = ((SCell)c).retrieveFormulaResult();
                break;

            case Ex2Utils.NUMBER:
                // For NUMBER type, parse the number and convert to string
                double d = Double.parseDouble(c.getData());
               return  ""+d;

            case Ex2Utils.FORM:
                try {
                    // For FORM type, evaluate the formula and return the result
                    SCell sCell = (SCell) c;

                    return sCell.evaluateCellFormula(this);

                } catch (StackOverflowError e) {
                    // Handle formula evaluation errors related to cycles
                    c.setType(Ex2Utils.ERR_CYCLE_FORM);
                    cellValue = Ex2Utils.ERR_CYCLE;
                } catch (IllegalArgumentException e) {
                    // Handle invalid formula formats
                    c.setType(Ex2Utils.ERR_FORM_FORMAT);
                    cellValue = Ex2Utils.ERR_FORM;
                } catch (CycleException e) {

                }


            case Ex2Utils.ERR_CYCLE_FORM:
                // Return the cycle error value
                cellValue = Ex2Utils.ERR_CYCLE;
                break;

            case Ex2Utils.ERR_FORM_FORMAT:
                // Return the format error value
                cellValue = Ex2Utils.ERR_FORM;
                break;
        }

        return cellValue;
    }


    @Override
    public Cell get(int x, int y) {
        return isIn(x , y) ? table[x][y]: null;
    }

    @Override
    public Cell get(String cords) {
       CellEntry cellentry = CellEntry.parseCellCoordinates(cords);
        if (cellentry == null || !cellentry.isValid()){
            return null;
        } else {
            return get(cellentry.getX() , cellentry.getY());
        }
    }

    /**
     * Finds the coordinates of a given cell within the spreadsheet.
     *
     * The function iterates through all cells in the spreadsheet and checks if
     * the provided cell matches any of the cells in the spreadsheet using the `equals` method.
     * If a match is found, it returns the coordinates of the matching cell as a string in the format of "A1".
     * If no match is found, it returns null.
     */
    public String findCord(Cell cell) {
        // Iterate over all rows (x) in the spreadsheet
        for (int x = 0; x < width(); x++) {
            // Iterate over all columns (y) in the spreadsheet
            for (int y = 0; y < height(); y++) {
                // Retrieve the cell at position (x, y)
                Cell cell1 = get(x, y);

                // Check if the cell is not null and if it equals the input cell
                if (cell1 != null && cell1.equals(cell)) {
                    // Return the coordinates of the matching cell as a string
                    return new CellEntry(x, y).toString();
                }
            }
        }

        // If no matching cell was found, return null
        return null;
    }

    @Override
    public int width() {
        return table.length;
    }
    @Override
    public int height() {
        return table[0].length;
    }



     // Updates the cell at position (x, y) with the value provided in the string s.
     // After updating the cell, it recalculates all cells in the spreadsheet to reflect changes.
    @Override
    public void set(int x, int y, String s) {
        // If the (x, y) coordinate is outside the boundaries of the spreadsheet, do nothing
        if (!isIn(x, y)) return;

        // Create a new SCell object with the provided string value and assign it to the cell at (x, y)
        table[x][y] = new SCell(s);

        // Call eval to recalculate all cells and update their values based on the new input
        this.eval();
    }

    @Override
    public void eval() {
        // Create an array to store the evaluated values of cells
        String[][] evaluatedValues = new String[width()][height()];

        // Iterate through each cell in the spreadsheet
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                // Get the current cell
                Cell cell = get(x, y);

                // If the cell is not null and it is a formula, evaluate it
                if (cell != null && cell.getType() == Ex2Utils.FORM) {
                    try {
                        // Try to evaluate the formula and store the result
                        String result = evaluateFormula(x, y);
                        evaluatedValues[x][y] = result;

                        // Set the evaluated result in the cell
                        cell.setData(result);
                    } catch (StackOverflowError | CycleException e) {
                        // Handle circular dependency by setting the cell type to error
                        cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                        evaluatedValues[x][y] = Ex2Utils.ERR_CYCLE;
                    } catch (Exception e) {
                        // Handle other exceptions (e.g., incorrect formula format)
                        cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                        evaluatedValues[x][y] = Ex2Utils.ERR_FORM;
                    }
                }
            }





        }
    }


    // Helper method to evaluate formulas for cells
    private String evaluateFormula(int x, int y) throws StackOverflowError, CycleException {
        Cell cell = get(x, y);

        // Check if the cell is a formula type
        if (cell != null && cell.getType() == Ex2Utils.FORM) {
            String formula = cell.getData(); // Get the formula as a string

            // Process the formula and calculate its value (simplified version)
            // Assuming the formula is in the form of a basic mathematical expression
            // You could expand this to support more complex formulas, like references to other cells

            String result = processFormula(formula, x, y);

            // Return the result of the formula evaluation
            return result;
        }

        // If the cell is not a formula, just return its current data (if any)
        return cell != null ? cell.getData() : Ex2Utils.EMPTY_CELL;
    }

    // Example method to process a formula (simplified for demonstration)
    private String processFormula(String formula, int x, int y) throws StackOverflowError, CycleException {
        // Here you would implement the logic to parse and evaluate the formula
        // For example, evaluating simple arithmetic expressions or references to other cells

        // In this example, we assume the formula might reference other cells (e.g., "=A1+B1")
        // You need to write the logic to handle this. For now, just returning the formula as is.

        if (formula.contains("A1")) { // Example of simple formula processing
            // This is a basic example, expand this based on your use case
            return "Calculated Result for " + formula;
        }

        // If you detect a cycle, throw a CycleException (or handle accordingly)
        if (isCyclicFormula(formula, x, y)) {
            throw new CycleException("Circular dependency detected!");
        }

        // If there's any other error, throw an appropriate exception
        return formula; // Placeholder, replace with actual calculation logic
    }

    // A simple check for cyclic formulas (you can expand this logic)
    private boolean isCyclicFormula(String formula, int x, int y) {
        // Check if the formula references a cell that has already been evaluated
        // and leads to a circular dependency.
        return false; // For simplicity, this is just a placeholder
    }



    @Override
    public boolean isIn(int xx, int yy) {
        // Checks if the coordinates xx and yy are within the valid range of the spreadsheet's dimensions
        return (xx >= 0 && yy >= 0) && (xx < width() && yy < height());
    }

    // This function calculates the dependency of each cell in the sheet and returns a
// 2D array where each cell represents the dependency order of the corresponding cell at the same position in the sheet.
    @Override
    public int[][] depth() {
        // Initialize a 2D array to hold the dependency order for each cell.
        // The dimensions of the array are the same as the sheet.
        int[][] ans = new int[width()][height()];

        // Iterate over all the rows (x-coordinate) of the spreadsheet
        for (int x = 0; x < width(); x++) {

            // Iterate over all the columns (y-coordinate) of the spreadsheet
            for (int y = 0; y < height(); y++) {

                // Get the cell at position (x, y) in the spreadsheet
                Cell cellentry = get(x, y);

                // Check if the cell is not null.
                if (cellentry != null) {

                    // Set the value in the 'ans' array at the corresponding position
                    // This value represents the dependency order of the cell.
                    // The 'getOrder' method presumably returns the dependency level of the cell.
                    ans[x][y] = cellentry.getOrder();
                }
            }
        }

        // Return the 2D array that holds the dependency order of all cells.
        return ans;
    }

    @Override
    public void load(String fileName) throws IOException {
        // Open the file using BufferedReader inside a try-with-resources block to ensure it closes automatically
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;

            // Loop through each line of the file until we reach the end of the file or the height limit
            while ((line = br.readLine()) != null && row < height()) {
                // Split the line by commas
                String[] cells = line.split(",");

                // Loop through each cell in the line and set the values in the sheet
                for (int col = 0; col < cells.length && col < width(); col++) {
                    set(row, col, cells[col].trim()); // Trim to remove any leading/trailing spaces from each cell value
                }

                row++; // Move to the next row
            }
        }
    }


    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            //  Write the data for each non-empty cell in the spreadsheet.
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    Cell cell = get(x, y); // Get the cell at the position (x, y)

                    if (cell != null && !cell.toString().isEmpty()) {
                        // Only write non-empty cells, in the format: x,y,data
                        writer.write(x + "," + y + "," + cell.toString() + "\n");
                    }
                }
            }
        }
    }


    @Override
    public String eval(int x, int y) {
        return value(x , y);
        }
}
