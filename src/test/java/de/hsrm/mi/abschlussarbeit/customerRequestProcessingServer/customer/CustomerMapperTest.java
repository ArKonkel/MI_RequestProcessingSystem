package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class CustomerMapperTest {

    private final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void toDto() {
        // GIVEN
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setEmail("max.mustermann@example.com");
        customer.setAddress("Musterstraße 1, 12345 Musterstadt");

        // WHEN
        CustomerDto dto = customerMapper.toDto(customer);

        // THEN
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Max", dto.firstName());
        assertEquals("Mustermann", dto.lastName());
        assertEquals("max.mustermann@example.com", dto.email());
        assertEquals("Musterstraße 1, 12345 Musterstadt", dto.address());
    }
}
