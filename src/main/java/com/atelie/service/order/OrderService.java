package com.atelie.service.order;

import com.atelie.db.order.Order;
import com.atelie.db.order.OrderRepository;
import com.atelie.db.order.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    public static final Sort DEFAULT_SORTING = Sort.by(
            Sort.Order.desc("creationDate"),
            Sort.Order.desc("id")
    );
    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Page<Order> list(Pageable pageable,
                            String username,
                            boolean onlyMine,
                            boolean onlyActive,
                            Integer orderNumber,
                            OrderStatus orderStatus) {

        return repo.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (onlyMine) {
                predicates.add(cb.equal(
                        root.get("assignedTo").get("username"),
                        username
                ));
            }
            if (onlyActive) {
                predicates.add(cb.equal(root.get("status"), OrderStatus.ACCEPTED));
            }
            if (orderNumber != null) {
                predicates.add(cb.equal(root.get("orderNumber"), orderNumber));
            }
            if (orderStatus != null) {
                predicates.add(cb.equal(root.get("status"), orderStatus));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findByOrderNumber(int orderNumber) {
        return repo.findTopByOrderNumberOrderByCreationDateDesc(orderNumber);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findActiveByOrderNumber(UUID id, int orderNumber) {
        return repo.findTopByOrderNumberAndIdIsNotAndStatusNotOrderByCreationDateDesc(
                orderNumber,
                id,
                OrderStatus.DONE
        );
    }

    @Transactional
    public void save(Order order) {
        repo.saveAndFlush(order);
    }

    @Transactional
    public void delete(Order order) {
        repo.delete(order);
    }
}