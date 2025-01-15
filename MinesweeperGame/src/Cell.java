public  class Cell implements I_Cell{
    private int row;
    private int col;
    private boolean isMine;
    private boolean isFlagged;
    private boolean isRevealed;
    private int neighboringMines;
    private boolean isSafe;
    private final int x;
    private final int y;


    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.row = row;
        this.col = col;
        this.isMine = false;
        this.isFlagged = false;
        this.isRevealed = false;
        this.neighboringMines = 0;
        this.isSafe = false;
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Getters for row and column~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    @Override
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Getter & Setter for isMine~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    @Override
    public boolean isMine() {
        return isMine;
    }
    public void setMine(boolean mine) {
        isMine = mine;
    }


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Getter & Setter for isFlagged~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    @Override
    public boolean isFlagged() {
        return isFlagged;
    }
    @Override
    public void setFlagged(boolean flagged) {
        if(!isRevealed) {
            isFlagged = flagged;//can t flag an already revealed cell
        }
    }


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Getter & Setter for isRevealed~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    @Override
    public boolean isRevealed() {
        return isRevealed;
    }
    @Override
    public void reveal() {
        if(!isFlagged) {
            isRevealed = true;//flagged cells shouldn t be revealed
        }
    }


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Getter & Setter for neighbouringMine~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    @Override
    public int getNeighboringMines() {
        return neighboringMines;
    }
    @Override
    public void setNeighboringMines(int neighboringMines) {
        this.neighboringMines = neighboringMines;
    }


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Getter & Setter for isSafe~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    @Override
    public boolean isSafe() {
        return isSafe;
    }
    @Override
    public void setSafe(boolean safe) {
        isSafe = safe;
    }
   // public abstract String display();
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
}

