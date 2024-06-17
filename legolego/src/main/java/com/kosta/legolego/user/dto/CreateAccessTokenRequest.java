package com.kosta.legolego.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.message.StringFormattedMessage;

@Getter
@Setter
public class CreateAccessTokenRequest {
    private String refreshToken;
}
