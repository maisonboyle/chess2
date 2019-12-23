package dab80.bitboards;
// TODO: Optimise representation to allow fast move generation and not lots of check-testing
// TODO: Figure out move structure - what state required, just have bunch of static methods for working on int/longs
// TODO: Have class for game state - manipulate an int
// TODO: Pick constants for pieces, castling, etc.

import java.io.PipedOutputStream;
import java.util.List;

// 0 is bottom left, 63 is top right (black).
// 56 .... 63
// ...     ...
// 0 ..... 7
public class Board {
    long white_pawn = 0xFF00;
    long white_knight = 0x24;
    long white_bishop = 0x42;
    long white_rook = 0x18;
    long white_queen = 0x8;
    long white_king = 0x01;
    long black_pawn = 0xFF000000000000L;
    long black_knight = 0x2400000000000000L;
    long black_bishop = 0x4200000000000000L;
    long black_rook = 0x1800000000000000L;
    long black_queen = 0x800000000000000L;
    long black_king = 0x0100000000000000L;

    GameState gameState; // TODO: Complete

    private static String row = "   +---+---+---+---+---+---+---+---+";
    private static String rowLabel = "     a   b   c   d   e   f   g   h";
    private static String[] pieceStrings = new String[] {" ", "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k"};

    // TODO: May no longer be necessary
    public void printBoard(){
  /*      System.out.println("\n"+row);
        for (int y = 7; y >= 0; y--){
            System.out.print((y+1) + "  | ");
            for (int x = 0; x < 8; x++){
                System.out.print(pieceStrings[board[y][x]] + " | ");
            }
            System.out.println("\n"+row);
        }
        System.out.println(rowLabel+"\n");*/
    }

    // TODO:
    public String getFEN(){return null;}
    public void makeMove(Move m){}
    public void undoMove(Move m){}
    public List<Move> validMoves(){return null;}
    public List<Piece> inCheck(){return null;}
    // identify if player whose turn it is currently is in check
    public List<Piece> inCheck(int kingX, int kingY){return null;}
    public boolean inMate(){return false;}
    public List<Piece> allPieces() { // For use in board evaluation
        return null;
    }

}

class GameState {
    // 4 bits castling, 4 bits en-passant, 7 bits stale count

    int state;

    static int getCastling(GameState s){return (s.state >> 11);}
    static int getEPTile(GameState s){return (s.state >> 7) & 0xF;}
    static int getStaleCount(GameState s){return s.state & 0x7F;}

    GameState(int castle, int ep, int stale){
        state = (castle << 11) + (ep << 7) + stale;
    }

}

class Move {
    // info before move made
    // 4 bits for castling - white short/long, black short/long
    // 4 bits for en-passant column (need an invalid value so 9 values)
    // 7 bits for stalemate count (up to 100)

    // info about move
    // 4 bits for piece - MSB is turn (0 = white)
    // 6 bits for moving from, 6 bits moving to
    // 3 bits captured piece
    // 2 bits promotion (0 = none, 1 = Q, 2 = N)

    // bottom 20 bits for the score
    // total - 56 bits
    // 63      56 55     52 51 48 47        41  40    37 36  31  30 25 24      22 21      20 19      0
    // [8 unused, 4 castle, 4 ep, 7 stalemate | 4 piece, 6 from, 6 to, 3 capture, 2 promote, 20 score]

    long move;

    static int getCastle(Move m){
        return (int)((m.move >> 52) & 0xF);
    }
    static int getEPTile(Move m){
        return (int)((m.move >> 48) & 0xF);
    }
    static int getStaleCount(Move m){
        return (int)((m.move >> 41) & 0x7F);
    }
    static int getPiece(Move m){
        return (int)((m.move >> 37) & 0xF);
    }
    static int getFromTile(Move m){
        return (int)((m.move >> 31) & 0x3F);
    }
    static int getToTile(Move m){
        return (int)((m.move >> 25) & 0x3F);
    }
    static int getCaptured(Move m){
        return (int)((m.move >> 22) & 0x7);
    }
    static int getPromotion(Move m){
        return (int)((m.move >> 20) & 0x3);
    }
    static int getScore(Move m){
        return (int)(m.move & 0xFFFFF);
    }

    Move(int p, int fx, int fy, int tx, int ty, int cap, int promote, Board b){
        move = (GameState.getCastling(b.gameState) << 20) + (GameState.getEPTile(b.gameState) << 16) +
          (GameState.getStaleCount(b.gameState) << 9) + (p << 5);
        move = (move << 1) + (fx + 8*fy);
        move = (move << 31) + ((tx + 8*ty)<<25)+ (cap << 22) + (promote << 20);
        // TODO: Score field current unused, could put full evaluation in or just basic estimate
        // TODO: e.g. value of promotion + value of capture
    }
}

class Piece {
    // 4 bits piece type, 6 bits pos
    int p;
    static int getPiece(Piece p){return p.p >> 6;}
    static int getTile(Piece p){return p.p & 0x3F;}
    Piece(int p, int tile){
        this.p = (p << 6) + tile;
    }
}
