
package com.example.auction_service.domain.validation;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CompositeBidValidator {
  private final List<BidRule> rules;
  public CompositeBidValidator(List<BidRule> rules){
    this.rules = rules;
  }
//  public void validate(AuctionAggregate agg, UUID playerId, UUID userId, BigDecimal amount){
//    for (BidRule r: rules)
//      r.validate(agg, playerId, userId, amount);
//  }
}
