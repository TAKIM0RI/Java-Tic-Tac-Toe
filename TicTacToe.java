import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class TicTacToe {
    public static void main(String[] args) {
        new TicTacToe();
    }

    int boardWidth = 900;
    int boardHeight = 650;

    JFrame frame = new JFrame("Tic-Tac-Toe");
    JLabel textLabel = new JLabel();
    JLabel timerLabel = new JLabel("Time Left: 15", JLabel.CENTER);  // Timer label
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel scorePanel = new JPanel();
    JPanel historyPanel = new JPanel();
    JTextArea historyArea = new JTextArea();

    JButton[][] board = new JButton[3][3];
    String playerX = "X";
    String playerO = "O";
    String currentPlayer = playerX;

    boolean gameOver = false;
    int turns = 0;

    int scoreX = 0;
    int scoreO = 0;
    int matchCounter = 1;

    ArrayList<String> gameHistory = new ArrayList<>();

    JLabel scoreLabelX = new JLabel();
    JLabel scoreLabelO = new JLabel();
    JLabel matchLabel = new JLabel("Best of 3 – First to 2 wins");

    JDialog congratsDialog;

    // Timer variables
    int timeLimit = 15;  // Time limit in seconds (set to 15 seconds per player)
    Timer countdownTimer;
    boolean isTimeOut = false;

    TicTacToe() {
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setBackground(Color.darkGray);
        textLabel.setForeground(Color.white);
        textLabel.setFont(new Font("Courier New", Font.BOLD, 40));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Tic-Tac-Toe");
        textLabel.setOpaque(true);
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        frame.add(textPanel, BorderLayout.NORTH);

        // Timer Label
        timerLabel.setFont(new Font("Courier New", Font.BOLD, 26));
        timerLabel.setForeground(Color.red);
        frame.add(timerLabel, BorderLayout.SOUTH);  // Add timer label at the bottom

        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBackground(Color.darkGray);
        frame.add(boardPanel, BorderLayout.CENTER);

        scorePanel.setPreferredSize(new Dimension(230, boardHeight));
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(new Color(50, 50, 50));

        scoreLabelX.setFont(new Font("Courier New", Font.BOLD, 26));
        scoreLabelX.setForeground(Color.cyan);
        scoreLabelX.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabelO.setFont(new Font("Courier New", Font.BOLD, 26));
        scoreLabelO.setForeground(Color.orange);
        scoreLabelO.setAlignmentX(Component.CENTER_ALIGNMENT);

        matchLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        matchLabel.setForeground(Color.lightGray);
        matchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton viewHistoryBtn = new JButton("View History");
        viewHistoryBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewHistoryBtn.addActionListener(_ -> toggleHistoryPanel());

        JButton clearHistoryBtn = new JButton("Clear History");
        clearHistoryBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearHistoryBtn.addActionListener(_ -> clearHistory());

        scorePanel.add(Box.createVerticalStrut(20));
        scorePanel.add(scoreLabelX);
        scorePanel.add(scoreLabelO);
        scorePanel.add(matchLabel);
        scorePanel.add(Box.createVerticalStrut(20));
        scorePanel.add(viewHistoryBtn);
        scorePanel.add(clearHistoryBtn);
        frame.add(scorePanel, BorderLayout.EAST);

        historyPanel.setPreferredSize(new Dimension(200, boardHeight));
        historyPanel.setLayout(new BorderLayout());
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyArea.setBackground(Color.black);
        historyArea.setForeground(Color.green);
        historyPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);

        congratsDialog = new JDialog(frame, "Champion!", true);
        congratsDialog.setSize(350, 180);
        congratsDialog.setLocationRelativeTo(frame);
        congratsDialog.setLayout(new BorderLayout());

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton tile = new JButton();
                board[r][c] = tile;
                boardPanel.add(tile);

                tile.setBackground(Color.darkGray);
                tile.setForeground(Color.white);
                tile.setFont(new Font("Arial", Font.BOLD, 120));
                tile.setFocusable(false);

                tile.addActionListener(e -> {
                    if (gameOver) return;
                    JButton clicked = (JButton) e.getSource();
                    if (clicked.getText().equals("")) {
                        clicked.setText(currentPlayer);
                        turns++;
                        checkWinner();
                        if (!gameOver) {
                            currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
                            textLabel.setText(currentPlayer + "'s turn.");
                            startTimer();  // Restart the timer when it's the next player's turn
                        }
                    }
                });
            }
        }

        updateScoreLabels();
        textLabel.setText(currentPlayer + "'s turn.");
        startTimer();  // Start the timer when the game begins
    }

    void startTimer() {
        // Stop the previous timer if it's still running
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        timeLimit = 15;  // Reset time limit to 15 seconds
        timerLabel.setText("Time Left: " + timeLimit);

        countdownTimer = new Timer(1000, _ -> {
            timeLimit--;
            timerLabel.setText("Time Left: " + timeLimit);

            if (timeLimit <= 0) {
                isTimeOut = true;
                handleTimeOut();
                countdownTimer.stop();
            }
        });

        countdownTimer.start();
    }

    void handleTimeOut() {
        textLabel.setText(currentPlayer + " ran out of time!");

        // Wait for a brief moment before switching to the next player
        Timer delay = new Timer(1000, e -> {
            currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
            textLabel.setText(currentPlayer + "'s turn.");
            startTimer();  // Restart the timer for the next player
        });
        delay.setRepeats(false);
        delay.start();
    }

    void toggleHistoryPanel() {
        if (frame.getContentPane().isAncestorOf(historyPanel)) {
            frame.remove(historyPanel);
        } else {
            updateHistoryArea();
            frame.add(historyPanel, BorderLayout.WEST);
        }
        frame.revalidate();
        frame.repaint();
    }

    void updateHistoryArea() {
        StringBuilder sb = new StringBuilder();
        for (String record : gameHistory) {
            sb.append(record).append("\n");
        }
        historyArea.setText(sb.toString());
    }

    void clearHistory() {
        gameHistory.clear();
        updateHistoryArea();
    }

    void checkWinner() {
        for (int r = 0; r < 3; r++) {
            if (board[r][0].getText().equals("")) continue;
            if (board[r][0].getText().equals(board[r][1].getText()) &&
                board[r][1].getText().equals(board[r][2].getText())) {
                for (int i = 0; i < 3; i++) setWinner(board[r][i]);
                handleGameWin(currentPlayer);
                return;
            }
        }

        for (int c = 0; c < 3; c++) {
            if (board[0][c].getText().equals("")) continue;
            if (board[0][c].getText().equals(board[1][c].getText()) &&
                board[1][c].getText().equals(board[2][c].getText())) {
                for (int i = 0; i < 3; i++) setWinner(board[i][c]);
                handleGameWin(currentPlayer);
                return;
            }
        }

        if (board[0][0].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][2].getText()) &&
            !board[0][0].getText().equals("")) {
            for (int i = 0; i < 3; i++) setWinner(board[i][i]);
            handleGameWin(currentPlayer);
            return;
        }

        if (board[0][2].getText().equals(board[1][1].getText()) &&
            board[1][1].getText().equals(board[2][0].getText()) &&
            !board[0][2].getText().equals("")) {
            setWinner(board[0][2]);
            setWinner(board[1][1]);
            setWinner(board[2][0]);
            handleGameWin(currentPlayer);
            return;
        }

        if (turns == 9) {
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++)
                    setTie(board[r][c]);

            textLabel.setText("It's a tie!");
            gameOver = true;
            Timer delay = new Timer(1000, _ -> resetBoard());
            delay.setRepeats(false);
            delay.start();
        }
    }

    void handleGameWin(String player) {
        if (player.equals(playerX)) scoreX++;
        else scoreO++;

        updateScoreLabels();

        if (scoreX == 2 || scoreO == 2) {
            textLabel.setText(player + " wins the match!");
            gameHistory.add("Match " + matchCounter++ + ": " + player + " won the match");
            updateHistoryArea();
            showCongratulations(player);

            Timer delay = new Timer(2000, _ -> resetMatch());
            delay.setRepeats(false);
            delay.start();
        } else {
            textLabel.setText(player + " wins this round!");
            gameOver = true;
            Timer delay = new Timer(1000, _ -> resetBoard());
            delay.setRepeats(false);
            delay.start();
        }
    }

    void updateScoreLabels() {
        scoreLabelX.setText("X: " + scoreX);
        scoreLabelO.setText("O: " + scoreO);
    }

    void setWinner(JButton tile) {
        tile.setForeground(Color.green);
        tile.setBackground(new Color(70, 70, 70));
        gameOver = true;
    }

    void setTie(JButton tile) {
        tile.setForeground(Color.orange);
        tile.setBackground(Color.gray);
    }

    void resetMatch() {
        scoreX = 0;
        scoreO = 0;
        updateScoreLabels();
        resetBoard();
        textLabel.setText("New match – First to 2 wins");
    }

    void resetBoard() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                board[r][c].setText("");
                board[r][c].setBackground(Color.darkGray);
                board[r][c].setForeground(Color.white);
            }
        turns = 0;
        gameOver = false;
        currentPlayer = playerX;
        textLabel.setText(currentPlayer + "'s turn.");
    }

    void showCongratulations(String winner) {
        congratsDialog.getContentPane().removeAll();

        JPanel congratsPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = winner.equals(playerX) ? 
                        new Color(100, 200, 255) : new Color(255, 180, 100);
                Color endColor = new Color(30, 30, 30);
                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JLabel congratsLabel = new JLabel("CONGRATULATIONS!");
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        congratsLabel.setForeground(Color.WHITE);
        congratsLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel winnerLabel = new JLabel(winner + " is the Champion!");
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setHorizontalAlignment(JLabel.CENTER);

        congratsPanel.add(congratsLabel, BorderLayout.NORTH);
        congratsPanel.add(winnerLabel, BorderLayout.CENTER);
        congratsDialog.add(congratsPanel);

        Timer closeTimer = new Timer(1800, _ -> congratsDialog.setVisible(false));
        closeTimer.setRepeats(false);

        congratsDialog.setModal(false);
        congratsDialog.setVisible(true);
        closeTimer.start();
    }
}
