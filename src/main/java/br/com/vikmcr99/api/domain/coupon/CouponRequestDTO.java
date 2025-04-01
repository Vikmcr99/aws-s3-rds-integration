package br.com.vikmcr99.api.domain.coupon;

public record CouponRequestDTO(String code, Integer discount, Long valid) {
}
