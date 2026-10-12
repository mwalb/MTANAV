package tz.mtanav.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.mtanav.business.Business;
import tz.mtanav.business.BusinessRepository;
import tz.mtanav.user.User;
import tz.mtanav.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDto createReview(Long userId, CreateReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + request.getBusinessId()));

        Review review = Review.builder()
                .user(user)
                .business(business)
                .productId(request.getProductId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);

        // Update average rating on business
        List<Review> reviews = reviewRepository.findByBusinessIdOrderByCreatedAtDesc(business.getId());
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        business.setRating(Math.round(avg * 10.0) / 10.0);
        business.setReviewCount(reviews.size());
        businessRepository.save(business);

        return ReviewDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .userName(user.getFullName())
                .businessId(business.getId())
                .productId(saved.getProductId())
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> getBusinessReviews(Long businessId) {
        return reviewRepository.findByBusinessIdOrderByCreatedAtDesc(businessId).stream()
                .map(r -> ReviewDto.builder()
                        .id(r.getId())
                        .userId(r.getUser().getId())
                        .userName(r.getUser().getFullName())
                        .businessId(r.getBusiness().getId())
                        .productId(r.getProductId())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
