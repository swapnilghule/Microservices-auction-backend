package com.example.bidding;
import org.springframework.boot.SpringApplication; import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication @EnableScheduling
@ComponentScan(basePackages = {
        "com.example.bidding",  // your gateway beans
        "com.example.common"        // common module beans
})
public class BiddingServiceApplication {
    public static void main(String[] args){

        SpringApplication.run(BiddingServiceApplication.class,args);
    }
}