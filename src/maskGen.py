import random
from itertools import chain, combinations
import numpy as np

def powerset(iterable):
    """
    powerset([1,2,3]) --> () (1,) (2,) (3,) (1,2) (1,3) (2,3) (1,2,3)
    """
    xs = list(iterable)
    # note we return an iterator rather than a list
    return chain.from_iterable(combinations(xs,n) for n in range(len(xs)+1))

def maskToPowers(m):
    vals = []
    while m > 0:
        s = bin(m)[::-1]
        i = s.index("1")
        vals.append(1<<i)
        m -= 1<<i
    return vals

def actualVal(occupancy, x, y):
    board = [['0' for i in range(8)] for j in range(8)]
    i = 1
    while x+i < 8:
        board[7-y][7-x-i] = '1'
        if 1 << (8*y+x+i) in occupancy:
            break
        i += 1
    i = 1
    while x-i >= 0:
        board[7-y][7-x+i] = '1'
        if 1 << (8*y+x-i) in occupancy:
            break
        i += 1
    i = 1
    while y+i < 8:
        board[7-y-i][7-x] = '1'
        if 1 << (8*(y+i)+x) in occupancy:
            break
        i += 1
    while y-i >= 0:
        board[7-y+i][7-x] = '1'
        if 1 << (8*(y-i)+x) in occupancy:
            break
        i += 1
    return int("".join(["".join(row) for row in board]),2)

def actualValBish(occupancy, x, y):
    board = [['0' for i in range(8)] for j in range(8)]
    i = 1
    while x+i < 8 and y+i < 8:
        board[7-y-i][7-x-i] = '1'
        if 1 << (8*(y+i)+x+i) in occupancy:
            break
        i += 1
    i = 1
    while x-i >= 0 and y+i < 8:
        board[7-y-i][7-x+i] = '1'
        if 1 << (8*(y+i)+x-i) in occupancy:
            break
        i += 1
    i = 1
    while x+i < 8 and y-i >= 0:
        board[7-y+i][7-x-i] = '1'
        if 1 << (8*(y-i)+x+i) in occupancy:
            break
        i += 1
    i = 1
    while x-i >= 0 and y-i >= 0:
        board[7-y+i][7-x+i] = '1'
        if 1 << (8*(y-i)+x-i) in occupancy:
            break
        i += 1
    return int("".join(["".join(row) for row in board]),2)

masks = [0 for i in range(64)]
shifts = [64 for i in range(64)]
for x in range(8):
    for y in range(8):
        board = [['0' for i in range(8)] for j in range(8)]
        board[7-y] = ['0','1','1','1','1','1','1','0']
        for i in range(1,7):
            board[i][7-x] = '1'
        board[7-y][7-x] = '0'
        s = "".join(["".join(row) for row in board])
        shifts[8*y+x] = 64-s.count("1")
        masks[8*y+x] = hex(int(s,2))

bishMasks = [0 for i in range(64)]
bishShifts = [64 for i in range(64)]
justInside = 0x7e7e7e7e7e7e00
for x in range(8):
    for y in range(8):
        m = actualValBish([],x,y) & justInside
        bishMasks[8*y+x] = hex(m)
        bishShifts[8*y+x] = 64 - bin(m).count("1")

attacks = [[] for i in range(64)]
#magics = [0 for i in range(64)]
magics = [0xd1800081fc204000, 0x840045000600040, 0x290008200131c102, 0x3880100180580024,
            0x1a0008300200a004, 0x4900021834002100, 0x20041080400b200, 0x420000814121020c, 0x648008a2c00581, 0x8148040008e6000,
            0xb1a004220809200, 0x54a100210b005000, 0x5003000800950110, 0xaf31000244003900, 0x24400068811102c, 0x8a300150000a242,
            0x40c0028000402492, 0x4010014008200940, 0x8e0a820040201200, 0x1010022100008, 0x100901003008004c, 0x2518818012000400,
            0x2b6144003f0810a2, 0x54a826000682c405, 0x8010c0008008a482, 0xc24001808044e000, 0x103860200204050, 0x8a04a10100100208,
            0x1c79040080080080, 0x808a000a00041018, 0x3210483c00102502, 0x400156000c0285, 0x6029a04004800080, 0x8082402008401001,
            0x110020008180100a, 0x4a02013242000820, 0xa900320012002278, 0x901200509e000408, 0x8048802200800900, 0x4a0001440600108d,
            0x48a3400083a18003, 0xa001820100460021, 0x2a9600013010040, 0xc20080010008080, 0x8108012500910008, 0xe001054220018,
            0x1540834278040010, 0x42d5000080eb0002, 0x148c1d8245000011, 0x81e5628c460009c0, 0xa057908020000680, 0x44908808000044,
            0x9402409d052000c0, 0x108401221080040, 0xaa8561c09080040, 0x32000100440a8a00, 0x10c5810818c2011a, 0xa0810421b1c003,
            0x96da00300104139, 0x1100202500093001, 0x1002002420181066, 0x122003810040b82, 0xa300300801308a04, 0x97c2402810443b2]
