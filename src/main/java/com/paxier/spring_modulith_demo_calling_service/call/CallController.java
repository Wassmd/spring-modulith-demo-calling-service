package com.paxier.spring_modulith_demo_calling_service.call;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CallController {

  private final CallRestClient client;

  @GetMapping({"/{customerId}"})
  ResponseEntity<Customer> call(@PathVariable int customerId) {
    return ResponseEntity.ok(client.getCustomerInfo(customerId));
  }

  @PostMapping
  ResponseEntity<Customer> call(@RequestBody Customer customer) {
    return ResponseEntity.ok().body(client.createCustomer(customer));
  }
}
