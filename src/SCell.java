import java.net.MalformedURLException;
import java.util.*;

public class SCell implements Cell {
    private String line;
    private String cellData;
    private int type;
    private int order;

    // Constructor: Initializes the cell with the given string and sets the data
    public SCell(String s) {
        cellData = s;
        setData(s);
    }

    // Checks if the text is a valid string (not a number or a formula)
    public boolean isText(String text) {
        return !isNumber(text) && !isFormula(text);
    }

    // Checks if the text is a valid formula (starts with '=')
    public boolean isFormula(String text) {
        return text != null && text.startsWith("=") && text.length() > 1;
    }

    // Evaluates the formula of the cell using the provided sheet
    public String evaluateCellFormula(Ex2Sheet sheet) throws CycleException {
        return evaluateCellFormula(sheet, new HashSet<>());
    }

    // Evaluates the formula of the cell with cycle detection to avoid circular references
    public String evaluateCellFormula(Ex2Sheet sheet, Set<String> visited) throws CycleException {
        if (type != Ex2Utils.FORM) {
            return line; // Only evaluate formulas
        }

        if (visited == null) {
            visited = new HashSet<>();
        }

        // Use the current cell as the entry point
        CellEntry thisCell = CellEntry.parseCellCoordinates(sheet.findCord(this));
        if (thisCell != null && visited.contains(thisCell.toString())) {
            type = Ex2Utils.ERR_CYCLE_FORM; // Cycle detected
            throw new CycleException("Cycle detected in formula for cell: " + thisCell);
        }
        if (thisCell != null) {
            visited.add(thisCell.toString());
        }

        String formula = getData().startsWith("=") ? getData().substring(1) : getData(); // Remove "="
        String result;

        try {
            result = String.valueOf(evaluateCellFormula(formula, sheet, visited));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        order = calculateOrder(formula, sheet); // Update order based on dependencies

        if (thisCell != null) {
            visited.remove(thisCell.toString()); // Remove from visited after evaluation
        }

        line = result; // Update cell data with the evaluated result
        return result;
    }

    // Checks if the text is a valid number
    public boolean isNumber(String text) {
        try {
            Double d = Double.parseDouble(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Determines the type of the data: Formula, Number, or Text
    private int determineDataType(String data) {
        if (this.isFormula(data)) {
            return Ex2Utils.FORM;
        } else if (this.isNumber(data)) {
            return Ex2Utils.NUMBER;
        } else {
            return Ex2Utils.TEXT;
        }
    }

    // Returns the order of the cell
    @Override
    public int getOrder() {
        return order;
    }

    // Overrides equals to compare cells based on their string representation
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    // Returns a string representation of the cell's data
    @Override
    public String toString() {
        return getData();
    }

    // Sets the data of the cell
    @Override
    public void setData(String s) {
        if (isNumber(s)) {
            line = "" + Double.parseDouble(s);
        } else {
            line = s;
        }
        if (type != Ex2Utils.FORM) {
            setType(determineDataType(s));
        }
    }

    // Returns the data of the cell
    @Override
    public String getData() {
        return cellData;
    }

    // Returns the type of the cell
    @Override
    public int getType() {
        return type;
    }

    // Sets the type of the cell
    @Override
    public void setType(int t) {
        type = t;
    }

    // Sets the order of the cell
    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    // Retrieves the result of the formula for the cell
    public String retrieveFormulaResult() {
        return line;
    }

    // Parses and evaluates the formula recursively
    double evaluateCellFormula(String formula, Ex2Sheet sheet, Set<String> visited) throws CycleException, MalformedURLException {
        formula = formula.trim();

        // Base cases: number or cell reference
        if (isNumericValue(formula)) { // Numeric literal
            return Double.parseDouble(formula);
        }
        if (isValidCellReference(formula)) { // Cell reference
            CellEntry ref = CellEntry.parseCellCoordinates(formula);
            if (ref == null || !sheet.isIn(ref.getX(), ref.getY())) {
                throw new MalformedURLException("Invalid cell reference: " + formula);
            }

            Cell refCell = sheet.get(ref.getX(), ref.getY());
            if (refCell.getType() == Ex2Utils.NUMBER) {
                return Double.parseDouble(refCell.getData());
            } else if (refCell.getType() == Ex2Utils.FORM) {
                return Double.parseDouble(((SCell) refCell).evaluateCellFormula(sheet, visited));
            } else if (refCell.getType() == Ex2Utils.ERR_CYCLE_FORM || Objects.equals(((SCell) refCell).retrieveFormulaResult(), Ex2Utils.ERR_CYCLE)) {
                throw new CycleException("Recursive cell reference: " + formula);
            } else if (refCell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
                throw new MalformedURLException("Invalid or unresolved cell reference: " + formula);
            } else {
                //TODO: Handle string
            }
        }

        // Recursive parsing: parentheses
        if (hasParentheses(formula) && areParenthesesBalanced(formula.substring(1, formula.length() - 1))) {
            return evaluateCellFormula(formula.substring(1, formula.length() - 1), sheet, visited);
        }

        // Recursive parsing: binary operations
        String[] operators = Ex2Utils.M_OPS;
        for (String op : operators) {
            int index = findMainOp(formula, op);
            if (index != -1) {
                String left = formula.substring(0, index).trim();
                String right = formula.substring(index + 1).trim();
                double leftVal = evaluateCellFormula(left, sheet, visited);
                double rightVal = evaluateCellFormula(right, sheet, visited);
                switch (op) {
                    case "+":
                        return leftVal + rightVal;
                    case "-":
                        return leftVal - rightVal;
                    case "*":
                        return leftVal * rightVal;
                    case "/":
                        if (rightVal == 0) {
                            throw new IllegalArgumentException("Division by zero");
                        }
                        return leftVal / rightVal;
                }
            }
        }

        throw new IllegalArgumentException("Invalid formula syntax: " + formula);
    }




    // Checks if the string is enclosed in parentheses
    boolean hasParentheses(String str) {
        return str.startsWith("(") && str.endsWith(")");
    }

    // Checks if the parentheses in the string are balanced
    boolean areParenthesesBalanced(String string) {
        int balanced = 0;
        for (char c : string.toCharArray()) {
            if (c == '(') balanced++;
            if (c == ')') balanced--;
            if (balanced < 0) return false; // Mismatched closing parenthesis
        }
        return balanced == 0; // True if parentheses are balanced
    }



    // Finds the main operator in the formula (ignores operators within parentheses)
    int findMainOp(String formula, String operator) {
        int level = 0;
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if (c == '(') level++;
            if (c == ')') level--;
            if (level == 0 && formula.startsWith(operator, i)) {
                return i;
            }
        }
        return -1;
    }





    // Checks if the string is a valid numeric literal
    boolean isNumericValue(String string) {
        if (string == null || string.isEmpty()) return false;
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }





    // Computes the order of the formula based on dependencies (e.g., cell references)
    int calculateOrder(String formula, Ex2Sheet sheet) {
        int maxOrder = 0;

        // Extract potential cell references
        String[] tokens = splitFormulaIntoTokens(formula);
        for (String token : tokens) {
            if (isValidCellReference(token)) {
                CellEntry ref = CellEntry.parseCellCoordinates(token);
                if (sheet.isIn(ref.getX(), ref.getY())) {
                    Cell refCell = sheet.get(ref.getX(), ref.getY());
                    maxOrder = Math.max(maxOrder, refCell.getOrder());
                }
            }
        }return maxOrder + 1; // Order is 1 + max dependency order
    }






    // Checks if the string is a valid cell reference (e.g., "A1")
    boolean isValidCellReference(String str) {
        if (str == null || str.isEmpty()) return false;
        char firstChar = str.charAt(0);
        if (!Character.isLetter(firstChar)) return false;

        for (int i = 1; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }





    // Tokenizes the formula into its component strings (e.g., numbers, operators, etc.)
    String[] splitFormulaIntoTokens(String form) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (char c : form.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                current.append(c);
            } else {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                if (!Character.isWhitespace(c)) {
                    tokens.add(String.valueOf(c));
                }
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens.toArray(new String[0]);
    }






    // A map to track dependencies for each cell
    private Map<CellEntry, Set<CellEntry>> cellDependencies = new HashMap<>();


    //Tracks a dependency between two cells: dependentCell depends on referencedCell.
    public void addDependency(CellEntry dependentCell, CellEntry referencedCell) {
        cellDependencies.computeIfAbsent(dependentCell, k -> new HashSet<>()).add(referencedCell);
    }


     //Returns the set of cells that depend on the given cell.
    public Set<CellEntry> getDependents(CellEntry cell) {
        Set<CellEntry> dependents = new HashSet<>();
        for (Map.Entry<CellEntry, Set<CellEntry>> entry : cellDependencies.entrySet()) {
            if (entry.getValue().contains(cell)) {
                dependents.add(entry.getKey());
            }
        }
        return dependents;
    }




     //Checks if the syntax of the formula is valid.
    private boolean isValidFormulaSyntax(String formula) {
        // Check for valid formula syntax here (can be extended)
        return formula.matches("[A-Za-z0-9()+\\-*/\\^ ]+");
    }









}
