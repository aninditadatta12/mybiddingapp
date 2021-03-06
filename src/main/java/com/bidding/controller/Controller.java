package com.bidding.controller;

import com.bidding.domain.AuctionEntity;
import com.bidding.dto.AuctionResponseDTO;
import com.bidding.dto.BiddingRequest;
import com.bidding.dto.User;
import com.bidding.security.component.TokenGeneration;
import com.bidding.service.AuctionService;
import com.bidding.util.AuctionStatus;
import com.bidding.util.BidStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Abhay Pandit
 */
@RestController
public class Controller {

    private final AuctionService auctionService;
   private final  TokenGeneration tokenGeneration;

    public Controller(AuctionService auctionService, TokenGeneration tokenGeneration) {
        this.auctionService = auctionService;
        this.tokenGeneration = tokenGeneration;
    }

    @GetMapping("/auction")
    public ResponseEntity getActions(@RequestParam(value = "status", required = false) String status,
                                     @RequestParam(defaultValue = "0") Integer pageNo,
                                     @RequestParam(defaultValue = "2") Integer pageSize) {
        List<AuctionResponseDTO> allAuctions = auctionService.getAllAuctions(
                (status == null ? AuctionStatus.RUNNING : status).toString(),pageNo, pageSize);
        return new ResponseEntity(allAuctions, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/user")
    public User getUserToken(@RequestParam("user") String username, @RequestParam("password") String pwd) {
        String token = tokenGeneration.getToken(username);
        User user = new User();
        user.setUserName(username);
        user.setToken(token);
        return user;
    }

    @PostMapping("/placeBid")
    public ResponseEntity placeBid(@RequestBody BiddingRequest biddingRequest) {
        BidStatus bidStatus = auctionService.placeBid(biddingRequest);
        return new ResponseEntity<>(bidStatus.getReason(), new HttpHeaders(), bidStatus.getCode());
    }
}
