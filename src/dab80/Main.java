package dab80;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

import static dab80.Board.*;

// TODO: Start tracking with version control

// TODO: 3-repetition rule

// TODO: Update GUI to provide promotion options

// TODO: Include tailCall class to allow tail recursion via lazy lists (look online)

// TODO: Update display before going to computer move for faster updates. Add indication of if tile selected (setBackground on btn)

// TODO: Custom btn class to track icon rather than PieceImage class

// TODO: Reset move choice on clicking friendly piece (and highlight)

// TODO: Use interface into board to iterate through positions.

public class Main extends SwingWorker<Boolean,Boolean> implements ActionListener {
    Board b = new Board();
    Scanner sc = new Scanner(System.in);
    List<Move> validMoves;
    Engine engine = new Engine(b);

    private Move moveToPlay;

    public void playerMove(){
        interactive = true;
        /*
        while (true) { // runs until a move is accepted
            String line = sc.nextLine();
            Move m;
            while (!line.matches("[a-h][1-8][a-h][1-8](N|Q)?|O-O|O-O-O")) {
                System.out.println("Invalid, please enter in format a1b2 or O-O or O-O-O");
            }
            int piece, y, promote;
            if (line.equals("O-O")) {
                piece = b.turn == WHITE ? WHITE_KING : BLACK_KING;
                y = b.turn == WHITE ? 0 : 7;
                m = new Move(piece, 4, y, 6, y, 0, 0, b);
            } else if (line.equals("O-O-O")) {
                piece = b.turn == WHITE ? WHITE_KING : BLACK_KING;
                y = b.turn == WHITE ? 0 : 7;
                m = new Move(piece, 4, y, 2, y, 0, 0, b);
            } else {
                int fromX = (int) line.charAt(0) - 97; // 97 = a
                int fromY = (int) line.charAt(1) - 49; // 49 = 1
                int toX = (int) line.charAt(2) - 97; // 97 = a
                int toY = (int) line.charAt(3) - 49; // 49 = 1
                piece = b.board[fromY][fromX];
                // promotion
                if (line.length() == 5) {
                    if (line.charAt(4) == 'N') {
                        promote = b.turn == WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
                    } else {
                        promote = b.turn == WHITE ? WHITE_QUEEN : BLACK_QUEEN;
                    }
                } else {
                    promote = 0;
                }
                m = new Move(piece, fromX, fromY, toX, toY, b.board[toY][toX], promote, b);
            }
            for (Move fromLst : validMoves) {
                if (fromLst.equals(m)) {
                    return fromLst;
                }
            } // not a valid move if return didn't execute
            System.out.println("Invalid move");
        }
        */
    }

    boolean[] isPlayer = new boolean[] {true, false};

    public void playGame(){
        b.printBoard();
        validMoves = b.validMoves();
        if (validMoves.size() > 0){
            issueMove();
        }
        /*
        while (validMoves.size() != 0 && b.stalemateCountdown < 100){
            Move m;
            if (isPlayer[b.turn == WHITE ? 0 : 1]) {
                m = playerMove();
            } else {
                m = engine.getMove();
                System.out.println(m);
            }
            b.makeMove(m);
            validMoves = b.validMoves();
            b.printBoard();
            updateDisplay();
        }
        // determine checkmate vs stalemate
        if (b.inMate()){
            System.out.println(String.format("Checkmate! %s wins!", b.turn == WHITE ? "Black" : "White"));
        }else{
            System.out.println("Stalemate");
        }*/
    }

    // tell computer or user methods to setup moveToPlay - validMoves has been set
    public void issueMove(){
        if (isPlayer[b.turn == WHITE ? 0 : 1]) {
            playerMove();
        } else {
            moveToPlay = engine.getMove();
            System.out.println(moveToPlay);
            completeMove(); // no action listener to do this
        }
    }

    // complete now that moveToPlay correctly set
    public void completeMove(){
        b.makeMove(moveToPlay);
        moveToPlay = null;
        validMoves = b.validMoves();
        b.printBoard();
        updateDisplay();
        if (validMoves.size() == 0 || b.stalemateCountdown >= 100){
            // determine checkmate vs stalemate
            if (b.inMate()){
                System.out.println(String.format("Checkmate! %s wins!", b.turn == WHITE ? "Black" : "White"));
            }else{
                System.out.println("Stalemate");
            }
        } else {
            SwingUtilities.invokeLater(() -> issueMove());
            //issueMove();
        }
    }

