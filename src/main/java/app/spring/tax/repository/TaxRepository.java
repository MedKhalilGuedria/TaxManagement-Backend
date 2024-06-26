package app.spring.tax.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.spring.tax.models.Tax;
import app.spring.tax.models.User;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    List<Tax> findByUser(Optional<User> user);

}
