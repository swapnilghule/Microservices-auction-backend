package com.example.auction_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuctionisAlreadyClosedExcpetion extends RuntimeException  {

    public AuctionisAlreadyClosedExcpetion(String message) {
        super(message);
    }
}
