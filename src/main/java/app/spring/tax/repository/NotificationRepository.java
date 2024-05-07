package app.spring.tax.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.spring.tax.models.Notification;
import app.spring.tax.models.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	
    List<Notification> findByUser(User user);

}