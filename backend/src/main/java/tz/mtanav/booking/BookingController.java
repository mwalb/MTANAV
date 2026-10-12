package tz.mtanav.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;
import tz.mtanav.security.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.createBooking(user.getId(), request), "Booking created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingDto>>> getMyBookings(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getCustomerBookings(user.getId())));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<List<BookingDto>>> getBusinessBookings(@PathVariable Long businessId) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBusinessBookings(businessId)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BookingDto>> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.updateBookingStatus(id, status), "Booking status updated"));
    }
}
