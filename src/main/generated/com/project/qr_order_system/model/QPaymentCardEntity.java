package com.project.qr_order_system.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPaymentCardEntity is a Querydsl query type for PaymentCardEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentCardEntity extends EntityPathBase<PaymentCardEntity> {

    private static final long serialVersionUID = 1329084802L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPaymentCardEntity paymentCardEntity = new QPaymentCardEntity("paymentCardEntity");

    public final com.project.qr_order_system.model.common.QBaseEntity _super = new com.project.qr_order_system.model.common.QBaseEntity(this);

    public final StringPath cardName = createString("cardName");

    public final StringPath cardToken = createString("cardToken");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDefault = createBoolean("isDefault");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final QUserEntity user;

    public QPaymentCardEntity(String variable) {
        this(PaymentCardEntity.class, forVariable(variable), INITS);
    }

    public QPaymentCardEntity(Path<? extends PaymentCardEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPaymentCardEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPaymentCardEntity(PathMetadata metadata, PathInits inits) {
        this(PaymentCardEntity.class, metadata, inits);
    }

    public QPaymentCardEntity(Class<? extends PaymentCardEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}

