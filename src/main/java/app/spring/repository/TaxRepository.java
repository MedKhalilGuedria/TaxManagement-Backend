package app.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.spring.models.Tax;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
}
