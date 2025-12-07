package com.project.qr_order_system.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = 1597012965L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final com.project.qr_order_system.model.common.QBaseEntity _super = new com.project.qr_order_system.model.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<OrderEntity, QOrderEntity> orders = this.<OrderEntity, QOrderEntity>createList("orders", OrderEntity.class, QOrderEntity.class, PathInits.DIRECT2);

    public final ListPath<StoreEntity, QStoreEntity> ownedStores = this.<StoreEntity, QStoreEntity>createList("ownedStores", StoreEntity.class, QStoreEntity.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final ListPath<PaymentCardEntity, QPaymentCardEntity> paymentCards = this.<PaymentCardEntity, QPaymentCardEntity>createList("paymentCards", PaymentCardEntity.class, QPaymentCardEntity.class, PathInits.DIRECT2);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}

