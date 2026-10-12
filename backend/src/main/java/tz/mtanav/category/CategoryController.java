package tz.mtanav.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.mtanav.common.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getProductCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getProductCategories()));
    }

    @GetMapping("/services")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getServiceCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getServiceCategories()));
    }
}
