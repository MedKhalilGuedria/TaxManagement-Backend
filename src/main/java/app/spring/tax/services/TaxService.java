package app.spring.tax.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.spring.tax.models.Tax;
import app.spring.tax.models.User;
import app.spring.tax.repository.TaxRepository;

import java.util.List;
import java.util.Optional;


@Service
public class TaxService {
	
	@Autowired
    private TaxRepository taxRepository;

    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }

    public Optional<Tax> getTaxById(Long id) {
        return taxRepository.findById(id);
    }
    
    public List<Tax> getTaxesByUser(Optional<User> user) {
        return taxRepository.findByUser(user);
    }

    public Tax createTax(Tax tax) {
        tax.setPaid(false); // Set tax status to unpaid by default
        return taxRepository.save(tax);
    }

    public Tax updateTax(Tax tax) {
        return taxRepository.save(tax);
    }

    public void deleteTax(Long id) {
        taxRepository.deleteById(id);
    }

}
