package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer getCustomerById(Long id) {
        log.info("Getting customer with id {}", id);

        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("Getting customer with email {}", email);

        return customerRepository.findByEmail(email);
    }
}
