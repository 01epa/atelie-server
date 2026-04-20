package com.atelie.service.notification;

import java.util.List;

public interface NotificationService {
    List<String> send(String message);
}