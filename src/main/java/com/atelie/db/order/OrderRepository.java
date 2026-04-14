package com.atelie.db.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
    Optional<Order> findTopByOrderNumberOrderByCreationDateDesc(int orderNumber);

    Optional<Order> findTopByOrderNumberAndIdIsNotAndStatusNotOrderByCreationDateDesc(
            int orderNumber,
            UUID id,
            OrderStatus status
    );

    List<Order> findAllByStatusAndDueDateLessThanOrderByOrderNumber(
            OrderStatus status,
            Instant dueDate
    );
}
