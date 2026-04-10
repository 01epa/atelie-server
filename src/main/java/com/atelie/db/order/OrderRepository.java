package com.atelie.db.order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    Slice<Order> findAllBy(Pageable pageable);

    Optional<Order> findTopByOrderNumberOrderByCreationDateDesc(int orderNumber);

    Optional<Order> findTopByOrderNumberAndIdIsNotAndStatusNotOrderByCreationDateDesc(
            int orderNumber,
            UUID id,
            OrderStatus status
    );
}
