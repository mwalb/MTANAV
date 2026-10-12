package tz.mtanav.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getProductCategories() {
        return productCategoryRepository.findAll().stream()
                .map(c -> CategoryDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .icon(c.getIcon())
                        .description(c.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getServiceCategories() {
        return serviceCategoryRepository.findAll().stream()
                .map(c -> CategoryDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .icon(c.getIcon())
                        .description(c.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
