package org.jabst.jabs.util;

// MessageDigest for SHA256 hash
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digest {

    /** Takes the SHA-256 digest of the String message
      * @param message The message to take the digest of
      * @return A byte[] of the digest, of fixed length 32 bytes
      */
    public static byte[] sha256(String message) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("No such algorith as SHA-256?");
            e.printStackTrace(System.err);
            return null;
        }
        return md.digest(message.getBytes());
    }

    /**
     * Returns a String representing the digest given in a byte array
     * Not currently used
     * @param digest the digest to convert to a hexadecimal string
     * @return A string representing the byte[] in hexadecimal text
     */
    public static String digestToHexString(byte[] digest) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < digest.length; ++i) {
            s.append(String.format("%x", digest[i]));
        }
        return s.toString();
    }
}