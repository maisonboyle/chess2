package dab80;

public class Move  {
    static int[] values = new int[] {0,1,3,3,5,9,0,1,3,3,5,9,0};

    int piece;
    int fromX, fromY, toX, toY;
    int captured;
    int promoteTo; // 0 if no promotion

    // For tracking board state over many moves - values BEFORE move made
    //boolean[] castles; // [White_short, White_long, Black_short, Black_long]
    int castles;
    int epCol;
    int staleCountdown; // stalemate once reaches 0, value before the move, not after
    int score;

    public Move(int p, int fx, int fy, int tx, int ty, int cap, int promote, Board b){
        piece = p;
        fromX = fx;
        fromY = fy;
        toX = tx;
        toY = ty;
        captured = cap;
        promoteTo = promote;
        //castles = b.castles.clone(); // TODO: Replace with byte/int i.e. 4 bits, no cloning
        castles = b.castles;
        epCol = b.enPassant;
        staleCountdown = b.stalemateCountdown;
        score = values[captured] + values[promoteTo];
    }

    // same move assuming same board treated as equal
    @Override public boolean equals(Object o){
        if (o instanceof Move){
            Move m = (Move)o;
            return m.fromX == fromX && m.fromY == fromY && m.toX == toX && m.toY == toY && m.promoteTo == promoteTo;
        }
        return false;
    }

    @Override public String toString(){
        return ""+(char)(97+fromX)+(fromY+1)+(char)(97+toX)+(toY+1);
    }
}
