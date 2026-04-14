package com.atelie.service.notification;

import com.atelie.db.order.Order;
import com.atelie.service.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.atelie.ui.order.OrdersListView.ORDERS;

@Component
public class NotificationJob {
    @Value("${server.host}")
    private String serverHost;
    @Value("${server.port}")
    private int serverPort;

    private static final Logger log = LoggerFactory.getLogger(NotificationJob.class);

    private final OrderService orderService;

    private final List<NotificationService> notificationServices;
    private final int maxAttempts;
    private final long retryDelayMs;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("ru"));

    public NotificationJob(
            OrderService orderService,
            List<NotificationService> notificationServices,
            @Value("${app.scheduler.max-attempts}") int maxAttempts,
            @Value("${app.scheduler.retry-delay-seconds}") long retryDelaySeconds
    ) {
        this.orderService = orderService;
        this.notificationServices = notificationServices;
        this.maxAttempts = maxAttempts;
        this.retryDelayMs = retryDelaySeconds;
    }

    @Scheduled(
            cron = "${app.scheduler.cron}",
            zone = "${app.scheduler.zone}"
    )
    public void run() {
        if (notificationServices.isEmpty()) {
            log.info("No notification services enabled");
            return;
        }
        List<Order> orders = orderService.getActiveOrders();
        String message = buildMessage(orders);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                for (NotificationService service : notificationServices) {
                    service.send(message);
                }
                log.info("Notification sent");
                return;
            } catch (Exception e) {
                log.error("Attempt {} failed", attempt, e);
                if (attempt == maxAttempts) {
                    log.error("All attempts failed. Giving up for today.");
                    return;
                }
                try {
                    Thread.sleep(retryDelayMs * 1000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public String buildMessage(List<Order> orders) {
        if (orders.isEmpty()) {
            return "Сегодня нет заказов";
        }

        ZoneId zone = ZoneId.of("Europe/Moscow");

        String date = LocalDate.now(zone).format(formatter);

        int total = orders.stream()
                .mapToInt(Order::getPrice)
                .sum();

        List<Order> overdue = orders.stream()
                .filter(o -> o.getDueDate().isBefore(Instant.now()))
                .toList();

        List<Order> active = orders.stream()
                .filter(o -> !o.getDueDate().isBefore(Instant.now()))
                .toList();

        Map<String, List<Order>> groupedActive = active.stream()
                .filter(o -> o.getAssignedTo() != null)
                .collect(Collectors.groupingBy(o -> o.getAssignedTo().getFullName()));

        List<Order> activeWithoutExecutor = active.stream()
                .filter(o -> o.getAssignedTo() == null)
                .toList();

        StringBuilder sb = new StringBuilder();

        sb.append("📦 <b>Заказы к выполнению ")
                .append(date)
                .append(" — ")
                .append(orders.size())
                .append(" шт. на ")
                .append(total)
                .append(" ₽</b>\n\n");

        // 🔴 ПРОСРОЧЕННЫЕ
        if (!overdue.isEmpty()) {
            sb.append("🟥 <b>Просроченные заказы (")
                    .append(overdue.size())
                    .append(" шт.)</b>\n");
            addListOfOrders(overdue, sb);
        }

        // 🟡 АКТИВНЫЕ — по исполнителям
        groupedActive.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    sb.append("👤 <b>")
                            .append(entry.getKey())
                            .append(" (")
                            .append(entry.getValue().size())
                            .append(" шт.)")
                            .append("</b>\n");
                    addListOfOrders(entry.getValue(), sb);
                });

        // 🟡 Общие
        if (!activeWithoutExecutor.isEmpty()) {
            sb.append("📌 <b>Общие")
                    .append(" (")
                    .append(activeWithoutExecutor.size())
                    .append(" шт.)")
                    .append("</b>\n");
            addListOfOrders(activeWithoutExecutor, sb);
        }

        return sb.toString();
    }

    private void addListOfOrders(List<Order> orders,
                                 StringBuilder sb) {
        orders.stream()
                .sorted(Comparator.comparing(Order::getOrderNumber))
                .forEach(o -> sb.append("🔹 № ")
                        .append("<a href=\"")
                        .append(serverHost)
                        .append(":")
                        .append(serverPort)
                        .append("/")
                        .append(ORDERS)
                        .append("/")
                        .append(o.getOrderNumber())
                        .append("\">")
                        .append(o.getOrderNumber())
                        .append("</a>")
                        .append(" — ")
                        .append(o.getPrice())
                        .append(" ₽\n"));
        sb.append("\n");
    }
}