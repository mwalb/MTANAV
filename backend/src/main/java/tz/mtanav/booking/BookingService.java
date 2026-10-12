package tz.mtanav.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.service.BusinessServiceEntity;
import tz.mtanav.service.BusinessServiceRepository;
import tz.mtanav.service.ServiceEntity;
import tz.mtanav.service.ServiceRepository;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessServiceRepository businessServiceRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingDto createBooking(Long customerId, CreateBookingRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + request.getBusinessId()));

        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + request.getServiceId()));

        BusinessServiceEntity bs = businessServiceRepository.findByBusinessId(business.getId()).stream()
                .filter(b -> b.getService().getId().equals(service.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This business does not offer the requested service"));

        Booking booking = Booking.builder()
                .customer(customer)
                .business(business)
                .service(service)
                .bookingStatus(BookingStatus.PENDING)
                .bookingDate(request.getBookingDate())
                .bookingTime(request.getBookingTime())
                .price(bs.getPrice())
                .notes(request.getNotes())
                .build();

        return mapToBookingDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBusinessBookings(Long businessId) {
        return bookingRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                .map(this::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDto updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        booking.setBookingStatus(status);
        return mapToBookingDto(bookingRepository.save(booking));
    }

    public BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getFullName())
                .businessId(booking.getBusiness().getId())
                .businessName(booking.getBusiness().getName())
                .serviceId(booking.getService().getId())
                .serviceName(booking.getService().getName())
                .bookingStatus(booking.getBookingStatus().name())
                .bookingDate(booking.getBookingDate())
                .bookingTime(booking.getBookingTime())
                .price(booking.getPrice())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
