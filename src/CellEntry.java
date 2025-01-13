// Add your documentation below:

public class CellEntry  implements Index2D {
    private int y ;
    private int x ;




    public CellEntry(int x , int y){
        this.y = y ;
        this.x = x;

    }

    // This function takes a string as input and converts it to the corresponding cell coordinates (x, y).
    public static CellEntry parseCellCoordinates(String index) {
        // Check if the input string is null or its length is less than 2 (invalid input)
        if (index == null || index.length() < 2) return null;

        // Extract the first character (assumed to represent the row letter)
        char rowLetter = index.charAt(0);

        int col;
        try {
            // Try to parse the rest of the string as an integer (representing the column number)
            col = Integer.parseInt(index.substring(1));
        } catch (NumberFormatException e) {
            // If parsing fails (invalid column number), return null
            return null;
        }

        // Convert the row letter (e.g., 'A', 'B', etc.) to a row index (0-based)
        int row = Character.toUpperCase(rowLetter) - 'A';

        // Check if the row and column are valid using the isValid function
        if(!isValid(row , col)) return null;

        // Return a new CellEntry object with the calculated row and column
        return new CellEntry(row, col);
    }


    public void increment() {
        this.x++;
        this.y++;
    }



    public char getRowLetter() {
        return (char) ('A' + x);
    }


    public String toIndexString() {
        return getRowLetter() + Integer.toString(y);
    }



    public void setCoordinates(int x, int y) {
        if (isValid(x, y)) {
            this.x = x;
            this.y = y;
        } else {
            throw new IllegalArgumentException("Invalid coordinates");
        }
    }






    // x is the row and y is the col.
    private static boolean isValid(int x , int y){
      // return the range is valid.
            return x >= 0 && x < 100 && y >= 0 && y < 26;
    }

    @Override
    public boolean isValid() {
        return isValid(x , y);
    }
    // get  x and y
    @Override
    public int getX() {return x;}
    @Override
    public int getY() {return y;}

    // Returns a string representation of the cellentry
    @Override
    public String toString(){
        char col = (char) ('A' + x ); // converting row index.
        return col + String.valueOf(y);// converting col index.
    }


    // this function compares the cellentry to another obj  for equality and
    // telling if it's true or false.
    @Override
    public boolean equals( Object k){
        if(!(k instanceof CellEntry)){
            return false;
        }else {
            if(k == this ){
                return true;
            }
        }

        return this.toString().equals(k.toString());
    }





}
