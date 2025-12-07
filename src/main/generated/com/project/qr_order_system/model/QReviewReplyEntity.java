package com.project.qr_order_system.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewReplyEntity is a Querydsl query type for ReviewReplyEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewReplyEntity extends EntityPathBase<ReviewReplyEntity> {

    private static final long serialVersionUID = 1778938046L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewReplyEntity reviewReplyEntity = new QReviewReplyEntity("reviewReplyEntity");

    public final com.project.qr_order_system.model.common.QBaseEntity _super = new com.project.qr_order_system.model.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath replyContent = createString("replyContent");

    public final QReviewEntity review;

    public final NumberPath<Long> reviewReplyId = createNumber("reviewReplyId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final QUserEntity user;

    public QReviewReplyEntity(String variable) {
        this(ReviewReplyEntity.class, forVariable(variable), INITS);
    }

    public QReviewReplyEntity(Path<? extends ReviewReplyEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewReplyEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewReplyEntity(PathMetadata metadata, PathInits inits) {
        this(ReviewReplyEntity.class, metadata, inits);
    }

    public QReviewReplyEntity(Class<? extends ReviewReplyEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.review = inits.isInitialized("review") ? new QReviewEntity(forProperty("review"), inits.get("review")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}

