package com.project.qr_order_system.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStoreEntity is a Querydsl query type for StoreEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreEntity extends EntityPathBase<StoreEntity> {

    private static final long serialVersionUID = 1921802925L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStoreEntity storeEntity = new QStoreEntity("storeEntity");

    public final com.project.qr_order_system.model.common.QBaseEntity _super = new com.project.qr_order_system.model.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUserEntity owner;

    public final ListPath<ProductEntity, QProductEntity> products = this.<ProductEntity, QProductEntity>createList("products", ProductEntity.class, QProductEntity.class, PathInits.DIRECT2);

    public final StringPath storeName = createString("storeName");

    public final EnumPath<StoreType> storeType = createEnum("storeType", StoreType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QStoreEntity(String variable) {
        this(StoreEntity.class, forVariable(variable), INITS);
    }

    public QStoreEntity(Path<? extends StoreEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStoreEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStoreEntity(PathMetadata metadata, PathInits inits) {
        this(StoreEntity.class, metadata, inits);
    }

    public QStoreEntity(Class<? extends StoreEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.owner = inits.isInitialized("owner") ? new QUserEntity(forProperty("owner")) : null;
    }

}

