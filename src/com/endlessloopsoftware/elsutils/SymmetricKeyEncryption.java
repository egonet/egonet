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

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author admin
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class SymmetricKeyEncryption
{
   private static final String KEY_STRING = "137-140-82-19-194-84-73-173";

   public static String encrypt(String source)
   {
      return encrypt(source.getBytes());
   }

   public static String encrypt(byte[] source)
   {
      try
      {
         // Get our secret key
         Key key = getKey();

         // Create the cipher
         Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

         // Initialize the cipher for encryption
         desCipher.init(Cipher.ENCRYPT_MODE, key);

         // Encrypt the cleartext
         byte[] ciphertext = desCipher.doFinal(source);

         // Return a String representation of the cipher text
         return getString(ciphertext);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public static String generateKey()
   {
      try
      {
         KeyGenerator keygen = KeyGenerator.getInstance("DES");
         SecretKey desKey = keygen.generateKey();
         byte[] bytes = desKey.getEncoded();
         return getString(bytes);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return null;
      }
   }

   public static String decrypt(String source)
   {
      try
      {
         // Get our secret key
         Key key = getKey();

         // Create the cipher
         Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

         // Encrypt the cleartext
         byte[] ciphertext = getBytes(source);

         // Initialize the same cipher for decryption
         desCipher.init(Cipher.DECRYPT_MODE, key);

         // Decrypt the ciphertext
         byte[] cleartext = desCipher.doFinal(ciphertext);

         // Return the clear text
         return new String(cleartext);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   private static Key getKey()
   {
      try
      {
         byte[] bytes = getBytes(KEY_STRING);
         DESKeySpec pass = new DESKeySpec(bytes);
         SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
         SecretKey s = skf.generateSecret(pass);
         return s;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Returns true if the specified text is encrypted, false otherwise
    */
   public static boolean isEncrypted(String text)
   {
      // If the string does not have any separators then it is not
      // encrypted
      if (text.indexOf('-') == -1) {
      ///System.out.println( "text is not encrypted: no dashes" );
      return false; }

      StringTokenizer st = new StringTokenizer(text, "-", false);
      while (st.hasMoreTokens())
      {
         String token = st.nextToken();
         if (token.length() > 3) {
         //System.out.println( "text is not encrypted: length of token greater than 3: " + token );
         return false; }
         for (int i = 0; i < token.length(); i++)
         {
            if (!Character.isDigit(token.charAt(i))) {
            //System.out.println( "text is not encrypted: token is not a digit" );
            return false; }
         }
      }
      //System.out.println( "text is encrypted" );
      return true;
   }

   private static String getString(byte[] bytes)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < bytes.length; i++)
      {
         byte b = bytes[i];
         sb.append((int) (0x00FF & b));
         if (i + 1 < bytes.length)
         {
            sb.append("-");
         }
      }
      return sb.toString();
   }

   private static byte[] getBytes(String str)
   {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      StringTokenizer st = new StringTokenizer(str, "-", false);
      while (st.hasMoreTokens())
      {
         int i = Integer.parseInt(st.nextToken());
         bos.write((byte) i);
      }
      return bos.toByteArray();
   }

   public static void main(String[] args)
   {
      if (args.length < 1)
      {
         System.out.println("Usage: EncryptionUtils <command> <args>");
         System.out.println("\t<command>: encrypt, decrypt, generate-key");
         System.exit(0);
      }
      String command = args[0];
      if (command.equalsIgnoreCase("generate-key"))
      {
         System.out.println("New key: " + SymmetricKeyEncryption.generateKey());
      }
      else if (command.equalsIgnoreCase("encrypt"))
      {
         String plaintext = args[1];
         System.out.println(plaintext + " = " + SymmetricKeyEncryption.encrypt(plaintext));
      }
      else if (command.equalsIgnoreCase("decrypt"))
      {
         String ciphertext = args[1];
         System.out.println(ciphertext + " = " + SymmetricKeyEncryption.decrypt(ciphertext));
      }
   }

   public static void showProviders()
   {
      try
      {
         Provider[] providers = Security.getProviders();
         for (int i = 0; i < providers.length; i++)
         {
            System.out.println("Provider: " + providers[i].getName() + ", " + providers[i].getInfo());
            for (Iterator itr = providers[i].keySet().iterator(); itr.hasNext();)
            {
               String key = (String) itr.next();
               String value = (String) providers[i].get(key);
               System.out.println("\t" + key + " = " + value);
            }

         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}