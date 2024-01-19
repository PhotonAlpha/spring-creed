package com.ethan.security.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ReplayLogDto implements Serializable {
    private Long id;
    private String traceId;
    private String nonce;
    private String timestamp;
    private String ip;

    public ReplayLogDto() {
    }

    public ReplayLogDto(String traceId, String nonce, String timestamp) {
        this.traceId = traceId;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }
}