bits64 = np.array([(1<<64)-1], "uint64")
# generate and test magics
##for i in range(64):
##    x = i%8
##    y = i // 8
##    mask = masks[i]
##    shift = shifts[i]
##   # occupancies = list(powerset(maskToPowers(int(mask,16))))
##    occupancies = np.array(list(powerset(maskToPowers(int(mask,16)))))
##    #actualVals = [actualVal(oc,x,y) for oc in occupancies]
##    actualVals = np.array([actualVal(oc,x,y) for oc in occupancies], "uint64")
##    totals = np.array([sum(occupied) for occupied in occupancies], "uint64")
##    while True:
##        magic = np.array([random.getrandbits(64)],"uint64")
##        magic &= np.array([random.getrandbits(64)],"uint64") # halve number of bits
##       # magic = np.array([int('180008024400010',16)],"uint64") #Works for first one
##        mapsto = {}
##        idxs = ((totals*magic)&bits64) >> shift
##        for val, idx in zip(actualVals, idxs):
##            if idx in mapsto:
##                if mapsto[idx] != val:
##                    break
##            else:
##                mapsto[idx] = val
##        else:
##            magics[i] = magic
##            print(i)
##            break

#bishMagics = [0 for i in range(64)]
bishMagics = [0xc010559004008121, 0x70a00800830dd002, 0xc7e20a00c6040684, 0x4242423804803d6,
            0xa062021060101324, 0x909b01084025b107, 0xc3082230161116, 0xac21390080200, 0x8948f02026b40c4c, 0x6700902ea0830200,
            0x9019401260a0204, 0x24000c04098da449, 0xa044440460209200, 0x4083398820098022, 0x850a8498384006, 0x401884404014890,
            0x8626008a0110200, 0x800440209b060600, 0x8270229200820208, 0x824002802444a00, 0xc000282e00480, 0x404400200d00c02,
            0x1400830a18045201, 0x8c2d0038208010a, 0x22402608100441, 0x500e125418902407, 0xa861901422040030, 0x39080014016020,
            0x821501006090c000, 0x2a28a8008080c02, 0x10308101020b1080, 0x4020c20030520211, 0x340e900c8040a802, 0x1808520104d402,
            0x4be10808020100c4, 0x401228080080200, 0x8216020c01020082, 0x4060808c00a024b, 0x4909040180a31805, 0x86083491062d80c0,
            0x314cb8a046005021, 0x1004d10450112040, 0xc0a9228020803002, 0x10c7e0a12402c806, 0x400d44100883dc06, 0x9041a8883028200,
            0x2418080804908141, 0x1c300102058a8261, 0x5129028860180103, 0x6a6e061202121257, 0x6140002201100048, 0x1f14680420a0140,
            0xc708202813041000, 0x1191806280a0414, 0x108a4810118a0000, 0x80a58801b4003124, 0x300504048c040610, 0x5594a44301d01000,
            0x210004fe2191010, 0x200400560208807, 0x131022b090520200, 0x510548588103d03, 0x84321044054c1411, 0x1388200904010312]
##for i in range(64):
##    x = i%8
##    y = i // 8
##    mask = bishMasks[i]
##    shift = bishShifts[i]
##   # occupancies = list(powerset(maskToPowers(int(mask,16))))
##    occupancies = np.array(list(powerset(maskToPowers(int(mask,16)))))
##    #actualVals = [actualVal(oc,x,y) for oc in occupancies]
##    actualVals = np.array([actualValBish(oc,x,y) for oc in occupancies], "uint64")
##    totals = np.array([sum(occupied) for occupied in occupancies], "uint64")
##    while True:
##        magic = np.array([random.getrandbits(64)],"uint64")
##        magic &= np.array([random.getrandbits(64)],"uint64") # halve number of bits
##       # magic = np.array([int('180008024400010',16)],"uint64") #Works for first one
##        mapsto = {}
##        idxs = ((totals*magic)&bits64) >> shift
##        for val, idx in zip(actualVals, idxs):
##            if idx in mapsto:
##                if mapsto[idx] != val:
##                    break
##            else:
##                mapsto[idx] = val
##        else:
##            bishMagics[i] = magic
##            print(i)
##            break


# generate attack masks i.e. lookup results
bits64 = (1<<64)-1
##for i in range(64):
##    x = i%8
##    y = i // 8
##    mask = masks[i]
##    shift = shifts[i]
##    magic = magics[i]
##    attacks[i] = [0 for i in range(1 << (64-shift))]
##    occupancies = list(powerset(maskToPowers(int(mask,16))))
##    actualVals = [actualVal(oc,x,y) for oc in occupancies]
##    totals = [sum(occupied) for occupied in occupancies]
##    for lookup, val in zip(totals, actualVals):
##        idx = ((lookup*magic)&bits64) >> shift
##        attacks[i][idx] = val
##    print(i)

bishAttacks = [[] for i in range(64)]
for i in range(64):
    x = i%8
    y = i // 8
    mask = bishMasks[i]
    shift = bishShifts[i]
    magic = bishMagics[i]
    bishAttacks[i] = [0 for i in range(1 << (64-shift))]
    occupancies = list(powerset(maskToPowers(int(mask,16))))
    actualVals = [actualValBish(oc,x,y) for oc in occupancies]
    totals = [sum(occupied) for occupied in occupancies]
    for lookup, val in zip(totals, actualVals):
        idx = ((lookup*magic)&bits64) >> shift
        bishAttacks[i][idx] = val
    print(i)
