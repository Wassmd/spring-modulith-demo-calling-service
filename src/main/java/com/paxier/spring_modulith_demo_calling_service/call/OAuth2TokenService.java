package com.paxier.spring_modulith_demo_calling_service.call;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2TokenService {

  private final OAuth2AuthorizedClientManager authorizedClientManager;

  /**
   * Retrieves an OAuth2 access token for the specified client registration.
   * spring-modulith-call the client registration ID from application.yaml
   * @return the access token value
   * @throws IllegalStateException if unable to authorize the client
   */
  public String getAccessToken() {
    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId("spring-modulith-call")
        .principal(new AnonymousAuthenticationToken(
            "key", "anonymousUser",
            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")))
        .build();

    OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

    if (authorizedClient == null) {
      throw new IllegalStateException("Unable to authorize client: " + "spring-modulith-call");
    }

    return authorizedClient.getAccessToken().getTokenValue();
  }
}

