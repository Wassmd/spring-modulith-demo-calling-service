package com.paxier.spring_modulith_demo_calling_service.call;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
public class CallRestClient {

  private final RestClient restClient;
  private final OAuth2TokenService tokenService;

  public CallRestClient(RestClient.Builder restClient, OAuth2TokenService tokenService) {
    this.restClient = restClient.baseUrl("http://localhost:8080").build();
    this.tokenService = tokenService;
  }

  public Customer getCustomerInfo(int customerId) {
    return restClient.get()
        .uri("/customers/"+customerId)
        .header("Authorization", "Bearer " + tokenService.getAccessToken())
        .retrieve()
        .body(Customer.class);
  }

  public Customer createCustomer(Customer customer) {
    return restClient.post()
        .uri("/customers")
        .body(customer)
        .header("Authorization", "Bearer " + tokenService.getAccessToken())
        .retrieve()
        .body(Customer.class);
  }
}
