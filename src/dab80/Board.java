package dab80;

import java.util.*;

// TODO: Optimise representation to allow fast move generation and not lots of check-testing

public class Board {
    public static int WHITE_PAWN = 1, WHITE_KNIGHT = 2, WHITE_BISHOP = 3, WHITE_ROOK = 4, WHITE_QUEEN = 5, WHITE_KING = 6,
               BLACK_PAWN = 7, BLACK_KNIGHT = 8, BLACK_BISHOP = 9, BLACK_ROOK = 10, BLACK_QUEEN = 11, BLACK_KING = 12;

    static int WHITE = 1, BLACK = -1;
    static int WHITE_SHORT = 1, WHITE_LONG = 2, BLACK_SHORT = 4, BLACK_LONG = 8; // castling
    static int nWHITE_SHORT = 0b1110, nWHITE_LONG = 0b1101, nBLACK_SHORT = 0b1011, nBLACK_LONG = 0b0111;
    int castles = 0xF;

    int turn = WHITE;
    int moves = 0; // number of half-turns since start

    int stalemateCountdown = 0; // stalemate if ever reaches 100
    int enPassant = -1;
    //boolean[] castles = new boolean[] {true, true, true, true}; // [White_short, White_long, Black_short, Black_long]

    public int[][] board = new int[][] {
            new int[] {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK},
            new int[] {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
            new int[] {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}
    };

    private static String row = "   +---+---+---+---+---+---+---+---+";
    private static String rowLabel = "     a   b   c   d   e   f   g   h";
    private static String[] pieceStrings = new String[] {" ", "P", "N", "B", "R", "Q", "K", "p", "n", "b", "r", "q", "k"};
    public void printBoard(){
        System.out.println("\n"+row);
        for (int y = 7; y >= 0; y--){
            System.out.print((y+1) + "  | ");
            for (int x = 0; x < 8; x++){
                System.out.print(pieceStrings[board[y][x]] + " | ");
            }
            System.out.println("\n"+row);
        }
        System.out.println(rowLabel+"\n");
    }

    // TODO: Get FEN string

    public void makeMove(Move m){
        int oldTurn = turn;
        turn = -turn;
        moves++;
        enPassant = -1;
        if (m.toY  == 0){ // white rook space
            if (m.toX == 0){
                castles &= nWHITE_LONG;
                //castles[1] = false;
            }else if (m.toX == 7){
                castles &= nWHITE_SHORT;
                //castles[0] = false;
            }
        } else if (m.toY == 7){ // black rook space
            if (m.toX == 0){
                castles &= nBLACK_LONG;
                //castles[3] = false;
            }else if (m.toX == 7){
                castles &= nBLACK_SHORT;
                //castles[2] = false;
            }
        }
        if (m.piece == WHITE_ROOK && m.fromY == 0){
            if (m.fromX == 0){castles &= nWHITE_LONG;} //castles[1] = false;}
            else if (m.fromX == 7){castles &= nWHITE_SHORT;} //castles[0] = false;}
        } else if (m.piece == BLACK_ROOK && m.fromY == 7){
            if (m.fromX == 0){castles &= nBLACK_LONG;} //castles[3] = false;}
            else if (m.fromX == 7){castles &= nBLACK_SHORT;} //castles[2] = false;}
        }
        // castling
        if (m.piece % 6 == 0){
            if (oldTurn == WHITE){
                castles &= 0b1100;
                //castles[0] = false; castles[1] = false;
            } else {
                castles &= 0b11;
                //castles[2] = false; castles[3] = false;
            }
            if (m.toX - m.fromX == 2){ // short
                board[m.fromY][m.fromX] = 0;
                board[m.toY][m.toX] = m.piece;
                // rook
                board[m.fromY][5] = board[m.fromY][7];
                board[m.fromY][7] = 0;
                return;
            } else if (m.toX - m.fromX == -2){ // long
                board[m.fromY][m.fromX] = 0;
                board[m.toY][m.toX] = m.piece;
                // rook
                board[m.fromY][3] = board[m.fromY][0];
                board[m.fromY][0] = 0;
                return;
            }
        } else if (m.piece % 6 == 1) { // pawn move
            stalemateCountdown = 0; // resets on a pawn move
            // promotion
            if (m.toY % 7 == 0){
                board[m.toY][m.toX] = m.promoteTo;
                board[m.fromY][m.fromX] = 0;
                return;
            }
            // en passant
            else if (m.fromX != m.toX && board[m.toY][m.toX] == 0){
                board[m.fromY][m.fromX] = 0;
                board[m.toY][m.toX] = m.piece;
                if (oldTurn == WHITE){
                    board[4][m.toX] = 0;
                }else{
                    board[3][m.toX] = 0;
                }
                return;
            } else if (m.toY - m.fromY == 2*oldTurn){
                enPassant = m.fromX;
            }
        }
        // regular
        if (board[m.toY][m.toX] != 0){
            stalemateCountdown = 0; // resets on a capture
        }
        board[m.fromY][m.fromX] = 0;
        board[m.toY][m.toX] = m.piece;
    }

    public void undoMove(Move m){
        moves--;
        castles = m.castles;
        //castles = m.castles.clone(); // TODO: Replace with byte/int i.e. 4 bits, no cloning
        enPassant = m.epCol;
        stalemateCountdown = m.staleCountdown;
        turn = -turn;

        // castling
        if (m.piece % 6 == 0){
            if (m.toX - m.fromX == 2){ // short
                // rook
                board[m.fromY][7] = board[m.fromY][5];
                board[m.fromY][5] = 0;
            } else if (m.toX - m.fromX == -2){ // long
                // rook
                board[m.fromY][0] = board[m.fromY][3];
                board[m.fromY][3] = 0;
            }
        } else if (m.piece % 6 == 1 && m.fromX != m.toX) { // pawn move
            // en passant
            if (turn == WHITE){
                if (m.toY == 5 && m.toX == enPassant){
                    board[4][enPassant] = BLACK_PAWN; // CHANGED from 5 to 4
                }
            }else if (m.toY == 2 && m.toX == enPassant){
                board[3][enPassant] = WHITE_PAWN; // CHANGED from 2 to 3
            }
        }
        board[m.toY][m.toX] = m.captured; // 0 if no piece taken (or en passant)
        board[m.fromY][m.fromX] = m.piece;
    }

    // TODO: Use Set rather than List, override hashCode in Move to match equals
    public List<Move> validMoves(){
        List<Move> results = new LinkedList<>();
        int kingVal = turn == WHITE ? WHITE_KING : BLACK_KING;
        int kingX = 0, kingY = 0;
        List<Piece> checks = null;
        kingSearch:
        for (int y = 0; y < 8; y++){
            for (int x = 0; x < 8; x++){
                if (board[y][x] == kingVal){
                    kingX = x;
                    kingY = y;
                    checks = inCheck(x,y);
                    break kingSearch;
                }
            }
        }
        if (checks.size() > 1){ // must move king
            for (int x = -1; x < 2; x++){
                for (int y = -1; y < 2; y++){
                    if (kingX+x >= 0 && kingX + x < 8 && kingY+y >= 0 && kingY+y < 8){
                        int val = board[kingY+y][kingX+x];
                        if (val == 0 || turn == WHITE && val > WHITE_KING || turn == BLACK && val < BLACK_PAWN){
                            // may still be a move into check, must test at end
                            results.add(new Move(kingVal, kingX, kingY, kingX+x, kingY+y, val, 0, this));
                        }
                    }
                }
            }
        } else { // could be in check TODO: split into case of single check and none, identify pin/capture lines
            int queen = turn == WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            int knight = turn == WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            int rook = turn == WHITE ? WHITE_ROOK : BLACK_ROOK;
            int bishop = turn == WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    int piece = board[y][x];
                    if (piece == (turn == WHITE ? WHITE_PAWN : BLACK_PAWN)) { // pawns
                        // regular | 2 move
                        if (board[y+turn][x] == 0){
                            if (y+turn == 0 || y + turn == 7){ // promotion
                                results.add(new Move(piece, x, y, x, y + turn, 0, queen, this));
                                results.add(new Move(piece, x, y, x, y + turn, 0, knight, this));
                                results.add(new Move(piece, x, y, x, y + turn, 0, rook, this));
                                results.add(new Move(piece, x, y, x, y + turn, 0, bishop, this));
                            }
                            else {
                                results.add(new Move(piece, x, y, x, y + turn, 0, 0, this));
                                if ((y == (turn == WHITE ? 1 : 6)) && board[y + 2 * turn][x] == 0) { // initial 2 move
                                    results.add(new Move(piece, x, y, x, y + 2*turn, 0, 0, this));
                                }
                            }
                        }
                        // capture
                        for (int dx = -1; dx <= 1; dx += 2){
                            if (x + dx >= 0 && x + dx < 8) {
                                int val = board[y+turn][x+dx];
                                if (val > 0 && (turn == WHITE ? val > WHITE_KING : val < BLACK_PAWN)){
                                    if (y + turn == 0 || y + turn == 7){ // promotion
                                        results.add(new Move(piece, x, y, x+dx, y + turn, val, queen, this));
                                        results.add(new Move(piece, x, y, x+dx, y + turn, val, knight, this));
                                        results.add(new Move(piece, x, y, x+dx, y + turn, val, rook, this));
                                        results.add(new Move(piece, x, y, x+dx, y + turn, val, bishop, this));
                                    } else {
                                        results.add(new Move(piece, x, y, x+dx, y + turn, val, 0, this));
                                    }
                                }
                                if (val == 0 && y == (turn == WHITE ? 4 : 3) && x+dx == enPassant){ // en passant
                                    results.add(new Move(piece, x, y, x+dx, y + turn, 0, 0, this));
                                }
                            }
                        }

                    } else if (piece == knight) { // knights TODO: lookup table
                        for (int dx = -1; dx <= 1; dx += 2){
                            for (int dy = -2; dy <= 2; dy += 4){
                                if (x+dx >= 0 && x + dx < 8 && y + dy >= 0 && y + dy < 8){
                                    int val = board[y+dy][x+dx];
                                    if (val == 0 || (turn == WHITE ? val > WHITE_KING : val < BLACK_PAWN)){
                                        results.add(new Move(piece,x,y,x+dx,y+dy,val,0,this));
                                    }
                                }
                                if (x+dy >= 0 && x + dy < 8 && y + dx >= 0 && y + dx < 8){
                                    int val = board[y+dx][x+dy];
                                    if (val == 0 || (turn == WHITE ? val > WHITE_KING : val < BLACK_PAWN)){
                                        results.add(new Move(piece,x,y,x+dy,y+dx,val,0,this));
                                    }
                                }
                            }
                        }
                    } else if (piece == bishop) { // bishops
                        for (int dx = -1; dx <= 1; dx += 2){
                            for (int dy = -1; dy <= 1; dy += 2){
                                int i = 1;
                                while (x + dx*i >= 0 && x + dx*i < 8 && y + dy*i >= 0 && y + dy*i < 8){
                                    int val = board[y+dy*i][x+dx*i];
                                    if (val == 0){
                                        results.add(new Move(piece,x,y,x+dx*i,y+dy*i,val,0,this));
                                    } else {
                                        if (turn == WHITE ? val > WHITE_KING : val < BLACK_PAWN){
                                            results.add(new Move(piece,x,y,x+dx*i,y+dy*i,val,0,this));
                                        }
                                        break;
                                    }
                                    i++;
                                }
                            }
                        }
                    } else if (piece == rook) { // rooks
                        for (int dx = -1; dx <= 1; dx+= 2){
                            for (int ax = 0; ax < 2; ax++){
                                int i = 1;
                                while (ax==0 ? y+dx*i >= 0 && y+dx*i < 8 : x+dx*i >= 0 && x+dx*i < 8){
                                    int val = board[y+dx*i*(1-ax)][x+dx*i*ax];
                                    if (val == 0){
                                        results.add(new Move(piece,x,y,x+dx*i*ax,y+dx*i*(1-ax),val,0,this));
                                    } else {
                                        if (turn == WHITE ? val > WHITE_KING : val < BLACK_PAWN){
                                            results.add(new Move(piece,x,y,x+dx*i*ax,y+dx*i*(1-ax),val,0,this));
                                        }
                                        break;
                                    }
                                    i++;
                                }
                            }
                        }
                    } else if (piece == (turn == WHITE ? WHITE_QUEEN : BLACK_QUEEN)) { // queens
                        // Diagonals - same as bishops
                        for (int dx = -1; dx <= 1; dx += 2){
                            for (int dy = -1; dy <= 1; dy += 2){
                                int i = 1;
                                while (x + dx*i >= 0 && x + dx*i < 8 && y + dy*i >= 0 && y + dy*i < 8){
                                    int val = board[y+dy*i][x+dx*i];
                                    if (val == 0){
                                        results.add(new Move(piece,x,y,x+dx*i,y+dy*i,val,0,this));
                                    } else {
                                        if (turn == WHITE ? val > WHITE_KING : val < BLACK_PAWN){
                                            results.add(new Move(piece,x,y,x+dx*i,y+dy*i,val,0,this));
                                        }
                                        break;
                                    }
                                    i++;
                                }
                            }
                        }
                        for (int dx = -1; dx <= 1; dx+= 2){
                            for (int ax = 0; ax < 2; ax++){
                                int i = 1;
                                while (ax==0 ? y+dx*i >= 0 && y+dx*i < 8 : x+dx*i >= 0 && x+dx*i < 8){
                                    int val = board[y+dx*i*(1-ax)][x+dx*i*ax];
                                    if (val == 0){
                                        results.add(new Move(piece,x,y,x+dx*i*ax,y+dx*i*(1-ax),val,0,this));
                                    } else {
                                        if (turn == WHITE ? val > WHITE_KING : val < BLACK_PAWN){
                                            results.add(new Move(piece,x,y,x+dx*i*ax,y+dx*i*(1-ax),val,0,this));
                                        }
                                        break;
                                    }
                                    i++;
                                }
                            }
                        }
                    } else if (piece == (turn == WHITE ? WHITE_KING : BLACK_KING)) { // king
                        for (int dx = -1; dx < 2; dx++){
                            for (int dy = -1; dy < 2; dy++){
                                if ((dx != 0 || dy != 0) && x+dx >= 0 && x + dx < 8 && y + dy >= 0 && y + dy < 8){
                                    int val = board[y+dy][x+dx];
                                    if (val == 0 || (turn == WHITE ? val > WHITE_KING : val < BLACK_PAWN)){
                                        results.add(new Move(piece,x,y,x+dx,y+dy,val,0,this));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // castle
            if (checks.size() == 0){
                if (turn == WHITE && (castles & 0b11) != 0){//(castles[0] || castles[1])){
                    board[0][4] = 0; // remove king to avoid check test collisions with extra king
                    if ((castles & WHITE_SHORT) != 0 && board[0][5] == 0 && board[0][6] == 0 && inCheck(5,0).size() == 0){
                    //if (castles[0] && board[0][5] == 0 && board[0][6] == 0 && inCheck(5,0).size() == 0){
                        results.add(new Move(WHITE_KING, 4, 0, 6, 0, 0, 0, this));
                    }
                    if ((castles & WHITE_LONG) != 0 && board[0][1] == 0 && board[0][2] == 0 && board[0][3] == 0 &&
                            inCheck(3,0).size() == 0){
                    //if (castles[1] && board[0][1] == 0 && board[0][2] == 0 && board[0][3] == 0 && inCheck(3,0).size() == 0){
                        results.add(new Move(WHITE_KING, 4, 0, 2, 0, 0, 0, this));
                    }
                    board[0][4] = WHITE_KING;
                }else if (turn == BLACK && (castles & 0b1100) != 0){//(castles[2] || castles[3])) {
                    board[7][4] = 0; // remove king to avoid check test collisions with extra king
                    //if (castles[2] && board[7][5] == 0 && board[7][6] == 0 && inCheck(5, 7).size() == 0) {
                    if ((castles & BLACK_SHORT) != 0 && board[7][5] == 0 && board[7][6] == 0 && inCheck(5, 7).size() == 0) {
                        results.add(new Move(BLACK_KING, 4, 7, 6, 7, 0, 0, this));
                    }
                    //if (castles[3] && board[7][1] == 0 && board[7][2] == 0 && board[7][3] == 0 && inCheck(3, 7).size() == 0) {
                    if ((castles & BLACK_LONG) != 0 && board[7][1] == 0 && board[7][2] == 0 && board[7][3] == 0 &&
                            inCheck(3, 7).size() == 0) {
                        results.add(new Move(BLACK_KING, 4, 7, 2, 7, 0, 0, this));
                    }
                    board[7][4] = BLACK_KING;
                }
            }
        }
        List<Move> fullLegal = new LinkedList<>();
        for (Move m : results){
            makeMove(m);
            turn = -turn;
            if (m.piece == WHITE_KING || m.piece == BLACK_KING){
                if (inCheck(m.toX,m.toY).size() == 0){
                    fullLegal.add(m);
                }
            }else if (inCheck(kingX,kingY).size() == 0){
                fullLegal.add(m);
            }
            turn = -turn;
            undoMove(m);
        }
        Collections.sort(fullLegal, (o1, o2) -> o2.score - o1.score); // greatest score treated as least so appears first
        return fullLegal;
    }

    public List<Piece> inCheck(){
        int king = turn == WHITE ? WHITE_KING : BLACK_KING;
        for (int y = 0; y < 8; y++){
            for (int x = 0; x < 8; x++){
                if (board[y][x] == king){
                    return inCheck(x,y);
                }
            }
        }
        System.out.println("failed");
        return null;
    }

    // identify if player whose turn it is currently is in check
    public List<Piece> inCheck(int kingX, int kingY){ // returns list of attacking pieces.
        // Also have to look for opponent king to avoid moving into check.
        // In this case only care whether or not a check occurs, not how many so breaks still ok
        // for regular check tests, want to know if 1 or 2 checks, cannot have multiple diagonal checks.
        List<Piece> attackers = new ArrayList<>();
        // knights
        int knight = turn == WHITE ? BLACK_KNIGHT : WHITE_KNIGHT;
        knightSearch:
        for (int dx = -1; dx <= 1; dx += 2){
            for (int dy = -2; dy <= 2; dy += 4){
                if (kingX + dx >= 0 && kingX + dx < 8 && kingY + dy >= 0 && kingY + dy < 8){
                    if (board[kingY+dy][kingX+dx] == knight){
                        attackers.add(new Piece(knight, kingX+dx, kingY+dy));
                        break knightSearch; // can only be checked by 1 knight at a time
                    }
                }
                if (kingX + dy >= 0 && kingX + dy < 8 && kingY + dx >= 0 && kingY + dx < 8){
                    if (board[kingY+dx][kingX+dy] == knight){
                        attackers.add(new Piece(knight, kingX+dy, kingY+dx));
                        break knightSearch;
                    }
                }
            }
        }
        // bishop/queen/pawn/king
        int pawn = turn == WHITE ? BLACK_PAWN : WHITE_PAWN;
        int bish = turn == WHITE ? BLACK_BISHOP : WHITE_BISHOP;
        int queen = turn == WHITE ? BLACK_QUEEN : WHITE_QUEEN;
        int king = turn == WHITE ? BLACK_KING : WHITE_KING;
        diagSearch:
        for (int dx = -1; dx <= 1; dx += 2){
            for (int dy = -1; dy <= 1; dy += 2){
                int i = 1;
                while (kingX + dx*i >= 0 && kingX + dx*i < 8 && kingY + dy*i >= 0 && kingY + dy*i < 8){
                    int piece = board[kingY + dy*i][kingX + dx*i];
                    if (piece != 0){
                        if (piece == bish || piece == queen || (piece == pawn && i == 1 && dy == turn)
                        || (piece == king && i == 1)){
                            attackers.add(new Piece(piece, kingX+dx*i, kingY+dy*i));
                            break diagSearch;
                        }
                        break;
                    }
                    i++;
                }
            }
        }
        // rook/queen/king
        int rook = turn == WHITE ? BLACK_ROOK : WHITE_ROOK;
        straightSearch:
        for (int dx = -1; dx <= 1; dx += 2){
            int i = 1;
            while (kingX + i*dx >= 0 && kingX + i*dx < 8){
                int piece = board[kingY][kingX + dx*i];
                if (piece != 0){
                    if (piece == rook || piece == queen  || (piece == king && i == 1)){
                        attackers.add(new Piece(piece, kingX+dx*i, kingY));
                        break straightSearch;
                    }
                    break;
                }
                i++;
            }
            i = 1;
            while (kingY + i*dx >= 0 && kingY + i*dx < 8){
                int piece = board[kingY+dx*i][kingX];
                if (piece != 0){
                    if (piece == rook || piece == queen  || (piece == king && i == 1)){
                        attackers.add(new Piece(piece, kingX, kingY+dx*i));
                        break straightSearch;
                    }
                    break;
                }
                i++;
            }
        }
        return attackers;
    }

    public boolean inMate(){
        int kingVal = turn == WHITE ? WHITE_KING : BLACK_KING;
        for (int y = 0; y < 8; y++){
            for (int x = 0; x < 8; x++){
                if (board[y][x] == kingVal){
                    return inCheck(x,y).size() > 0 && validMoves().size() == 0;
                }
            }
        }
        return true;
    }

    public List<Piece> allPieces(){ // For use in board evaluation
        List<Piece> pieces = new ArrayList<>();
        for (int y = 0; y < 8; y++){
            for (int x = 0; x < 8; x++){
                int val = board[y][x];
                if (val != 0){
                    pieces.add(new Piece(val,x,y));
                }
            }
        }
        return pieces;
    }


    // TODO: Perft test after changing castling
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Board b = new Board();
        b.board = InitialPositions.kiwiPeteBoard; //  perft test

        int p2 = 0;
        int p3 = 0;
        int p4 = 0;
        int p5 = 0;

        List<Move> p1Moves = b.validMoves();
        int p1 = p1Moves.size();
        for (Move m : p1Moves){
            b.makeMove(m);
            List<Move> p2Moves = b.validMoves();
            p2 += p2Moves.size();
            for (Move n : p2Moves){
                b.makeMove(n);
                List<Move> p3Moves = b.validMoves();
                for (Move m3 : p3Moves){
                    b.makeMove(m3);
                    List<Move> moves4 = b.validMoves();
                    p4 += moves4.size();
                    for (Move m4 : moves4){
                        b.makeMove(m4);
                        p5 += b.validMoves().size();
                        b.undoMove(m4);
                    }
                    b.undoMove(m3);
                }
                p3 += p3Moves.size();
                b.undoMove(n);
            }
            b.undoMove(m);
        }
        System.out.println();
        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p4);
        System.out.println(p5); // 193690690
        System.out.println("Time taken: "+(System.nanoTime()-startTime)); // with int castles : 35003485400
    }
}
