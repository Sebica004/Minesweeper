import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class MinesweeperGUI {
    private JFrame frame;
    private JButton[][] buttons;
    private Board board;
    private JPanel boardPanel;

    private Map<String, Integer> stats;

    public MinesweeperGUI(Board board) {
        this.board = board;

        this.stats = StatsManager.loadStats();
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        int rows = board.getRows();
        int cols = board.getColumns();
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows, cols));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(rows, cols));

        buttons = new JButton[rows][cols];
       /* if (!board.solveBoard()) {
            System.out.println("Board unsolvable, regenerating...");
        }*/
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.PLAIN, 20));
                final int row = i;
                final int col = j;

                button.addActionListener(e -> handleLeftClick(row, col));
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            handleRightClick(row, col);
                        }
                    }
                });
                buttons[i][j] = button;
                panel.add(button);
            }
        }
        frame.add(panel);
        frame.setVisible(true);
        updateBoard();
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Click handlers~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public void endGame(boolean win) {
    if (win) {
        System.out.println("Congratulations! You won!");
        stats.put("wins", stats.get("wins") + 1); // Actualizează victoriile
    } else {
        System.out.println("BOOM! You lost!");
        stats.put("losses", stats.get("losses") + 1); // Actualizează pierderile
    }

    StatsManager.saveStats(stats); // Salvează statisticile în fișier
    displayStats(); // Afișează statistici în consolă
}
    private void displayStats() {
        System.out.println("Current Stats:");
        System.out.println("Wins: " + stats.get("wins"));
        System.out.println("Losses: " + stats.get("losses"));
    }
    private void handleLeftClick(int row, int col) {
        Cell cell = board.getCell(row, col);

        if (cell.isRevealed()) {
            if (cell.getNeighboringMines() > 0) {
                int flaggedNeighbors = board.countFlaggedNeighbors(row, col);
                if (flaggedNeighbors == cell.getNeighboringMines()) {
                    board.revealSafeNeighbors(row, col);
                    updateBoard();
                }
            }
        } else {
            if (board.revealCell(row, col)) {
                JOptionPane.showMessageDialog(frame, createGameOverPanel("BOOM! You hit a mine. Game over."));
                frame.dispose();
                endGame(false);
            } else if (board.isWin()) {
                JOptionPane.showMessageDialog(frame, createGameOverPanel("Congratulations! You won!"));
                endGame(true);
                frame.dispose();
            }

            updateBoard();
        }
    }

    private JPanel createGameOverPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        JButton resetButton = new JButton("Play Again");
        resetButton.addActionListener(e -> resetGame());
        panel.add(resetButton, BorderLayout.SOUTH);

        return panel;
    }

    private void resetGame() {
        frame.dispose();
        Board newBoard = new Board(board.getRows(), board.getColumns(), board.getTotalMines());

        new MinesweeperGUI(newBoard);
    }




    private void handleRightClick(int row, int col) {
        System.out.println("Right-click detected at (" + row + ", " + col + ")");
        Cell cell = board.getCell(row, col);

        if (cell.isRevealed()) {
            System.out.println("Right-click on a revealed cell. Ignored.");
            return;
        }

        if (!cell.isFlagged()) {
            System.out.println("Flagging cell at (" + row + ", " + col + ")");
        } else {
            System.out.println("Unflagging cell at (" + row + ", " + col + ")");
        }

        board.toggleFlag(row, col);
        updateBoard();
    }

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~update Board~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    private void updateBoard() {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                Cell cell = board.getCell(i, j);
                JButton button = buttons[i][j];

                if (cell.isRevealed()) {
                    if (cell.isMine()) {
                        button.setText("M");
                        button.setBackground(Color.RED);
                        button.setEnabled(false);
                    } else {
                        int neighboringMines = cell.getNeighboringMines();
                        button.setText(neighboringMines > 0 ? String.valueOf(neighboringMines) : "");
                        button.setBackground(Color.LIGHT_GRAY);
                        button.setEnabled(true);
                    }
                } else if (cell.isFlagged()) {
                    button.setText("F");
                    button.setBackground(Color.YELLOW);
                    button.setEnabled(true);
                } else if (i == board.getStartX() && j == board.getStartY()) {
                    button.setText("X");
                    button.setBackground(Color.GREEN);
                    button.setEnabled(true);
                } else {
                    button.setText("");
                    button.setBackground(null);
                    button.setEnabled(true);
                }
            }
        }
    }





    public static void main(String[] args) {
        Board board = new Board(10, 10, 18);
        new MinesweeperGUI(board);
    }
    private void initializeGUI(Board board) {
        Cell[][] cells = board.getCells();

        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                Cell cell = cells[y][x];
                JButton button = new JButton();

                button.addActionListener(e -> {
                    if (cell.isMine()) {
                        button.setText("M");
                        button.setBackground(Color.RED);
                    } else {
                        button.setText(String.valueOf(cell.getNeighboringMines()));
                        button.setBackground(Color.LIGHT_GRAY);
                    }
                });

                boardPanel.add(button);
            }
        }
    }

}
