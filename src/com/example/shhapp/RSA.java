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

  RSA() {
    BigInteger p = new BigInteger(
        "20866471414292806296710351110199588239732877470724895566972720613733511927176978703316961801501936335396037"
            + "96206466706104126357097436166991687119954817525958578593324694034898260237547317815763781575731746521925732693"
            + "64680038242124456319846190336172119941631001395905282683523976377570122450119020325488320857145535616851976511"
            + "14981124276270668046647556227338538302948331769542644358404898070692694773436985000105375509749505292594557562"
            + "28660834098602721925608454646065059869175478311926931254701437006966230361186520834001307948075057731738651441"
            + "67107122625359210096447426515399147490857784664420857156772792661098395375072042408598337368115204659885190564"
            + "006638780119032780198753545585000191631483778292315898754502058314531820142912053546113851109003");
    BigInteger q = new BigInteger(
        "234684479"
            + "26983557229223845406712099045097219112568218723570336798176400159057655046540865688192268220863053536964273717"
            + "66556577598316702443335374809933657938079170445814334937572559532859403324118122251897283734949166711184309407"
            + "62460877239311272226077062095323519403438729051346300562082169152549318549019304954442511497740263077724422232"
            + "59789839429947799459386158631348270469408516810949914567990214649733224002210529837175595942125347693631074889"
            + "69426973525847500588622991280162036449342904135137324865073294693921271141130600720455413004753902752165577658"
            + "79229870650885420288854808244549436408367793903620782852820612783546268541470634475118897414538451342884374183"
            + "046320978832247228449953924269993686973618540097234321325766274461771172027230886017");
    BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
    modulus = p.multiply(q);
    /** Note: RSA publicKey is (publicKey,modulus) **/
    publicKey = new BigInteger("65537"); // common value in practice = 2^16 + 1
    /** Note: RSA privateKey is (privateKey,modulus) **/
    privateKey = publicKey.modInverse(phi);
    System.out.println("P: " + p + "  Q: " + q );
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