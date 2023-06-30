//import java.io.*;
//import java.util.*;
import java.math.BigInteger;
import java.security.*;

public class Clientfunc extends Thread {
    // sha-256ハッシュ値を返す
    public static String make_hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            return String.format("%064x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
