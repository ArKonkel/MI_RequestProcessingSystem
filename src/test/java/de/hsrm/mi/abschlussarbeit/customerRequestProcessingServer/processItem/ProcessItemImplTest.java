package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationEventPublisher;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationType;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessItemImplTest {

    @Mock
    NotificationEventPublisher publisher;

    @Mock
    ProcessItemRepository processItemRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ProcessItemImpl processItemService;

    @Test
    void assignProcessItemToUserOfEmployee_shouldAssignAndPublishEvent() {
        // GIVEN
        ProcessItem item = new Task();
        item.setId(100L);
        item.setTitle("Test Task");

        User employee = new User();
        employee.setId(42L);

        when(processItemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(userService.getUserOfEmployee(42L)).thenReturn(employee);

        // WHEN
        processItemService.assignProcessItemToUserOfEmployee(100L, 42L);

        // THEN
        assertThat(item.getAssignee()).isEqualTo(employee);

        //Check if event was published
        verify(publisher).publishNotificationEvent(argThat(event ->
                event.type() == NotificationType.ASSIGNED &&
                        event.processItemTitle().equals("Test Task") &&
                        event.userIdsToNotify().contains(42L)
        ));

        //Check if saved
        verify(processItemRepository).save(item);
    }
}
