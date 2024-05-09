package app.spring.tax.controller;

import app.spring.tax.dto.AuthResponse;
import app.spring.tax.config.JwtUtil;
import app.spring.tax.models.Notification;
import app.spring.tax.models.Tax;
import app.spring.tax.models.User;
import app.spring.tax.services.NotificationService;
import app.spring.tax.services.TaxService;
import app.spring.tax.services.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/app")
public class AppController {
	private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final UserService userService;
    @Autowired
    private TaxService taxService;
    @Autowired
    private NotificationService notificationService;
    

    @Autowired
    public  AppController(AuthenticationManager authenticationManager, JwtUtil jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        final String role = userService.getUserRole(authenticationRequest.getUsername());
        System.out.println(role);// Fetch user role
        AuthResponse response = new AuthResponse(token, role);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
    }
    
    

    
    @PostMapping("/tax")
    public ResponseEntity<Tax> createTax(@RequestBody Tax tax) {
        Tax createdTax = taxService.createTax(tax);
        return ResponseEntity.ok(createdTax);
    }

   
    
    @PostMapping("/assignTax/{userId}")
    public ResponseEntity<Tax> assignTaxToUser(@PathVariable Long userId, @RequestBody Tax tax) {
        Optional<User> optionalUser = userService.getUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            tax.setUser(user);
            Tax assignedTax = taxService.createTax(tax);
            if (user != null) {
                String message = "You have a new tax assigned.";
                notificationService.sendNotification(user, message);
            }
            return ResponseEntity.ok(assignedTax);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/tax/{id}")
    public ResponseEntity<?> deleteTax(@PathVariable Long id) {
        taxService.deleteTax(id);
        return ResponseEntity.ok("Tax deleted successfully!");
    }
    
    
    @GetMapping("/taxes")
    public ResponseEntity<List<Tax>> getAllTaxes() {
        List<Tax> taxes = taxService.getAllTaxes();
        return ResponseEntity.ok(taxes);
    }
    
    
    @GetMapping("/{userId}/taxes")
    public ResponseEntity<List<Tax>> getTaxesByUserId(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Tax> taxes = taxService.getTaxesByUser(user);
        return ResponseEntity.ok(taxes);
    }
    
    

    @GetMapping("/tax/{id}")
    public ResponseEntity<Tax> getTaxById(@PathVariable Long id) {
        Optional<Tax> tax = taxService.getTaxById(id);
        return tax.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{userId}/notifications")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
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
