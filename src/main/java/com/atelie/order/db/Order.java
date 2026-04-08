package com.atelie.order.db;

import com.atelie.order.OrderStatus;
import com.atelie.order.PaymentMethod;
import com.atelie.order.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    public static final int DESCRIPTION_MAX_LENGTH = 3000;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false, length = DESCRIPTION_MAX_LENGTH)
    private String comment = "";

    @Column(nullable = false)
    private Instant creationDate = Instant.now();

    @Column
    private Instant dueDate = LocalDateTime.now()
            .plusDays(1)
            .withHour(20)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
            .atZone(ZoneId.systemDefault())
            .toInstant();

    @Column(nullable = false)
    private int orderNumber;

    @Column
    private String acceptedBy;

    @Column
    private String assignedTo;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    @Column
    private int partiallyPaid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.TRANSFER;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.ACCEPTED;

    public Order() {
    }

    public UUID getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getPartiallyPaid() {
        return partiallyPaid;
    }

    public void setPartiallyPaid(int partiallyPaid) {
        this.partiallyPaid = partiallyPaid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}