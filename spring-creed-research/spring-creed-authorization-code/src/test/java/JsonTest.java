import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class JsonTest {
  @Test
  void testJson() {

    String str = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid authorization code: 5CpskX\"";
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode node = mapper.readTree(str);
      System.out.println(node == null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
