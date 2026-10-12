package tz.mtanav.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts()));
    }

    @GetMapping("/quick")
    public ResponseEntity<ApiResponse<List<QuickProductDto>>> getQuickProducts() {
        return ResponseEntity.ok(ApiResponse.success(productService.getQuickProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @GetMapping("/business-product/{id}")
    public ResponseEntity<ApiResponse<BusinessProductDto>> getBusinessProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBusinessProductById(id)));
    }
}
