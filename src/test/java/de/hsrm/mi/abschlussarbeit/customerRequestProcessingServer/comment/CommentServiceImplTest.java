package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationEventPublisher;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    NotificationEventPublisher publisher;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserService userService;

    @Mock
    ProcessItemService processItemService;

    @InjectMocks
    CommentServiceImpl commentService;


    @Test
    void addCommentToProcessItem_shouldResolveMentionsAndPublishEvent() {
        // GIVEN
        User author = new User();
        author.setId(99L);

        User mentioned1 = new User();
        mentioned1.setId(1L);
        User mentioned2 = new User();
        mentioned2.setId(42L);

        ProcessItem processItem = new Task();
        processItem.setId(123L);
        processItem.setTitle("Test Process");

        String commentText = "Hello @1 and @42!";

        CommentCreateDto dto = new CommentCreateDto(commentText, author.getId());

        when(userService.getUserById(author.getId())).thenReturn(author);
        when(userService.getUserById(1L)).thenReturn(mentioned1);
        when(userService.getUserById(42L)).thenReturn(mentioned2);
        when(processItemService.getProcessItemById(processItem.getId())).thenReturn(processItem);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        commentService.addCommentToProcessItem(processItem.getId(), dto);

        // THEN
        verify(publisher).publishNotificationEvent(argThat(event ->
                event.processItemId().equals(processItem.getId()) &&
                        event.userIdsToNotify().contains(1L) &&
                        event.userIdsToNotify().contains(42L) &&
                        event.text().equals(commentText) &&
                        event.processItemTitle().equals("Test Process")
        ));
    }
}
