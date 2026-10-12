package tz.mtanav.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final BusinessServiceRepository businessServiceRepository;

    @Transactional(readOnly = true)
    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(s -> ServiceDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .icon(s.getIcon())
                        .description(s.getDescription())
                        .categoryId(s.getCategoryId())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceDto> getQuickServices() {
        List<String> quickNames = List.of("Garage", "Car Wash", "Salon", "Phone Repair");
        return serviceRepository.findAll().stream()
                .filter(s -> quickNames.stream().anyMatch(qn -> qn.equalsIgnoreCase(s.getName())))
                .map(s -> ServiceDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .icon(s.getIcon())
                        .description(s.getDescription())
                        .categoryId(s.getCategoryId())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BusinessServiceDto> getBusinessServices(Long businessId) {
        return businessServiceRepository.findByBusinessId(businessId).stream()
                .map(bs -> BusinessServiceDto.builder()
                        .id(bs.getId())
                        .businessId(bs.getBusiness().getId())
                        .businessName(bs.getBusiness().getName())
                        .serviceId(bs.getService().getId())
                        .serviceName(bs.getService().getName())
                        .serviceIcon(bs.getService().getIcon())
                        .serviceDescription(bs.getService().getDescription())
                        .price(bs.getPrice())
                        .durationMinutes(bs.getDurationMinutes())
                        .available(bs.isAvailable())
                        .build())
                .collect(Collectors.toList());
    }
}
