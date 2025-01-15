import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Board {
    private List<Cell> revealedCells = new ArrayList<Cell>();
    private int rows;
    private int cols;
    private int totalMines;
    private Cell[][] cells;
    private int startX;
    private int startY;
    private final Random random = new Random();

    public Board(int rows, int columns, int totalMines) {
        this.rows = rows;
        this.cols = columns;
        this.totalMines = totalMines;
        this.cells = new Cell[rows][columns];
        initializeBoard();
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~functii pentru setare Board~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//


    private void initializeBoard() {

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                cells[y][x] = new Cell(x, y);
            }
        }

        this.startX = random.nextInt(cols);
        this.startY = random.nextInt(rows);

        reserveStartingArea(startX, startY);
        placeRandomMines(totalMines);
        calculateNeighboringMines();
    }

    private void reserveStartingArea(int startX, int startY) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = startX + dx;
                int ny = startY + dy;

                if (isInBounds(nx, ny)) {
                    cells[nx][ny].setSafe(true);
                }
            }
        }
    }

    private void placeRandomMines(int numMines) {
        while (numMines > 0) {
            int x = random.nextInt(cols);
            int y = random.nextInt(rows);

            if (!cells[y][x].isMine() && !cells[y][x].isSafe()) {
                cells[y][x].setMine(true);
                numMines--;
            }
        }
    }



    private void calculateNeighboringMines() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int neighboringMines = 0;

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;

                        if (isValidCell(nx, ny) && cells[ny][nx].isMine()) {
                            neighboringMines++;
                        }
                    }
                }

                cells[y][x].setNeighboringMines(neighboringMines);
            }
        }
    }



//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Logica de reveal~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//


    public boolean revealCell(int row, int col) {
        if (!isValidCell(row, col)) {
            System.out.println("Invalid cell position: (" + row + ", " + col + ")");
            return false;
        }

        Cell cell = cells[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            System.out.println("Cell already revealed or flagged: (" + row + ", " + col + ")");
            return false;
        }

        cell.reveal();
        revealedCells.add(cell);
        System.out.println("Revealed cell: (" + row + ", " + col + ")");

        if (cell.isMine()) {
            System.out.println("BOOM! You hit a mine at (" + row + ", " + col + ").");
            return true;
        }

        if (cell.getNeighboringMines() == 0) {
            cascadeReveal(row, col);
        }

        return false;
    }

    public List<Cell> getRevealedCells() {
        return revealedCells;
    }

    private void cascadeReveal(int row, int col) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];

            if (isValidCell(newRow, newCol)) {
                Cell neighbor = cells[newRow][newCol];
                if (!neighbor.isRevealed() && !neighbor.isMine() && !neighbor.isFlagged()) {
                    neighbor.reveal();
                    if (neighbor.getNeighboringMines() == 0) {
                        cascadeReveal(newRow, newCol);
                    }
                }
            }
        }
    }


    public void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells[i][j].isMine()) {
                    System.out.print("M ");
                } else if (cells[i][j].isSafe()) {
                    System.out.print("S ");
                } else {
                    System.out.print(cells[i][j].getNeighboringMines() + " ");
                }
            }
            System.out.println();
        }
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Win manager~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public boolean isWin() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                if (!cell.isMine() && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Functii pentru interactionat cu Boardul~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    public void toggleFlag(int row, int col) {
        if (!isValidCell(row, col)) {
            System.out.println("Invalid cell position!");
            return;
        }
        Cell cell = cells[row][col];
        if (cell.isRevealed()) {
            System.out.println("Cannot flag a revealed cell!");
        } else {
            cell.setFlagged(!cell.isFlagged());
            System.out.println("Cell (" + row + ", " + col + ") " +
                    (cell.isFlagged() ? "flagged." : "unflagged."));
        }
    }



    public void revealSafeNeighbors(int x, int y) {

        int flaggedNeighbors = countFlaggedNeighbors(x, y);
        int neighboringMines = cells[x][y].getNeighboringMines();

        if (flaggedNeighbors == neighboringMines) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx;
                    int ny = y + dy;

                    if (isInBounds(nx, ny)) {
                        Cell neighbor = cells[nx][ny];

                        if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                            revealCell(nx, ny);

                        }
                    }
                }
            }
        }
    }

    public int countFlaggedNeighbors(int row, int col) {
        int count = 0;
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < dx.length; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];
            if (isValidCell(newRow, newCol) && cells[newRow][newCol].isFlagged()) {
                count++;
            }
        }

        return count;
    }


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Helpers (in mare parte getteri mici)~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

    public Cell getCell(int row, int col) {
        if (isValidCell(row, col)) {
            return cells[row][col];
        } else {
            throw new IndexOutOfBoundsException("Cell position out of bounds!");
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return cols;
    }
    public int getTotalMines() {
        return totalMines;
    }
    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }
    public Cell[][] getCells() {
        return cells;
    }




    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }




    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~sort celule revealed~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
