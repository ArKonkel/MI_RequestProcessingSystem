package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.Customer;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Needs to be a SpringBootTest because it has dependencies to other mappers
@SpringBootTest
class CustomerRequestMapperTest {

    @Autowired
    private CustomerRequestMapper customerRequestMapper;

    @Test
    void toDto() {
        Instant creationDate = Instant.parse("2025-09-01T12:00:00.00Z");
        
        // GIVEN
        Customer customer = new Customer();
        customer.setId(5L);
        customer.setFirstName("Anna");
        customer.setLastName("Schmidt");
        customer.setEmail("anna.schmidt@example.com");
        customer.setAddress("Beispielstraße 12, 54321 Beispielstadt");

        CustomerRequest request = new CustomerRequest();
        request.setId(10L);
        request.setPriority(Priority.HIGH);
        request.setEstimatedScope(BigDecimal.valueOf(15));
        request.setStatus(CustomerRequestStatus.RECEIVED);
        request.setChargeable(Chargeable.NOT_DETERMINED);
        request.setCategory(Category.BUG_REPORT);

        request.setCustomer(customer);
        request.setTitle("Feature Request");
        request.setDescription("Add export functionality");
        request.setCreationDate(creationDate);
        request.setAssignee(null);

        // WHEN
        CustomerRequestDto dto = customerRequestMapper.toDto(request);

        // THEN
        assertNotNull(dto);
        assertNotNull(dto.getProcessItem());
        assertEquals("Feature Request", dto.getProcessItem().getTitle());
        assertEquals("Add export functionality", dto.getProcessItem().getDescription());
        assertEquals(creationDate, dto.getProcessItem().getCreationDate());
        assertNull(dto.getProcessItem().getAssigneeId());

        assertEquals(Priority.HIGH, dto.getPriority());
        assertEquals(BigDecimal.valueOf(15).longValue(), dto.getEstimatedScope());
        assertEquals(CustomerRequestStatus.RECEIVED, dto.getStatus());
        assertEquals(Chargeable.NOT_DETERMINED, dto.getChargeable());
        assertEquals(Category.BUG_REPORT, dto.getCategory());

        assertNotNull(dto.getCustomer());
        assertEquals(5L, dto.getCustomer().id());
        assertEquals("Anna", dto.getCustomer().firstName());
        assertEquals("Schmidt", dto.getCustomer().lastName());
        assertEquals("anna.schmidt@example.com", dto.getCustomer().email());
        assertEquals("Beispielstraße 12, 54321 Beispielstadt", dto.getCustomer().address());
    }

    @Test
    void toEntity_fromCreateDto() {
        // GIVEN
        CustomerRequestCreateDto dto = new CustomerRequestCreateDto(
                ProcessItemCreateDto.builder()
                        .title("New Feature")
                        .description("description")
                        .build(),
                Priority.MEDIUM,
                Category.BUG_REPORT,
                1L,
                List.of(new EmailAddress("test.mail"))
        );

        // WHEN
        CustomerRequest entity = customerRequestMapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals("New Feature", entity.getTitle());
        assertEquals("description", entity.getDescription());
        assertEquals(Priority.MEDIUM, entity.getPriority());
        assertEquals(Category.BUG_REPORT, entity.getCategory());
        assertEquals(BigDecimal.ZERO, entity.getEstimatedScope());
        assertEquals(CustomerRequestStatus.RECEIVED, entity.getStatus());
        assertEquals(Chargeable.NOT_DETERMINED, entity.getChargeable());

        // ignored fields
        assertNull(entity.getCustomer());
    }
}
