/*
 * Created on Apr 10, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.endlessloopsoftware.elsutils.security;

import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.StringTokenizer;

/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class AsymmetricEncryption
{
   private PrivateKey privateKey;
   private PublicKey  publicKey;

   public void generateKeys()
   {
      try
      {
         KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
         keyGen.initialize(1024);
         KeyPair pair = keyGen.generateKeyPair();
         this.privateKey = pair.getPrivate();
         this.publicKey = pair.getPublic();
         System.out.println("Public key: " + getString(publicKey.getEncoded()));
         System.out.println("Private key: " + getString(privateKey.getEncoded()));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public String sign(String plaintext)
   {
      try
      {
         Signature dsa = Signature.getInstance("SHA1withDSA");
         dsa.initSign(privateKey);
         dsa.update(plaintext.getBytes());
         byte[] signature = dsa.sign();
         return getString(signature);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public boolean verifySignature(String plaintext, String signature)
   {
      try
      {
         Signature dsa = Signature.getInstance("SHA1withDSA");
         dsa.initVerify(publicKey);

         dsa.update(plaintext.getBytes());
         boolean verifies = dsa.verify(getBytes(signature));
         System.out.println("signature verifies: " + verifies);
         return verifies;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return false;
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
         if (token.length() > 3) { return false; }
         for (int i = 0; i < token.length(); i++)
         {
            if (!Character.isDigit(token.charAt(i))) { return false; }
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
      AsymmetricEncryption pki = new AsymmetricEncryption();
      pki.generateKeys();
      String data = "This is a test";
      String baddata = "This is an test";
      String signature = pki.sign(data);
      String badSignature = signature.substring(0, signature.length() - 1) + "1";
      boolean verifies = pki.verifySignature(data, signature);
      boolean verifiesBad = pki.verifySignature(data, badSignature);
      boolean verifiesBad2 = pki.verifySignature(baddata, signature);

      System.out.println("Data: " + data);
      System.out.println("Signature: " + signature);
      System.out.println("Verifies (good): " + verifies);
      System.out.println("Bad Signature: " + badSignature);
      System.out.println("Verifies (bad): " + verifiesBad);
      System.out.println("Verifies (bad2): " + verifiesBad2);
   }
}

