package com.framework.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Restful Booker /booking resource. Records give value equality, so a whole booking can be compared in one assert. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Booking(String firstname, String lastname, Integer totalprice, Boolean depositpaid,
                      BookingDates bookingdates, String additionalneeds) {
}
