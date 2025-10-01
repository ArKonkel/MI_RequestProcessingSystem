package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.UserNotificationType;
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
    NotificationService notificationService;

    @Mock
    ProcessItemRepository processItemRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ProcessItemImpl processItemService;

    @Test
    void assignProcessItemToUser_shouldAssignAndPublishEvent() {
        // GIVEN
        ProcessItem item = new Task();
        item.setId(100L);
        item.setTitle("Test Task");

        User user = new User();
        user.setId(42L);

        when(processItemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(userService.getUserById(42L)).thenReturn(user);

        // WHEN
        processItemService.assignProcessItemToUser(100L, 42L);

        // THEN
        assertThat(item.getAssignee()).isEqualTo(user);

        //Check if event was published
        verify(notificationService).sendUserNotification((argThat(event ->
                event.type() == UserNotificationType.ASSIGNED &&
                        event.processItemTitle().equals("Test Task") &&
                        event.userIdsToNotify().contains(42L)
        )));

        //Check if saved
        verify(processItemRepository).save(item);
    }
}
