package com.example.shhapp;

/*************************************************************************
 *  Compilation:  javac RSA.java
 *  Execution:    java RSA N
 *  
 *  Generate an N-bit public and private RSA key and use to encrypt
 *  and decrypt a random message.
 * 
 *  % java RSA 50
 *  public  = 65537
 *  private = 553699199426609
 *  modulus = 825641896390631
 *  message   = 48194775244950
 *  encrpyted = 321340212160104
 *  decrypted = 48194775244950
 *
 *  Known bugs (not addressed for simplicity)
 *  -----------------------------------------
 *  - It could be the case that the message >= modulus. To avoid, use
 *    a do-while loop to generate key until modulus happen to be exactly N bits.
 *
 *  - It's possible that gcd(phi, publicKey) != 1 in which case
 *    the key generation fails. This will only happen if phi is a
 *    multiple of 65537. To avoid, use a do-while loop to generate
 *    keys until the gcd is 1.
 *  
 *************************************************************************/
import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
  private final static BigInteger one = new BigInteger("1");
  private final static SecureRandom random = new SecureRandom();

  private BigInteger privateKey;
  private BigInteger publicKey;
  private BigInteger modulus;

  // generate an N-bit (roughly) public and private key
  RSA(int N) {
    BigInteger p = BigInteger.probablePrime(N / 2, random);
    BigInteger q = BigInteger.probablePrime(N / 2, random);
    BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
    modulus = p.multiply(q);
    /** Note: RSA publicKey is (publicKey,modulus) **/
    publicKey = new BigInteger("65537"); // common value in practice = 2^16 + 1
    /** Note: RSA privateKey is (privateKey,modulus) **/
    privateKey = publicKey.modInverse(phi);
    System.out.println("P: " + p + "  Q: " + q + "  phi: " + phi + " N: "
        + modulus);
  }

  BigInteger encrypt(BigInteger message) {
    return message.modPow(publicKey, modulus);
  }

  BigInteger decrypt(BigInteger encrypted) {
    return encrypted.modPow(privateKey, modulus);
  }

  public String toString() {
    String s = "";
    s += "public  = " + publicKey + "\n";
    s += "private = " + privateKey + "\n";
    s += "modulus = " + modulus;
    return s;
  }
}