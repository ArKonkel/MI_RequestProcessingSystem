package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.Comment;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentRepository;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.File;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.FileService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.ChangeType;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessItemImplTest {

    @Mock
    NotificationService notificationService;

    @Mock
    ProcessItemRepository processItemRepository;

    @Mock
    UserService userService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    FileService fileService;

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

    @Test
    void assignProcessItemToUser_shouldUnassign() {
        // GIVEN
        ProcessItem item = new Task();
        item.setId(100L);
        item.setTitle("Test Task");

        // Set an existing assignee first
        User previousAssignee = new User();
        previousAssignee.setId(42L);
        item.setAssignee(previousAssignee);

        // WHEN
        when(processItemRepository.findById(100L)).thenReturn(Optional.of(item));

        processItemService.assignProcessItemToUser(100L, -1L);

        // THEN
        // Assignee should be null
        assertThat(item.getAssignee()).isNull();
        verify(processItemRepository).save(item);

        // No user notification should be sent
        verify(notificationService, never()).sendUserNotification(any());
    }

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
        when(processItemRepository.findById(processItem.getId())).thenReturn(Optional.of(processItem));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        processItemService.addCommentToProcessItem(processItem.getId(), dto);

        // THEN
        verify(notificationService).sendUserNotification((argThat(event ->
                event.processItemId().equals(processItem.getId()) &&
                        event.userIdsToNotify().contains(1L) &&
                        event.userIdsToNotify().contains(42L) &&
                        event.text().equals(commentText) &&
                        event.processItemTitle().equals("Test Process")
        )));
    }


    @Test
    void addAttachment_shouldUploadFileAddToProcessItemAndSendNotification_withoutSpy() throws IOException {
        // GIVEN
        Long processItemId = 123L;

        ProcessItem processItem = new Task();
        processItem.setId(processItemId);

        MultipartFile multipartFile = mock(MultipartFile.class);

        File uploadedFile = new File();
        uploadedFile.setId("Foo");

        when(processItemRepository.findById(processItemId)).thenReturn(Optional.of(processItem));
        when(fileService.upload(multipartFile)).thenReturn(uploadedFile);

        // WHEN
        processItemService.addAttachment(processItemId, multipartFile);

        // THEN
        verify(fileService).upload(multipartFile);
        assertThat(processItem.getAttachments()).contains(uploadedFile);
        assertThat(uploadedFile.getProcessItem()).isEqualTo(processItem);

        verify(processItemRepository).save(processItem);

        verify(notificationService).sendChangeNotification(
                argThat(event ->
                        event.changeType() == ChangeType.UPDATED &&
                                event.processItemId().equals(processItemId) &&
                                event.targetType() != null
                ));
    }


}
