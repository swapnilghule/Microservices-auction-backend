package com.example.auction_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuctionAlreadyStartedException extends RuntimeException {
    public AuctionAlreadyStartedException(String message) {
        super(message);
    }
}
