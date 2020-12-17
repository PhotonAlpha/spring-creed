import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

public class PasswordEncoderTest {
  @Test
  void testEncoder() {

    //PasswordEncoder pwdEncoder = NoOpPasswordEncoder.getInstance();
    //String res = pwdEncoder.encode("112233");
    //System.out.println(res);
    //PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
    //boolean match = encoder.matches("112233", "{noop}112233");


    try {
      Map<String, String> values = new LinkedHashMap<String, String>();
      values.put("key", "value");
      values.put("key2", "value2");
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] bytes = digest.digest(values.toString().getBytes("UTF-8"));


      String str = String.format("%032x", new BigInteger(1, bytes));
      System.out.println(str);
      System.out.println(bCrypt.encode(values.toString()));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

  }
}
