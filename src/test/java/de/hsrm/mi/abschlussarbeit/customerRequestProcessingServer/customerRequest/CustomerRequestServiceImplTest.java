package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.Customer;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customer.CustomerService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotAllowedException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.EmailAddress;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.mail.MailService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.ChangeNotificationEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.UserNotificationEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerRequestServiceImplTest {

    @Mock
    CustomerRequestRepository customerRequestRepository;

    @Mock
    CustomerService customerService;

    @Mock
    CustomerRequestMapper requestMapper;

    @Mock
    MailService mailService;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    CustomerRequestServiceImpl service;

    CustomerRequestCreateDto createDto;
    ProcessItemCreateDto processItemCreateDto;
    CustomerRequest requestEntity;
    CustomerRequestDto requestDto;
    Customer customer;

    @BeforeEach
    void setup() {
        // Customer
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@test.com");

        // ProcessItemCreateDto
        processItemCreateDto = new ProcessItemCreateDto();
        processItemCreateDto.setTitle("Test Title");
        processItemCreateDto.setDescription("Test Description");

        // CustomerRequestCreateDto
        createDto = CustomerRequestCreateDto.builder()
                .processItem(processItemCreateDto)
                .priority(Priority.MEDIUM)
                .category(Category.BUG_REPORT)
                .customerId(customer.getId())
                .toRecipients(List.of(new EmailAddress("recipient@test.com")))
                .build();

        // CustomerRequest entity
        requestEntity = new CustomerRequest();
        requestEntity.setId(100L);
        requestEntity.setTitle("Test Title");
        requestEntity.setDescription("Test Description");
        requestEntity.setCustomer(customer);
        requestEntity.setStatus(CustomerRequestStatus.WAITING_FOR_PROCESSING);

        // CustomerRequest DTO
        requestDto = new CustomerRequestDto();

        // ProcessItem DTO
        ProcessItemDto processItemDto = new ProcessItemDto();
        processItemDto.setId(10L);
        processItemDto.setTitle("Test Title");
        processItemDto.setDescription("Test Description");

        requestDto.setProcessItem(processItemDto);
    }

    @Test
    void createRequest_shouldSaveSendMailAndNotifications() {
        when(requestMapper.toEntity(createDto)).thenReturn(requestEntity);
        when(customerService.getCustomerById(customer.getId())).thenReturn(customer);
        when(customerRequestRepository.save(requestEntity)).thenReturn(requestEntity);
        when(requestMapper.toDto(requestEntity)).thenReturn(requestDto);

        CustomerRequestDto result = service.createRequest(createDto);

        assertThat(result).isEqualTo(requestDto);

        verify(customerRequestRepository).save(requestEntity);
        verify(mailService).sendMails(any(), eq(customer.getEmail()));


        verify(notificationService).sendChangeNotification(any(ChangeNotificationEvent.class));
        verify(notificationService).sendUserNotification(any(UserNotificationEvent.class));
    }

    @Test
    void updateCustomerRequest_shouldUpdateFieldsAndSendNotification() {
        UpdateCustomerRequestDto updateDto = new UpdateCustomerRequestDto();
        updateDto.setTitle("Updated Title");
        updateDto.setPriority(Priority.HIGH);

        when(customerRequestRepository.findById(100L)).thenReturn(Optional.of(requestEntity));
        when(customerRequestRepository.save(requestEntity)).thenReturn(requestEntity);
        when(requestMapper.toDto(requestEntity)).thenReturn(requestDto);

        CustomerRequestDto result = service.updateCustomerRequest(100L, updateDto);

        assertThat(result).isEqualTo(requestDto);
        assertThat(requestEntity.getTitle()).isEqualTo("Updated Title");
        assertThat(requestEntity.getPriority()).isEqualTo(Priority.HIGH);

        verify(notificationService).sendChangeNotification(any(ChangeNotificationEvent.class));
    }

    @Test
    void updateCustomerRequest_shouldThrowNotAllowedException_ifProjectAlreadyAttached() {
        requestEntity.setClassifiedAsProject(IsProjectClassification.YES);
        requestEntity.setProjects(Set.of(new Project()));

        UpdateCustomerRequestDto updateDto = new UpdateCustomerRequestDto();
        updateDto.setClassifiedAsProject(IsProjectClassification.NO);

        when(customerRequestRepository.findById(100L)).thenReturn(Optional.of(requestEntity));

        assertThrows(NotAllowedException.class, () -> service.updateCustomerRequest(100L, updateDto));
    }

    @Test
    void isRequestReadyForProcessing_shouldReturnTrueOrFalseBasedOnStatus() {
        when(customerRequestRepository.findById(100L)).thenReturn(Optional.of(requestEntity));

        // WAITING_FOR_PROCESSING
        requestEntity.setStatus(CustomerRequestStatus.WAITING_FOR_PROCESSING);
        assertThat(service.isRequestReadyForProcessing(100L)).isTrue();

        // IN_PROCESS
        requestEntity.setStatus(CustomerRequestStatus.IN_PROCESS);
        assertThat(service.isRequestReadyForProcessing(100L)).isTrue();

        // DONE
        requestEntity.setStatus(CustomerRequestStatus.DONE);
        assertThat(service.isRequestReadyForProcessing(100L)).isFalse();
    }

    @Test
    void isRequestReadyForProcessing_shouldThrowException_ifNotFound() {
        when(customerRequestRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.isRequestReadyForProcessing(100L));
    }
}
