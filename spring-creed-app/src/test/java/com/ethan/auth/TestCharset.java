package com.ethan.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class TestCharset {
  private final ObjectMapper mapper = new ObjectMapper();

  private Charset charset = Charset.forName("GB18030");

  @Test
  void name() throws IOException {
    String originalRequest = "{\"key\":\"中文das\"}";
    JsonNode req = mapper.readTree(originalRequest);
    InputStream stream = IOUtils.toInputStream(req.toString(), charset);

  }
}
