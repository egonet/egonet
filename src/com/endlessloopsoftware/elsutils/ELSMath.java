/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.endlessloopsoftware.elsutils;

public class ELSMath
{
   private ELSMath()
   {
   }

   /**
    * <p>Returns a ceiling quotient which is dividend/divisor rounded up to nearest integer</p>
    * @return ceiling quotient of dividend/divisor
    */
   public static int ceilingQuotient(int dividend, int divisor)
   {
      return (
         (dividend >= 0)
            ? ((dividend + divisor - 1) / divisor)
            : (dividend / divisor));
   }

   /**
    * <p>Returns a ceiling quotient which is dividend/divisor rounded up to nearest integer</p>
    * @return ceiling quotient of dividend/divisor
    */
   public static long ceilingQuotient(long dividend, long divisor)
   {
      return (
         (dividend >= 0)
            ? ((dividend + divisor - 1) / divisor)
            : (dividend / divisor));
   }

   /**
    * <p>Returns a ceiling quotient which is dividend/divisor rounded down to nearest integer</p>
    * @return floored quotient of dividend/divisor
    */
   public static int flooredQuotient(int dividend, int divisor)
   {
      return (
         (dividend >= 0)
            ? (dividend / divisor)
            : ((dividend - divisor + 1) / divisor));
   }

   /**
    * <p>Returns a ceiling quotient which is dividend/divisor rounded down to nearest integer</p>
    * @return floored quotient of dividend/divisor
    */
   public static long flooredQuotient(long dividend, long divisor)
   {
      return (
         (dividend >= 0)
            ? (dividend / divisor)
            : ((dividend - divisor + 1) / divisor));
   }

   /**
    * <p>Returns factorial of param</p>
    * @return i factorial
    */
   public static int factorial(int i)
   {
      int n = 1;

      while (i-- > 1)
      {
         n = n * i;
      }

      return n;
   }

   /**
    * <p>Returns summation of param</p>
    * @return i summation
    */
   public static int summation(int i)
   {
      int n = 0;

      while (i > 0)
      {
         n += i;
         i--;
      }

      return n;
   }

   /*******
    * Parses string for an integer, if none found returns Integer(0)
    * @param field String to parse
    * @return Integer value of string
    */
   public static Integer parseNewInteger(String field)
   {
      Integer i;
      try
      {
         i = new Integer(field.trim());
      }
      catch (NumberFormatException ex)
      {
         i = new Integer(0);
      }
      return i;
   }

   /*******
    * Parses string for an integer, if none found returns Integer(0)
    * @param field String to parse
    * @return Integer value of string
    */
   public static Float parseNewFloat(String field)
   {
      Float f;
      try
      {
         f = new Float(field.trim());
      }
      catch (NumberFormatException ex)
      {
         f = new Float(0);
      }
      return f;
   }

   // Conversions to/from byte arrays
   public static byte[] toByteArray(short foo)
   {
      return toByteArray(foo, new byte[2]);
   }

   public static byte[] toByteArray(int foo)
   {
      return toByteArray(foo, new byte[4]);
   }

   public static byte[] toByteArray(long foo)
   {
      return toByteArray(foo, new byte[8]);
   }

   private static byte[] toByteArray(long foo, byte[] array)
   {
      for (int iInd = 0; iInd < array.length; ++iInd)
      {
         array[iInd] = (byte) ((foo >> (iInd * 8)) % 0xFF);
      }
      return array;
   }

   public static short toShort(byte[] foo)
   {
      short rlong = 0;

      for (int iInd = 0; iInd < foo.length; ++iInd)
      {
         rlong += foo[iInd] << (iInd * 8);
      }

      return rlong;
   }

   public static int toInt(byte[] foo)
   {
      int rlong = 0;

      for (int iInd = 0; iInd < foo.length; ++iInd)
      {
         rlong += foo[iInd] << (iInd * 8);
      }

      return rlong;
   }

   public static long toLong(byte[] foo)
   {
      long rlong = 0;

      for (int iInd = 0; iInd < foo.length; ++iInd)
      {
         rlong += (long) (foo[iInd] << (iInd * 8));
      }

      return rlong;
   }

}