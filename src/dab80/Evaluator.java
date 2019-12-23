package dab80;

// TODO: More efficient evaluator for bitboards
public class Evaluator {
    // Kings value is 0 due to always being on the board
    static int[] values = new int[] {0, 1, 3, 3, 5, 9, 0, -1, -3, -3, -5, -9, 0};

    // TODO: Update to make use of centipawns based on position
    // TODO: Update to adjust values towards endgame (positional values)
    public static int score(Board b){
        int total = 0;
        for (Piece p : b.allPieces()){
            total += values[p.piece];
        }
        return total;
    }
}
