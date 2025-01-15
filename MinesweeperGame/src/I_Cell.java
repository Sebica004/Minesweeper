public interface I_Cell {
    public int getRow();
    public int getCol();
    public boolean isMine();
    public void setMine(boolean mine);
    public boolean isFlagged();
    public void setFlagged(boolean flagged);
    public boolean isRevealed();
    public void reveal();
    public int getNeighboringMines();
    public void setNeighboringMines(int neighboringMines);
    public boolean isSafe();
    public void setSafe(boolean safe);

}
