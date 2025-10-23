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

    /**
     * Retrieves a customer by their unique identifier.
     *
     * @param id the unique identifier of the customer to fetch
     * @return the {@code Customer} object corresponding to the provided id
     * @throws NotFoundException if no customer with the specified id is found
     */
    @Override
    public Customer getCustomerById(Long id) {
        log.info("Getting customer with id {}", id);

        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer with id " + id + " not found"));
    }

    /**
     * Retrieves a customer by their email address.
     *
     * @param email the email address of the customer to fetch
     * @return an {@code Optional<Customer>} containing the customer if found, or an empty {@code Optional} if no customer with the specified email exists
     */
    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        log.info("Getting customer with email {}", email);

        return customerRepository.findByEmail(email);
    }
}
