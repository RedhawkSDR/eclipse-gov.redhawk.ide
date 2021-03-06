/* MFileParserTokenManager.java */
/* Generated By:JavaCC: Do not edit this line. MFileParserTokenManager.java */
package gov.redhawk.mfile.parser;
import java.io.*;
import java.util.*;
import gov.redhawk.mfile.parser.model.*;

/** Token Manager. */
@SuppressWarnings("unused")class MFileParserTokenManager implements MFileParserConstants {

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0){
   switch (pos)
   {
      case 0:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 20;
            return 20;
         }
         return -1;
      case 1:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 20;
            jjmatchedPos = 1;
            return 20;
         }
         return -1;
      case 2:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 20;
            jjmatchedPos = 2;
            return 20;
         }
         return -1;
      case 3:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 20;
            jjmatchedPos = 3;
            return 20;
         }
         return -1;
      case 4:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 20;
            jjmatchedPos = 4;
            return 20;
         }
         return -1;
      case 5:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 20;
            jjmatchedPos = 5;
            return 20;
         }
         return -1;
      case 6:
         if ((active0 & 0x800L) != 0L)
         {
            jjmatchedKind = 20;
            jjmatchedPos = 6;
            return 20;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0){
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0(){
   switch(curChar)
   {
      case 40:
         return jjStopAtPos(0, 15);
      case 41:
         return jjStopAtPos(0, 17);
      case 44:
         return jjStopAtPos(0, 16);
      case 59:
         return jjStopAtPos(0, 18);
      case 61:
         return jjStopAtPos(0, 14);
      case 91:
         return jjStopAtPos(0, 12);
      case 93:
         return jjStopAtPos(0, 13);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x800L);
      default :
         return jjMoveNfa_0(3, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0){
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa4_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 116:
         return jjMoveStringLiteralDfa5_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 111:
         return jjMoveStringLiteralDfa7_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private int jjMoveStringLiteralDfa7_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 110:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(7, 11, 20);
         break;
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 76;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 3:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 21)
                        kind = 21;
                     { jjCheckNAddStates(0, 7); }
                  }
                  else if ((0x3400L & l) != 0L)
                  {
                     if (kind > 19)
                        kind = 19;
                  }
                  else if (curChar == 34)
                     { jjCheckNAddStates(8, 10); }
                  else if (curChar == 39)
                     { jjAddStates(11, 12); }
                  else if (curChar == 46)
                     { jjCheckNAdd(28); }
                  else if (curChar == 35)
                  {
                     if (kind > 10)
                        kind = 10;
                     { jjCheckNAddStates(13, 15); }
                  }
                  else if (curChar == 37)
                  {
                     if (kind > 9)
                        kind = 9;
                     { jjCheckNAddStates(16, 18); }
                  }
                  if ((0x3fe000000000000L & l) != 0L)
                     { jjCheckNAddStates(19, 21); }
                  else if (curChar == 48)
                     { jjAddStates(22, 23); }
                  else if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 4;
                  else if (curChar == 37)
                     jjstateSet[jjnewStateCnt++] = 0;
                  break;
               case 1:
                  if (curChar == 32)
                     { jjAddStates(24, 25); }
                  break;
               case 2:
                  if (curChar == 10 && kind > 3)
                     kind = 3;
                  break;
               case 5:
                  if (curChar == 32)
                     { jjAddStates(26, 27); }
                  break;
               case 6:
                  if (curChar == 10 && kind > 6)
                     kind = 6;
                  break;
               case 7:
                  if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 8:
                  if (curChar != 37)
                     break;
                  if (kind > 9)
                     kind = 9;
                  { jjCheckNAddStates(16, 18); }
                  break;
               case 9:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 9)
                     kind = 9;
                  { jjCheckNAddStates(16, 18); }
                  break;
               case 10:
                  if ((0x2400L & l) != 0L && kind > 9)
                     kind = 9;
                  break;
               case 11:
                  if (curChar == 10 && kind > 9)
                     kind = 9;
                  break;
               case 12:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 13:
                  if (curChar != 35)
                     break;
                  if (kind > 10)
                     kind = 10;
                  { jjCheckNAddStates(13, 15); }
                  break;
               case 14:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  { jjCheckNAddStates(13, 15); }
                  break;
               case 15:
                  if ((0x2400L & l) != 0L && kind > 10)
                     kind = 10;
                  break;
               case 16:
                  if (curChar == 10 && kind > 10)
                     kind = 10;
                  break;
               case 17:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 18:
                  if ((0x3400L & l) != 0L && kind > 19)
                     kind = 19;
                  break;
               case 20:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 21:
                  if ((0x3fe000000000000L & l) != 0L)
                     { jjCheckNAddStates(19, 21); }
                  break;
               case 22:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddStates(19, 21); }
                  break;
               case 23:
                  if (curChar == 46)
                     jjstateSet[jjnewStateCnt++] = 24;
                  break;
               case 24:
                  if ((0x3fe000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(25, 26); }
                  break;
               case 25:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(25, 26); }
                  break;
               case 27:
                  if (curChar == 46)
                     { jjCheckNAdd(28); }
                  break;
               case 28:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  { jjCheckNAddStates(28, 30); }
                  break;
               case 30:
                  if ((0x280000000000L & l) != 0L)
                     { jjCheckNAdd(31); }
                  break;
               case 31:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  { jjCheckNAddTwoStates(31, 32); }
                  break;
               case 33:
                  if (curChar == 39)
                     { jjAddStates(11, 12); }
                  break;
               case 34:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     { jjCheckNAdd(35); }
                  break;
               case 35:
                  if (curChar == 39 && kind > 26)
                     kind = 26;
                  break;
               case 37:
                  if ((0x8000008400000000L & l) != 0L)
                     { jjCheckNAdd(35); }
                  break;
               case 38:
                  if (curChar == 48)
                     { jjCheckNAddTwoStates(39, 35); }
                  break;
               case 39:
                  if ((0xff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(39, 35); }
                  break;
               case 40:
                  if ((0x3fe000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(41, 35); }
                  break;
               case 41:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(41, 35); }
                  break;
               case 42:
                  if (curChar == 48)
                     { jjAddStates(31, 32); }
                  break;
               case 44:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(44, 35); }
                  break;
               case 46:
                  if (curChar == 34)
                     { jjCheckNAddStates(8, 10); }
                  break;
               case 47:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     { jjCheckNAddStates(8, 10); }
                  break;
               case 49:
                  if ((0x8000008400000000L & l) != 0L)
                     { jjCheckNAddStates(8, 10); }
                  break;
               case 50:
                  if (curChar == 34 && kind > 27)
                     kind = 27;
                  break;
               case 51:
                  if (curChar == 48)
                     { jjCheckNAddStates(33, 36); }
                  break;
               case 52:
                  if ((0xff000000000000L & l) != 0L)
                     { jjCheckNAddStates(33, 36); }
                  break;
               case 53:
                  if ((0x3fe000000000000L & l) != 0L)
                     { jjCheckNAddStates(37, 40); }
                  break;
               case 54:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddStates(37, 40); }
                  break;
               case 55:
                  if (curChar == 48)
                     { jjAddStates(41, 42); }
                  break;
               case 57:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddStates(43, 46); }
                  break;
               case 59:
                  if (curChar == 48)
                     { jjAddStates(22, 23); }
                  break;
               case 61:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  { jjAddStates(47, 48); }
                  break;
               case 64:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 21)
                     kind = 21;
                  { jjCheckNAddStates(0, 7); }
                  break;
               case 65:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 21)
                     kind = 21;
                  { jjCheckNAddTwoStates(65, 66); }
                  break;
               case 67:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(67, 68); }
                  break;
               case 68:
                  if (curChar != 46)
                     break;
                  if (kind > 24)
                     kind = 24;
                  { jjCheckNAddStates(49, 51); }
                  break;
               case 69:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 24)
                     kind = 24;
                  { jjCheckNAddStates(49, 51); }
                  break;
               case 70:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(70, 27); }
                  break;
               case 71:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(71, 72); }
                  break;
               case 73:
                  if ((0x280000000000L & l) != 0L)
                     { jjCheckNAdd(74); }
                  break;
               case 74:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 25)
                     kind = 25;
                  { jjCheckNAddTwoStates(74, 75); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 3:
               case 20:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  { jjCheckNAdd(20); }
                  break;
               case 0:
                  if (curChar == 123)
                     { jjAddStates(24, 25); }
                  break;
               case 4:
                  if (curChar == 123)
                     { jjAddStates(26, 27); }
                  break;
               case 9:
                  if (kind > 9)
                     kind = 9;
                  { jjAddStates(16, 18); }
                  break;
               case 14:
                  if (kind > 10)
                     kind = 10;
                  { jjAddStates(13, 15); }
                  break;
               case 26:
                  if ((0x1000000010L & l) != 0L && kind > 22)
                     kind = 22;
                  break;
               case 29:
                  if ((0x2000000020L & l) != 0L)
                     { jjAddStates(52, 53); }
                  break;
               case 32:
                  if ((0x104000001040L & l) != 0L && kind > 24)
                     kind = 24;
                  break;
               case 34:
                  if ((0xffffffffefffffffL & l) != 0L)
                     { jjCheckNAdd(35); }
                  break;
               case 36:
                  if (curChar == 92)
                     { jjAddStates(54, 57); }
                  break;
               case 37:
                  if ((0x54404610000000L & l) != 0L)
                     { jjCheckNAdd(35); }
                  break;
               case 43:
                  if (curChar == 120)
                     { jjCheckNAdd(44); }
                  break;
               case 44:
                  if ((0x7e0000007eL & l) != 0L)
                     { jjCheckNAddTwoStates(44, 35); }
                  break;
               case 45:
                  if (curChar == 88)
                     { jjCheckNAdd(44); }
                  break;
               case 47:
                  if ((0xffffffffefffffffL & l) != 0L)
                     { jjCheckNAddStates(8, 10); }
                  break;
               case 48:
                  if (curChar == 92)
                     { jjAddStates(58, 61); }
                  break;
               case 49:
                  if ((0x54404610000000L & l) != 0L)
                     { jjCheckNAddStates(8, 10); }
                  break;
               case 56:
                  if (curChar == 120)
                     { jjCheckNAdd(57); }
                  break;
               case 57:
                  if ((0x7e0000007eL & l) != 0L)
                     { jjCheckNAddStates(43, 46); }
                  break;
               case 58:
                  if (curChar == 88)
                     { jjCheckNAdd(57); }
                  break;
               case 60:
                  if (curChar == 120)
                     { jjCheckNAdd(61); }
                  break;
               case 61:
                  if ((0x7e0000007eL & l) == 0L)
                     break;
                  if (kind > 23)
                     kind = 23;
                  { jjCheckNAddTwoStates(61, 62); }
                  break;
               case 62:
                  if ((0x20100000201000L & l) != 0L && kind > 23)
                     kind = 23;
                  break;
               case 63:
                  if (curChar == 88)
                     { jjCheckNAdd(61); }
                  break;
               case 66:
                  if ((0x20100000201000L & l) != 0L && kind > 21)
                     kind = 21;
                  break;
               case 72:
                  if ((0x2000000020L & l) != 0L)
                     { jjAddStates(62, 63); }
                  break;
               case 75:
                  if ((0x104000001040L & l) != 0L && kind > 25)
                     kind = 25;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 9:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 9)
                     kind = 9;
                  { jjAddStates(16, 18); }
                  break;
               case 14:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 10)
                     kind = 10;
                  { jjAddStates(13, 15); }
                  break;
               case 34:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjstateSet[jjnewStateCnt++] = 35;
                  break;
               case 47:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(8, 10); }
                  break;
               default : if (i1 == 0 || l1 == 0 || i2 == 0 ||  l2 == 0) break; else break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 76 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private int jjMoveStringLiteralDfa0_1(){
   switch(curChar)
   {
      case 37:
         return jjMoveStringLiteralDfa1_1(0x10L);
      default :
         return 1;
   }
}
private int jjMoveStringLiteralDfa1_1(long active0){
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 1;
   }
   switch(curChar)
   {
      case 125:
         if ((active0 & 0x10L) != 0L)
            return jjStopAtPos(1, 4);
         break;
      default :
         return 2;
   }
   return 2;
}
private int jjMoveStringLiteralDfa0_2(){
   switch(curChar)
   {
      case 35:
         return jjMoveStringLiteralDfa1_2(0x80L);
      default :
         return 1;
   }
}
private int jjMoveStringLiteralDfa1_2(long active0){
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 1;
   }
   switch(curChar)
   {
      case 125:
         if ((active0 & 0x80L) != 0L)
            return jjStopAtPos(1, 7);
         break;
      default :
         return 2;
   }
   return 2;
}
static final int[] jjnextStates = {
   65, 66, 67, 68, 70, 27, 71, 72, 47, 48, 50, 34, 36, 14, 15, 17, 
   9, 10, 12, 22, 23, 26, 60, 63, 1, 2, 5, 6, 28, 29, 32, 43, 
   45, 47, 48, 52, 50, 47, 48, 54, 50, 56, 58, 47, 48, 57, 50, 61, 
   62, 69, 29, 32, 30, 31, 37, 38, 40, 42, 49, 51, 53, 55, 73, 74, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default :
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, 
"\146\165\156\143\164\151\157\156", "\133", "\135", "\75", "\50", "\54", "\51", "\73", null, null, null, null, 
null, null, null, null, null, };
protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(java.io.IOException e)
   {
      jjmatchedKind = 0;
      jjmatchedPos = -1;
      matchedToken = jjFillToken();
      matchedToken.specialToken = specialToken;
      return matchedToken;
   }
   image = jjimage;
   image.setLength(0);
   jjimageLen = 0;

   for (;;)
   {
     switch(curLexState)
     {
       case 0:
         try { input_stream.backup(0);
            while (curChar <= 32 && (0x100000200L & (1L << curChar)) != 0L)
               curChar = input_stream.BeginToken();
         }
         catch (java.io.IOException e1) { continue EOFLoop; }
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_0();
         break;
       case 1:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_1();
         if (jjmatchedPos == 0 && jjmatchedKind > 5)
         {
            jjmatchedKind = 5;
         }
         break;
       case 2:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_2();
         if (jjmatchedPos == 0 && jjmatchedKind > 8)
         {
            jjmatchedKind = 8;
         }
         break;
     }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
           matchedToken.specialToken = specialToken;
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else if ((jjtoSkip[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           if ((jjtoSpecial[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
           {
              matchedToken = jjFillToken();
              if (specialToken == null)
                 specialToken = matchedToken;
              else
              {
                 matchedToken.specialToken = specialToken;
                 specialToken = (specialToken.next = matchedToken);
              }
              SkipLexicalActions(matchedToken);
           }
           else
              SkipLexicalActions(null);
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
        jjimageLen += jjmatchedPos + 1;
      if (jjnewLexState[jjmatchedKind] != -1)
        curLexState = jjnewLexState[jjmatchedKind];
        curPos = 0;
        jjmatchedKind = 0x7fffffff;
        try {
           curChar = input_stream.readChar();
           continue;
        }
        catch (java.io.IOException e1) { }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
   }
  }
}

void SkipLexicalActions(Token matchedToken)
{
   switch(jjmatchedKind)
   {
      default :
         break;
   }
}
private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

    /** Constructor. */
    public MFileParserTokenManager(JavaCharStream stream){

      if (JavaCharStream.staticFlag)
            throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");

    input_stream = stream;
  }

  /** Constructor. */
  public MFileParserTokenManager (JavaCharStream stream, int lexState){
    ReInit(stream);
    SwitchTo(lexState);
  }

  /** Reinitialise parser. */
  public void ReInit(JavaCharStream stream)
  {
    jjmatchedPos = jjnewStateCnt = 0;
    curLexState = defaultLexState;
    input_stream = stream;
    ReInitRounds();
  }

  private void ReInitRounds()
  {
    int i;
    jjround = 0x80000001;
    for (i = 76; i-- > 0;)
      jjrounds[i] = 0x80000000;
  }

  /** Reinitialise parser. */
  public void ReInit(JavaCharStream stream, int lexState)
  {
    ReInit(stream);
    SwitchTo(lexState);
  }

  /** Switch to specified lex state. */
  public void SwitchTo(int lexState)
  {
    if (lexState >= 3 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
    else
      curLexState = lexState;
  }

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
   "IN_MULTI_LINE_COMMENT",
   "IN_MULTI_LINE_COMMENT_2",
};

/** Lex State array. */
public static final int[] jjnewLexState = {
   -1, -1, -1, 1, 0, -1, 2, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, 
};
static final long[] jjtoToken = {
   0xffff801L, 
};
static final long[] jjtoSkip = {
   0x696L, 
};
static final long[] jjtoSpecial = {
   0x690L, 
};
static final long[] jjtoMore = {
   0x168L, 
};
    protected JavaCharStream  input_stream;

    private final int[] jjrounds = new int[76];
    private final int[] jjstateSet = new int[2 * 76];

    private final StringBuilder jjimage = new StringBuilder();
    private StringBuilder image = jjimage;
    private int jjimageLen;
    private int lengthOfMatch;
    
    protected char curChar;
}
