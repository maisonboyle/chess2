package dab80;

import static dab80.Board.*;

public class InitialPositions {
    // [White_short, White_long, Black_short, Black_long]
    public static boolean[] defaultCastle = new boolean[] {true, true, true, true};
    public static boolean[] noCastles = new boolean[] {false, false, false, false};
    public static boolean[] justWhiteCastles = new boolean[] {true, true, false, false};
    public static boolean[] justBlackCastles = new boolean[] {false, false, true, true};

    public static int[][] defaultBoard = new int[][] {
            new int[] {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK},
            new int[] {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
            new int[] {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}
    };

    // FEN : r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1
    public static int[][] kiwiPeteBoard = new int[][] {
            new int[] {WHITE_ROOK, 0, 0, 0, WHITE_KING, 0, 0, WHITE_ROOK},
            new int[] {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_BISHOP, WHITE_BISHOP, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
            new int[] {0, 0, WHITE_KNIGHT, 0, 0, WHITE_QUEEN, 0, BLACK_PAWN},
            new int[] {0, BLACK_PAWN, 0, 0, WHITE_PAWN, 0, 0, 0},
            new int[] {0, 0, 0, WHITE_PAWN, WHITE_KNIGHT, 0, 0, 0},
            new int[] {BLACK_BISHOP, BLACK_KNIGHT, 0, 0, BLACK_PAWN, BLACK_KNIGHT, BLACK_PAWN, 0},
            new int[] {BLACK_PAWN, 0, BLACK_PAWN, BLACK_PAWN, BLACK_QUEEN, BLACK_PAWN, BLACK_BISHOP, 0},
            new int[] {BLACK_ROOK, 0, 0, 0, BLACK_KING, 0, 0, BLACK_ROOK}
    };

    public static int[][] endBoard = new int[][] {
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {0, 0, 0, 0, WHITE_PAWN, 0, WHITE_PAWN, 0},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0},
            new int[] {0, WHITE_ROOK, 0, 0, 0, BLACK_PAWN, 0, BLACK_KING},
            new int[] {WHITE_KING, WHITE_PAWN, 0, 0, 0, 0, 0, BLACK_ROOK},
            new int[] {0, 0, 0, BLACK_PAWN, 0, 0, 0, 0},
            new int[] {0, 0, BLACK_PAWN, 0, 0, 0, 0, 0},
            new int[] {0, 0, 0, 0, 0, 0, 0, 0}
    };
}
