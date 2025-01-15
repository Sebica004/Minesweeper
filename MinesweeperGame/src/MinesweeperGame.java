import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MinesweeperGame {

    private Board board;

    public MinesweeperGame(Board board) {
        this.board = board;
    }

    public void startGame() {
        System.out.println("Starting game...");
        playGame();
    }

    private void playGame() {
        Scanner scanner = new Scanner(System.in);
        boolean gameRunning = true;

        while (gameRunning) {
            board.printBoard();
            System.out.println("Enter your move:");
            System.out.println("Format: 'R row col' to reveal or 'F row col' to flag/unflag.");
            String command = scanner.next();
            int row = scanner.nextInt();
            int col = scanner.nextInt();

            switch (command.toUpperCase()) {
                case "R":
                    if (board.revealCell(row, col)) {
                        gameRunning = false;
                        System.out.println("You lost! Better luck next time.");
                    } else if (board.isWin()) {
                        gameRunning = false;
                        System.out.println("Congratulations! You revealed all safe cells and won!");
                    }
                    break;
                case "F":
                    board.toggleFlag(row, col);
                    break;
                default:
                    System.out.println("Invalid command! Use 'R' or 'F'.");
            }
        }

        scanner.close();
    }


}
