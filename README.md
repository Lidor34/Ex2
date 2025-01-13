# Ex2 - Foundations of Object-Oriented and Recursion  
 

## Overview
This assignment focuses on designing a basic **Spreadsheet** with cells that can contain:
- **Text** (String)
- **Numbers** (Double)
- **Formulas** (e.g., `=A1+3`, `=(2+3)*2`)

We will implement a `Cell` class for individual cells and a `Spreadsheet` class for managing the entire 2D array of cells.

### Formula Types:
1. `=number` (e.g., `=1`, `=3.14`)
2. `=(Formula)` (e.g., `=(2+3)`)
3. `=Formula op Formula` (e.g., `=A1+2`, `=B2*3`)
4. `=cell` (e.g., `=A1`, `=C2`)

### Error Types:
- **ERR_CYCLE**: Cyclic dependency, e.g., `A0:A0`
- **ERR_WRONG_FORM**: Invalid formula format.

## Key Features:
1. **sCell Class**:
   - Methods for checking if a value is a number, text, or formula.
   - A method to compute the result of formulas.

2. **cellEntry Class**:
   - Create and manage a 2D array of cells.
   - Methods for setting/getting cells, evaluating formulas, and detecting cycles.

### Example:
```java
Spreadsheet sheet = new Spreadsheet(5, 5);
sheet.set(0, 0, new SCell("=5"));
sheet.set(1, 0, new SCell("=A0+3"));
System.out.println(sheet.eval(1, 0)); // Should output 8
