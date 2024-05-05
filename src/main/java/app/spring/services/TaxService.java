package app.spring.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.spring.models.Tax;
import app.spring.repository.TaxRepository;

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