    @Override
    public Boolean doInBackground(){
        playGame();
        return true;
    }

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private JPanel chessBoard;
    private final JLabel message = new JLabel(
            "ready to play!");
    private static final String COLS = "ABCDEFGH";

    Main() {
        initializeGui();
    }

    private int fromX = -1, fromY = -1, toX = -  1 , toY = -1;
    private int pieceSelected;
    private boolean interactive = false;

    @Override
    public void actionPerformed(ActionEvent e){
        if (interactive) {
            String pos = e.getActionCommand();
            int x = pos.charAt(0) - '0';
            int y = pos.charAt(1) - '0';
            System.out.println(x + " , " + y);
            if (b.turn == WHITE ? (b.board[y][x] > 0 && b.board[y][x] < BLACK_PAWN) : (b.board[y][x] > WHITE_KING)) {
                pieceSelected = b.board[y][x];
                fromX = x;
                fromY = y;
            } else {
                toX = x;
                toY = y;
                int promote;
                if (pieceSelected % 6 == 1 && toY % 7 == 0) {
                    // TODO: Handle promotions better
                    promote = b.turn == WHITE ? WHITE_QUEEN : BLACK_QUEEN;
                } else {
                    promote = 0;
                }
                Move m = new Move(pieceSelected, fromX, fromY, toX, toY, 0, promote, b);
                for (Move fromLst : validMoves) {
                    if (fromLst.equals(m)) {
                        moveToPlay = fromLst;
                        fromX = -1;
                        fromY = -1;
                        toX = -1;
                        toY = -1;
                        pieceSelected = 0;
                        interactive = false;
                        completeMove();
                        return;
                    }
                }
                fromX = -1;
                fromY = -1;
                toX = -1;
                toY = -1;
                pieceSelected = 0;
            }
        }
    }

    public final void initializeGui() {
        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        tools.add(new JButton("New")); // TODO - add functionality!
        tools.add(new JButton("Save")); // TODO - add functionality!
        tools.add(new JButton("Restore")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(new JButton("Resign")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(message);

        gui.add(new JLabel("?"), BorderLayout.LINE_START);

        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setBorder(new LineBorder(Color.BLACK));
        gui.add(chessBoard);

        // create the chess board squares
        Insets buttonMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton btn = new JButton();
                btn.setMargin(buttonMargin);
                btn.addActionListener(this);
                btn.setActionCommand((""+jj)+ii);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                int val = b.board[ii][jj];
                ImageIcon icon;
                if (val == 0){
                    icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                } else {
                    icon = new PieceImage(ii, jj, b.board[ii][jj]).img; // TODO: get just the image
                }
                btn.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 1)
                        //) {
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    btn.setBackground(Color.WHITE);
                } else {
                    btn.setBackground(new Color(50,50,50));//Color.BLACK);
                }
                chessBoardSquares[jj][ii] = btn;
            }
        }

        //fill the chess board
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                            SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 7; ii >= 0; ii--) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (ii + 1),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                }
            }
        }
    }

    public void updateDisplay(){
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                int val = b.board[ii][jj];
                ImageIcon icon;
                if (val == 0) {
                    icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                } else {
                    icon = new PieceImage(ii, jj, b.board[ii][jj]).img; // TODO: get just the image
                }
                chessBoardSquares[jj][ii].setIcon(icon);
            }
        }
        chessBoard.repaint();
        chessBoard.revalidate();

    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }


    public static void main(String[] args) {
        Runnable r = () -> {
                Main cb = new Main();

                JFrame f = new JFrame("Chess");
                f.add(cb.getGui());
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);

                // ensures the frame is the minimum size it needs to be
                // in order display the components within it
                f.pack();
                // ensures the minimum size is enforced.
                f.setMinimumSize(f.getSize());
                f.setVisible(true);
                cb.execute();
            };
        SwingUtilities.invokeLater(r);
    }
}