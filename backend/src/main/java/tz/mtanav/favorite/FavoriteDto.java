package tz.mtanav.favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDto {
    private Long id;
    private Long userId;
    private Long businessId;
    private Long productId;
    private Long serviceId;
}
