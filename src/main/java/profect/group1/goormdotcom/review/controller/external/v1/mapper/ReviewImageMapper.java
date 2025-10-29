package profect.group1.goormdotcom.review.controller.external.v1.mapper;

import profect.group1.goormdotcom.review.domain.ReviewImage;
import profect.group1.goormdotcom.review.repository.entity.ReviewImageEntity;

import java.util.Locale;

public class ReviewImageMapper {
    // 1. ReviewImageEntity -> ReviewImage (imageUrl 없이)
    public static ReviewImage toDomain(ReviewImageEntity entity) {
        if (entity == null) return null;

        return new ReviewImage(
                entity.getId(),
                entity.getReviewId(),
                entity.getFileId()
        );
    }



    //2. Entity -> Domain (imageUrl 있음)
    public static ReviewImage toDomainWithUrl(ReviewImageEntity entity, String imageUrl) {
        if (entity == null) return null;

        return new ReviewImage(
                entity.getId(),
                entity.getReviewId(),
                entity.getFileId(),
                imageUrl,
                entity.getDeletedAt()
        );
    }
}
