package tz.mtanav.favorite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    @Transactional
    public FavoriteDto addFavorite(Long userId, Long businessId, Long productId, Long serviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Favorite favorite = Favorite.builder()
                .user(user)
                .businessId(businessId)
                .productId(productId)
                .serviceId(serviceId)
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        return FavoriteDto.builder()
                .id(saved.getId())
                .userId(userId)
                .businessId(saved.getBusinessId())
                .productId(saved.getProductId())
                .serviceId(saved.getServiceId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<FavoriteDto> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(f -> FavoriteDto.builder()
                        .id(f.getId())
                        .userId(f.getUser().getId())
                        .businessId(f.getBusinessId())
                        .productId(f.getProductId())
                        .serviceId(f.getServiceId())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeBusinessFavorite(Long userId, Long businessId) {
        favoriteRepository.deleteByUserIdAndBusinessId(userId, businessId);
    }
}
