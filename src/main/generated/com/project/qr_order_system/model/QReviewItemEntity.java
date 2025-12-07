package com.project.qr_order_system.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewItemEntity is a Querydsl query type for ReviewItemEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewItemEntity extends EntityPathBase<ReviewItemEntity> {

    private static final long serialVersionUID = -1377493659L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewItemEntity reviewItemEntity = new QReviewItemEntity("reviewItemEntity");

    public final QOrderItemEntity orderItem;

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final QReviewEntity review;

    public final NumberPath<Long> reviewItemId = createNumber("reviewItemId", Long.class);

    public QReviewItemEntity(String variable) {
        this(ReviewItemEntity.class, forVariable(variable), INITS);
    }

    public QReviewItemEntity(Path<? extends ReviewItemEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewItemEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewItemEntity(PathMetadata metadata, PathInits inits) {
        this(ReviewItemEntity.class, metadata, inits);
    }

    public QReviewItemEntity(Class<? extends ReviewItemEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.orderItem = inits.isInitialized("orderItem") ? new QOrderItemEntity(forProperty("orderItem"), inits.get("orderItem")) : null;
        this.review = inits.isInitialized("review") ? new QReviewEntity(forProperty("review"), inits.get("review")) : null;
    }

}

