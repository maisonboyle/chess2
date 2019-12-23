package dab80.bitboards;
// File format: 64 lines, 1 per position
// each line has an integer for number of entries
// followed by that many longs (in decimal) separated by spaces

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

// TODO: Search for improved magics for larger sized tiles e.g. [0,0] for rooks is 12 bits

// NOTE: Attacks include friendly pieces, must AND with ~friendly
public class SlideAttacks {
    static int[] rookShifts = new int[] {52, 53, 53, 53, 53, 53, 53, 52, 53, 54, 54, 54, 54, 54, 54, 53,
            53, 54, 54, 54, 54, 54, 54, 53, 53, 54, 54, 54, 54, 54, 54, 53, 53, 54, 54, 54, 54, 54, 54, 53,
            53, 54, 54, 54, 54, 54, 54, 53, 53, 54, 54, 54, 54, 54, 54, 53, 52, 53, 53, 53, 53, 53, 53, 52};
    static int[] bishShifts = new int[] {58, 59, 59, 59, 59, 59, 59, 58, 59, 59, 59, 59, 59, 59, 59, 59, 59,
            59, 57, 57, 57, 57, 59, 59, 59, 59, 57, 55, 55, 57, 59, 59, 59, 59, 57, 55, 55, 57, 59, 59, 59,
            59, 57, 57, 57, 57, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 58, 59, 59, 59, 59, 59, 59, 58};
    static long[] rookMagics = new long[] {0xd1800081fc204000L, 0x840045000600040L, 0x290008200131c102L, 0x3880100180580024L,
            0x1a0008300200a004L, 0x4900021834002100L, 0x20041080400b200L, 0x420000814121020cL, 0x648008a2c00581L, 0x8148040008e6000L,
            0xb1a004220809200L, 0x54a100210b005000L, 0x5003000800950110L, 0xaf31000244003900L, 0x24400068811102cL, 0x8a300150000a242L,
            0x40c0028000402492L, 0x4010014008200940L, 0x8e0a820040201200L, 0x1010022100008L, 0x100901003008004cL, 0x2518818012000400L,
            0x2b6144003f0810a2L, 0x54a826000682c405L, 0x8010c0008008a482L, 0xc24001808044e000L, 0x103860200204050L, 0x8a04a10100100208L,
            0x1c79040080080080L, 0x808a000a00041018L, 0x3210483c00102502L, 0x400156000c0285L, 0x6029a04004800080L, 0x8082402008401001L,
            0x110020008180100aL, 0x4a02013242000820L, 0xa900320012002278L, 0x901200509e000408L, 0x8048802200800900L, 0x4a0001440600108dL,
            0x48a3400083a18003L, 0xa001820100460021L, 0x2a9600013010040L, 0xc20080010008080L, 0x8108012500910008L, 0xe001054220018L,
            0x1540834278040010L, 0x42d5000080eb0002L, 0x148c1d8245000011L, 0x81e5628c460009c0L, 0xa057908020000680L, 0x44908808000044L,
            0x9402409d052000c0L, 0x108401221080040L, 0xaa8561c09080040L, 0x32000100440a8a00L, 0x10c5810818c2011aL, 0xa0810421b1c003L,
            0x96da00300104139L, 0x1100202500093001L, 0x1002002420181066L, 0x122003810040b82L, 0xa300300801308a04L, 0x97c2402810443b2L};
    static long[] bishMagics = new long[] {0xc010559004008121L, 0x70a00800830dd002L, 0xc7e20a00c6040684L, 0x4242423804803d6L,
            0xa062021060101324L, 0x909b01084025b107L, 0xc3082230161116L, 0xac21390080200L, 0x8948f02026b40c4cL, 0x6700902ea0830200L,
            0x9019401260a0204L, 0x24000c04098da449L, 0xa044440460209200L, 0x4083398820098022L, 0x850a8498384006L, 0x401884404014890L,
            0x8626008a0110200L, 0x800440209b060600L, 0x8270229200820208L, 0x824002802444a00L, 0xc000282e00480L, 0x404400200d00c02L,
            0x1400830a18045201L, 0x8c2d0038208010aL, 0x22402608100441L, 0x500e125418902407L, 0xa861901422040030L, 0x39080014016020L,
            0x821501006090c000L, 0x2a28a8008080c02L, 0x10308101020b1080L, 0x4020c20030520211L, 0x340e900c8040a802L, 0x1808520104d402L,
            0x4be10808020100c4L, 0x401228080080200L, 0x8216020c01020082L, 0x4060808c00a024bL, 0x4909040180a31805L, 0x86083491062d80c0L,
            0x314cb8a046005021L, 0x1004d10450112040L, 0xc0a9228020803002L, 0x10c7e0a12402c806L, 0x400d44100883dc06L, 0x9041a8883028200L,
            0x2418080804908141L, 0x1c300102058a8261L, 0x5129028860180103L, 0x6a6e061202121257L, 0x6140002201100048L, 0x1f14680420a0140L,
            0xc708202813041000L, 0x1191806280a0414L, 0x108a4810118a0000L, 0x80a58801b4003124L, 0x300504048c040610L, 0x5594a44301d01000L,
            0x210004fe2191010L, 0x200400560208807L, 0x131022b090520200L, 0x510548588103d03L, 0x84321044054c1411L, 0x1388200904010312L};
    static long[] rookMasks = new long[] {0x101010101017eL, 0x202020202027cL, 0x404040404047aL, 0x8080808080876L, 0x1010101010106eL,
            0x2020202020205eL, 0x4040404040403eL, 0x8080808080807eL, 0x1010101017e00L, 0x2020202027c00L, 0x4040404047a00L, 0x8080808087600L,
            0x10101010106e00L, 0x20202020205e00L, 0x40404040403e00L, 0x80808080807e00L, 0x10101017e0100L, 0x20202027c0200L, 0x40404047a0400L,
            0x8080808760800L, 0x101010106e1000L, 0x202020205e2000L, 0x404040403e4000L, 0x808080807e8000L, 0x101017e010100L, 0x202027c020200L,
            0x404047a040400L, 0x8080876080800L, 0x1010106e101000L, 0x2020205e202000L, 0x4040403e404000L, 0x8080807e808000L, 0x1017e01010100L,
            0x2027c02020200L, 0x4047a04040400L, 0x8087608080800L, 0x10106e10101000L, 0x20205e20202000L, 0x40403e40404000L, 0x80807e80808000L,
            0x17e0101010100L, 0x27c0202020200L, 0x47a0404040400L, 0x8760808080800L, 0x106e1010101000L, 0x205e2020202000L, 0x403e4040404000L,
            0x807e8080808000L, 0x7e010101010100L, 0x7c020202020200L, 0x7a040404040400L, 0x76080808080800L, 0x6e101010101000L,
            0x5e202020202000L, 0x3e404040404000L, 0x7e808080808000L, 0x7e01010101010100L, 0x7c02020202020200L, 0x7a04040404040400L,
            0x7608080808080800L, 0x6e10101010101000L, 0x5e20202020202000L, 0x3e40404040404000L, 0x7e80808080808000L};
    static long[] bishMasks = new long[] {0x40201008040200L, 0x402010080400L, 0x4020100a00L, 0x40221400L, 0x2442800L, 0x204085000L,
            0x20408102000L, 0x2040810204000L, 0x20100804020000L, 0x40201008040000L, 0x4020100a0000L, 0x4022140000L, 0x244280000L,
            0x20408500000L, 0x2040810200000L, 0x4081020400000L, 0x10080402000200L, 0x20100804000400L, 0x4020100a000a00L, 0x402214001400L,
            0x24428002800L, 0x2040850005000L, 0x4081020002000L, 0x8102040004000L, 0x8040200020400L, 0x10080400040800L, 0x20100a000a1000L,
            0x40221400142200L, 0x2442800284400L, 0x4085000500800L, 0x8102000201000L, 0x10204000402000L, 0x4020002040800L, 0x8040004081000L,
            0x100a000a102000L, 0x22140014224000L, 0x44280028440200L, 0x8500050080400L, 0x10200020100800L, 0x20400040201000L, 0x2000204081000L,
            0x4000408102000L, 0xa000a10204000L, 0x14001422400000L, 0x28002844020000L, 0x50005008040200L, 0x20002010080400L, 0x40004020100800L,
            0x20408102000L, 0x40810204000L, 0xa1020400000L, 0x142240000000L, 0x284402000000L, 0x500804020000L, 0x201008040200L, 0x402010080400L,
            0x2040810204000L, 0x4081020400000L, 0xa102040000000L, 0x14224000000000L, 0x28440200000000L, 0x50080402000000L, 0x20100804020000L,
            0x40201008040200L};


    static long[][] rookAttacks = new long[64][];
    static long[][] bishAttacks = new long[64][];
    static { // read rooks
        try {
            Scanner sc = new Scanner(new File("res/rookAttacks.txt"));
            for (int i = 0; i < 64; i++){
                int n = sc.nextInt();
                long[] pieceAttacks = new long[n];
                rookAttacks[i] = pieceAttacks;
                for (int j = 0; j < n; j++){
                    pieceAttacks[j] = sc.nextLong();
                }
            } // read bishops
            sc = new Scanner(new File("res/bishAttacks.txt"));
            for (int i = 0; i < 64; i++){
                int n = sc.nextInt();
                long[] pieceAttacks = new long[n];
                bishAttacks[i] = pieceAttacks;
                for (int j = 0; j < n; j++){
                    pieceAttacks[j] = sc.nextLong();
                }
            }
        } catch (IOException e){
            throw new ExceptionInInitializerError(e);
        }
    }
}