public void sortRevealedCells() {
    revealedCells.sort((c1, c2) -> {
        if (c1.getRow() != c2.getRow()) {
            return Integer.compare(c1.getRow(), c2.getRow());
        } else {
            return Integer.compare(c1.getCol(), c2.getCol());
        }
    });
    System.out.println("Revealed cells sorted: " + revealedCells);
}

}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~The Place Of Useless Functions~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

/*

    public int countUnopenedNeighbors(int row, int col) {
        int count = 0;
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < dx.length; i++) {
            int newRow = row + dx[i];
            int newCol = col + dy[i];
            if (isValidCell(newRow, newCol) && !cells[newRow][newCol].isRevealed()) {
                count++;
            }
        }

        return count;
    }


}

    private int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;

                if (isInBounds(nx, ny) && cells[nx][ny].isMine()) {
                    count++;
                }
            }
        }
        return count;
    }

public boolean flagMineNeighbors(int x, int y) {
        boolean progress = false;

        int unopenedNeighbors = countUnopenedNeighbors(x, y);
        int neighboringMines = cells[x][y].getNeighboringMines();

        if (unopenedNeighbors == neighboringMines) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx;
                    int ny = y + dy;

                    if (isInBounds(nx, ny)) {
                        Cell neighbor = cells[nx][ny];

                        if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                            neighbor.setFlagged(true);
                            progress = true;
                        }
                    }
                }
            }
        }

        return progress;
    }


private boolean isSolvable() {
        boolean[][] visited = new boolean[rows][cols];
        visited[startX][startY] = true; // Start from the safe position

        // Begin solving from the safe position
        return solveBoard();
    }


    public boolean solveBoard() {
        // Add debug print to ensure the method is being called correctly
        System.out.println("Entering solveBoard...");

        // Variable to track whether progress is made in this iteration
        boolean progress;

        // Repeat until no progress is made
        do {
            progress = false;
            cells[0][0].reveal();
            cascadeReveal(0, 0);
            // Iterate over all cells in the board
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    Cell cell = cells[row][col];

                    // Debug output to verify the conditions for analyzing a cell
                    System.out.println("Checking cell (" + row + ", " + col + ")");
                    System.out.println("Is revealed: " + cell.isRevealed());
                    System.out.println("Neighboring mines: " + cell.getNeighboringMines());

                    // Analyze only cells that are revealed and have neighboring mines
                    if (cell.isRevealed() && cell.getNeighboringMines() > 0) {
                        // Get the list of unopened neighboring cells
                        List<Cell> neighbors = getUnopenedNeighbors(row, col);

                        // Count the number of flagged neighboring cells
                        int flaggedMines = countFlaggedNeighbors(row, col);

                        // Debug output to monitor the cell being analyzed
                        System.out.println("Analyzing cell (" + row + ", " + col + ")");
                        System.out.println("Neighbors: " + neighbors.size());
                        System.out.println("Flagged Mines: " + flaggedMines);

                        // Calculate remaining mines based on flagged cells
                        int remainingMines = cell.getNeighboringMines() - flaggedMines;
                        System.out.println("Remaining Mines: " + remainingMines);

                        // Rule 1: If all neighboring mines are flagged, mark all remaining neighbors as safe
                        if (flaggedMines == cell.getNeighboringMines()) {
                            for (Cell neighbor : neighbors) {
                                if (!neighbor.isFlagged() && !neighbor.isRevealed()) {
                                    neighbor.setSafe(true); // Mark the cell as safe
                                    System.out.println("Marking cell (" + neighbor.getRow() + ", " + neighbor.getCol() + ") as safe.");
                                    progress = true; // Indicate that progress was made
                                }
                            }
                        }

                        // Rule 2: If the remaining unopened neighbors match the number of remaining mines, flag them as mines
                        if (remainingMines == neighbors.size()) {
                            for (Cell neighbor : neighbors) {
                                if (!neighbor.isFlagged() && !neighbor.isRevealed()) {
                                    neighbor.setMine(true); // Flag the cell as a mine
                                    System.out.println("Flagging cell (" + neighbor.getRow() + ", " + neighbor.getCol() + ") as a mine.");
                                    progress = true; // Indicate that progress was made
                                }
                            }
                        }
                    }
                }
            }

        } while (progress); // Continue looping until no progress is made

        // Add debug statement to check why the method might be failing
        if (!progress && hasUnopenedCells()) {
            System.out.println("Solver could not make progress, and there are still unopened cells.");
            return false; // Return false since guessing is not allowed
        }

        // Final verification to ensure all mines are flagged and safe cells revealed
        return allMinesFlagged() && allSafeCellsRevealed();
    }

private boolean canRevealCell(int row, int col) {
        if (!isValidCell(row, col)) {
            return false; // Ensure the cell is within bounds
        }

        Cell cell = cells[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return false; // Already revealed or flagged
        }

        // Rule 1: If all mines are flagged, the remaining neighbors are safe
        if (countFlaggedNeighbors(row, col) == cell.getNeighboringMines()) {
            return true; // Safe to reveal this cell
        }

        // Rule 2: If all safe neighbors are revealed, remaining neighbors are mines
        if (countUnopenedNeighbors(row, col) ==
                cell.getNeighboringMines() - countFlaggedNeighbors(row, col)) {
            return false; // The cell is not safe
        }

        // Optional: Rule 3: Add pattern recognition (e.g., 1-2-1)
        if (isOneTwoOnePattern(row, col)) {
            return true; // Pattern-based deduction
        }

        return false; // If no deduction applies, guessing would be required
    }

private boolean isOneTwoOnePattern(int row, int col) {
    // Example: Check horizontal 1-2-1 pattern
    if (isValidCell(row, col - 1) && isValidCell(row, col) && isValidCell(row, col + 1)) {
        int left = cells[row][col - 1].getNeighboringMines();
        int middle = cells[row][col].getNeighboringMines();
        int right = cells[row][col + 1].getNeighboringMines();

        return left == 1 && middle == 2 && right == 1;
    }

    // Add similar checks for vertical and diagonal patterns if needed
    return false;
}


    public boolean needsGuessing() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];

                if (!cell.isRevealed() && !cell.isFlagged()) {
                    int flaggedNeighbors = countFlaggedNeighbors(i, j);
                    int unopenedNeighbors = countUnopenedNeighbors(i, j);

                    if (flaggedNeighbors < cell.getNeighboringMines() &&
                            unopenedNeighbors > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean allMinesFlagged() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Cell cell = cells[row][col];
                if (cell.isMine() && !cell.isFlagged()) {
                    return false; // A mine is not flagged
                }
            }
        }
        return true;
    }

    public boolean hasUnopenedCells() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!cells[row][col].isRevealed() && !cells[row][col].isFlagged()) {
                    System.out.println("Unopened cell found at (" + row + ", " + col + ")");
                    return true;
                }
            }
        }
        System.out.println("No unopened cells remaining.");
        return false;
    }


    private List<Cell> getUnopenedNeighbors(int row, int col) {
        List<Cell> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;

                // Skip out-of-bounds cells and the current cell itself
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && !(i == 0 && j == 0)) {
                    Cell neighbor = cells[newRow][newCol];
                    if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        return neighbors;
    }


    private boolean allSafeCellsRevealed() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Cell cell = cells[row][col];
                if (!cell.isMine() && !cell.isRevealed()) {
                    return false; // A safe cell is not revealed
                }
            }
        }
        return true;
    }
    public boolean solve() {
        boolean progress = true;

        while (progress) {
            progress = false;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell cell = cells[i][j];

                    if (cell.isRevealed() && cell.getNeighboringMines() > 0) {
                        if (revealSafeNeighbors(i, j)) {
                            progress = true;
                        }
                        if (flagMineNeighbors(i, j)) {
                            progress = true;
                        }
                    }
                }
            }

            if (isWin()) {
                return true; // The board is solved
            }

            if (!progress && needsGuessing()) {
                System.out.println("Stuck! Unable to solve further without guessing.");
                break; // Exit the loop to avoid infinite iterations
            }
        }

        return false; // The board is not solvable
    }














//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~functie de Ajustare Tabla~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    private void adjustBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];

                if (!cell.isRevealed() && !cell.isFlagged() && !cell.isMine()) {
                    // Add a mine to this cell
                    cell.setMine(true);

                    // Remove a mine from a random safe cell
                    for (int x = 0; x < rows; x++) {
                        for (int y = 0; y < cols; y++) {
                            Cell potentialMine = cells[x][y];
                            if (potentialMine.isMine() && !potentialMine.isSafe() && (x != i || y != j)) {
                                potentialMine.setMine(false);
                                return;
                            }
                        }
                    }
                }
            }
        }

        // Recalculate neighboring mines after adjustment
        calculateNeighboringMines();
    }



}
*/