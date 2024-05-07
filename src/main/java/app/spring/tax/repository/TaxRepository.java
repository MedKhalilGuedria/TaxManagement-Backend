package app.spring.tax.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.spring.tax.models.Tax;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
}
