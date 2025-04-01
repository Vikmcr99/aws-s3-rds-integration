package br.com.vikmcr99.api.service;

import br.com.vikmcr99.api.domain.coupon.Coupon;
import br.com.vikmcr99.api.domain.coupon.CouponRequestDTO;
import br.com.vikmcr99.api.domain.event.Event;
import br.com.vikmcr99.api.repositories.CouponRepository;
import br.com.vikmcr99.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EventRepository eventRepository;

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO couponRequestDTO) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Coupon coupon = new Coupon();
        coupon.setCode(couponRequestDTO.code());
        coupon.setDiscount(couponRequestDTO.discount());
        coupon.setValid(new Date(couponRequestDTO.valid()));
        coupon.setEvent(event);

        return couponRepository.save(coupon);
    }
}
