package br.com.vikmcr99.api.controller;

import br.com.vikmcr99.api.domain.coupon.Coupon;
import br.com.vikmcr99.api.domain.coupon.CouponRequestDTO;
import br.com.vikmcr99.api.domain.event.Event;
import br.com.vikmcr99.api.repositories.CouponRepository;
import br.com.vikmcr99.api.repositories.EventRepository;
import br.com.vikmcr99.api.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;


    @PostMapping
    public ResponseEntity<Coupon> addCouponsToEvent(@PathVariable UUID eventId, @RequestBody CouponRequestDTO couponRequestDTO) {
        return new ResponseEntity<>(couponService.addCouponToEvent(eventId, couponRequestDTO),HttpStatus.CREATED);
    }


}
