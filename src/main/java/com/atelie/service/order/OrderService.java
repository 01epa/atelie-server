package com.atelie.service.order;

import com.atelie.db.order.OrderStatus;
import com.atelie.db.order.Order;
import com.atelie.db.order.OrderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Order> list(Pageable pageable) {
        if (pageable == null) {
            return repo.findAll(DEFAULT_SORTING);
        }
        return repo.findAllBy(pageable).toList();
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