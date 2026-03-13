package com.atelie.order.service;

import com.atelie.order.db.Order;
import com.atelie.order.db.OrderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Order> list(Pageable pageable) {
        if (pageable == null) {
            return repo.findAll();
        }
        return repo.findAllBy(pageable).toList();
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return repo.findById(id);
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