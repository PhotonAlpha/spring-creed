package com.ethan.std.provider;

import com.ethan.std.provisioning.Oauth2ClientDetails;
import com.ethan.std.provisioning.Oauth2ClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/3/2022 6:29 PM
 */
public class CustomizeClientDetailsService implements ClientDetailsService, ClientRegistrationService {
    private Oauth2ClientDetailsRepository clientDetailsRepository;
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Optional<Oauth2ClientDetails> detailsOptional = clientDetailsRepository.findByClientId(clientId);
        return detailsOptional.orElseThrow(() ->
                new NoSuchClientException("No client with requested id: " + clientId)
        );
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        try {
            Oauth2ClientDetails clientDtl = (Oauth2ClientDetails) clientDetails;
            String encryptedPwd = clientDtl.getClientSecret() != null ? passwordEncoder.encode(clientDtl.getClientSecret())
                    : null;
            clientDtl.setClientSecret(encryptedPwd);
            clientDetailsRepository.save(clientDtl);
        } catch (DuplicateKeyException e) {
            throw new ClientAlreadyExistsException("Client already exists: " + clientDetails.getClientId(), e);
        }
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {

        if (clientDetailsRepository.findByClientId(clientDetails.getClientId()).isPresent()) {
            clientDetailsRepository.save((Oauth2ClientDetails) clientDetails);
        } else {
            throw new NoSuchClientException("No client found with id = " + clientDetails.getClientId());
        }
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        Optional<Oauth2ClientDetails> detailsOptional = clientDetailsRepository.findByClientId(clientId);
        if (detailsOptional.isPresent()) {
            String newSecret = passwordEncoder.encode(secret);
            Oauth2ClientDetails clientDetails = detailsOptional.get();
            clientDetails.setClientSecret(newSecret);
            clientDetailsRepository.save(clientDetails);
        } else {
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        Optional<Oauth2ClientDetails> detailsOptional = clientDetailsRepository.findByClientId(clientId);
        if (detailsOptional.isPresent()) {
            clientDetailsRepository.delete(detailsOptional.get());
        } else {
            throw new NoSuchClientException("No client found with id = " + clientId);
        }
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        Iterable<Oauth2ClientDetails> detailsIterable = clientDetailsRepository.findAll();
        return StreamSupport.stream(detailsIterable.spliterator(), false)
                .sorted(Comparator.comparing(ClientDetails::getClientId))
                .collect(Collectors.toList());
    }

    @Autowired
    public void setClientDetailsRepository(Oauth2ClientDetailsRepository clientDetailsRepository) {
        this.clientDetailsRepository = clientDetailsRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
