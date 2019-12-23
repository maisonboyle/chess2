package dab80;

import javax.swing.*;

public class PieceImage extends JLabel {
    static String res = "res/icons/";
    static String[] imageFiles = {null, "white_pawn.png", "white_knight.png", "white_bishop.png", "white_rook.png", "white_queen.png",
            "white_king.png", "black_pawn.png", "black_knight.png", "black_bishop.png", "black_rook.png", "black_queen.png", "black_king.png"};

    int row, col;
    ImageIcon img;

    public PieceImage(int x, int y, int piece){
        row = x;
        col = y;
        img = new ImageIcon(res+imageFiles[piece]);
        setIcon(img);
    }
}
