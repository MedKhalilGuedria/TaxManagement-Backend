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
@RequestMapping("/user")
public class UserController {
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

	    @PostMapping("/payTax/{taxId}")
	    public ResponseEntity<?> payTax(@PathVariable Long taxId) {
	        Optional<Tax> optionalTax = taxService.getTaxById(taxId);
	        if (optionalTax.isPresent()) {
	            Tax tax = optionalTax.get();
	            if (!tax.isPaid()) {
	                tax.setPaid(true);
	                taxService.updateTax(tax);
	                return ResponseEntity.ok("Tax paid successfully!");
	            } else {
	                return ResponseEntity.badRequest().body("Tax is already paid!");
	            }
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	    }
}