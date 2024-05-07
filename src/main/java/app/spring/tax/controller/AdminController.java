package app.spring.tax.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.spring.tax.models.Tax;
import app.spring.tax.repository.TaxRepository;
import app.spring.tax.services.TaxService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
	@Autowired
    private TaxService taxService;

    @GetMapping("/taxes")
    public ResponseEntity<List<Tax>> getAllTaxes() {
        List<Tax> taxes = taxService.getAllTaxes();
        return ResponseEntity.ok(taxes);
    }

    @GetMapping("/tax/{id}")
    public ResponseEntity<Tax> getTaxById(@PathVariable Long id) {
        Optional<Tax> tax = taxService.getTaxById(id);
        return tax.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/tax")
    public ResponseEntity<Tax> createTax(@RequestBody Tax tax) {
        Tax createdTax = taxService.createTax(tax);
        return ResponseEntity.ok(createdTax);
    }

    @PutMapping("/tax/{id}")
    public ResponseEntity<Tax> updateTax(@PathVariable Long id, @RequestBody Tax taxDetails) {
        Optional<Tax> optionalTax = taxService.getTaxById(id);
        if (optionalTax.isPresent()) {
            Tax existingTax = optionalTax.get();
            existingTax.setAmount(taxDetails.getAmount());
            existingTax.setType(taxDetails.getType());
            Tax updatedTax = taxService.updateTax(existingTax);
            return ResponseEntity.ok(updatedTax);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/tax/{id}")
    public ResponseEntity<?> deleteTax(@PathVariable Long id) {
        taxService.deleteTax(id);
        return ResponseEntity.ok("Tax deleted successfully!");
    }
}