package dab80;

import java.util.*;

public class Engine {
    Board board;
    static int MAXDEPTH = 4;
    static int mateValue = -10000;

    public Engine(Board b){
        board = b;
    }

    public Move getMove(){
        Move best = null;
        int bestVal = 2*mateValue;
        for (Move m : board.validMoves()){
            board.makeMove(m);
            int val = -minimax(MAXDEPTH, 2*mateValue, -2*mateValue); // negative as side swaps
            if (val > bestVal){
                bestVal = val;
                best = m;
            }
            board.undoMove(m);
        }
        return best;
    }

    private int minimax(int depth, int alpha, int beta){
        if (depth == 0){
            return Evaluator.score(board)*board.turn;
        }
        List<Move> validMoves = board.validMoves();
        if (validMoves.size() == 0){
            if (board.inCheck().size() > 0){
                return mateValue-depth; // prefer a checkmate that's further away i.e. closer to leaves
            } else {
                return 0;
            }
        }
        for (Move m : validMoves){
            board.makeMove(m);
            int val = -minimax(depth-1, -beta, -alpha);
            alpha = Math.max(alpha, val);
            board.undoMove(m);
            if (alpha >= beta){
                break;
            }
        }
        return alpha;
    }
}
