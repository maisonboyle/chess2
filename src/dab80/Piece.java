package dab80;

public class Piece {
    int piece;
    int x;
    int y;

    static String[] names = new String[] {"", "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k"};
    static String[] tiles = new String[] {"a","b","c","d","e","f","g","h"};

    public Piece(int p, int col, int row){
        piece = p;
        x = col;
        y = row;
    }
    @Override
    public String toString(){
        return names[piece]+" "+tiles[x]+(y+1);
    }
}