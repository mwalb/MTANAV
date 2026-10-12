package tz.mtanav.search;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.mtanav.common.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<SearchSuggestionDto>>> getSearchSuggestions(
            @RequestParam(name = "q", required = false) String query) {
        return ResponseEntity.ok(ApiResponse.success(searchService.getSearchSuggestions(query)));
    }
}
