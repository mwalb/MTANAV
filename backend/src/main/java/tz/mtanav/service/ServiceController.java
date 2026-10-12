package tz.mtanav.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceDto>>> getAllServices() {
        return ResponseEntity.ok(ApiResponse.success(serviceService.getAllServices()));
    }

    @GetMapping("/quick")
    public ResponseEntity<ApiResponse<List<ServiceDto>>> getQuickServices() {
        return ResponseEntity.ok(ApiResponse.success(serviceService.getQuickServices()));
    }
}
