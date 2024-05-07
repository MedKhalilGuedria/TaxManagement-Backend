package app.spring.tax.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.spring.tax.models.Notification;
import app.spring.tax.models.User;
import app.spring.tax.repository.NotificationRepository;
import app.spring.tax.repository.UserRepository;
@Service

public class NotificationService {
	
	
	@Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public void sendNotification(User user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    public List<Notification> getNotificationsForUser(Long userId) {
        // First, fetch the user by userId
        User user = userRepository.findById(userId).orElse(null);
        
        if (user == null) {
            // Handle the case where user is not found
            return Collections.emptyList();
        }
        
        // Now, retrieve notifications for the user
        return notificationRepository.findByUser(user);
    }

}
