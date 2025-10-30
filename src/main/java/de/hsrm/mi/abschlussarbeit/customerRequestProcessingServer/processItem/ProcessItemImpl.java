package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.Comment;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentRepository;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.File;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.FileService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.*;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project.Project;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.Task;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProcessItemImpl implements ProcessItemService, CommentService {

    private final NotificationService notificationService;

    private final ProcessItemRepository processItemRepository;

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final FileService fileService;

    /**
     * Assigns a process item to a user or unassigns it if the userId is -1.
     * Sends notifications about the assignment or unassignment to the relevant parties.
     *
     * @param processItemId the ID of the process item to be assigned or unassigned
     * @param userId        the ID of the user to whom the process item will be assigned;
     *                      pass -1 to unassign the process item
     */
    @Override
    @Transactional
    public void assignProcessItemToUser(Long processItemId, Long userId) {
        log.info("Assigning process item {} to user {}", processItemId, userId);
        User user = null;

        ProcessItem processItem = getProcessItemById(processItemId);

        if (userId == -1) {
            processItem.setAssignee(null);
        } else {
            user = userService.getUserById(userId);

            processItem.setAssignee(user);
        }

        processItemRepository.save(processItem);

        TargetType targetType = determineTargetType(processItem);

        notificationService.sendChangeNotification(new ChangeNotificationEvent(processItem.getId(), ChangeType.UPDATED, targetType));

        if (user != null) {
            notificationService.sendUserNotification(new UserNotificationEvent(UserNotificationType.ASSIGNED, processItem.getId(), processItem.getTitle(),
                    List.of(user.getId()), "", Instant.now(), targetType));
        }
    }

    @Override
    @Transactional
    public ProcessItem getProcessItemById(Long id) {

        return processItemRepository.findById(id).orElseThrow(() -> new NotFoundException("Process item with id " + id + " not found"));
    }

    /**
     * Adds an attachment to the specified process item and sends a change notification.
     *
     * @param processItemId the ID of the process item to which the attachment will be added
     * @param multipartFile the file to be uploaded and attached to the process item
     * @throws IOException if an I/O error occurs during file upload
     */
    @Override
    @Transactional
    public void addAttachment(Long processItemId, MultipartFile multipartFile) throws IOException {
        log.info("Adding attachment to process item {}", processItemId);

        ProcessItem processItem = processItemRepository.findById(processItemId)
                .orElseThrow(() -> new NotFoundException("ProcessItem not found"));

        File file = fileService.upload(multipartFile);

        file.setProcessItem(processItem);
        processItem.getAttachments().add(file);

        processItemRepository.save(processItem);

        TargetType targetType = determineTargetType(processItem);
        notificationService.sendChangeNotification(new ChangeNotificationEvent(processItem.getId(), ChangeType.UPDATED, targetType));
    }

    /**
     * Adds a comment to a specific process item and handles related notifications.
     *
     * @param processItemId the ID of the process item to which the comment will be added
     * @param comment the comment details, including the author's ID and the text of the comment
     */
    @Override
    @Transactional
    public void addCommentToProcessItem(Long processItemId, CommentCreateDto comment) {
        log.info("Adding comment {} to process item {}", comment.text(), processItemId);

        ProcessItem processItem = getProcessItemById(processItemId);
        User user = userService.getUserById(comment.authorId());

        Comment commentToCreate = new Comment();
        commentToCreate.setProcessItem(processItem);
        commentToCreate.setAuthor(user);
        commentToCreate.setText(comment.text());
        commentToCreate.setTimeStamp(Instant.now());

        Comment savedComment = commentRepository.save(commentToCreate);

        List<User> users = findMentionedUsers(comment.text());
        List<Long> usersIds = users.stream().map(User::getId).toList();

        TargetType targetType = determineTargetType(processItem);
        notificationService.sendChangeNotification(new ChangeNotificationEvent(processItem.getId(), ChangeType.UPDATED, targetType));

        if (!users.isEmpty())
            notificationService.sendUserNotification(new UserNotificationEvent(UserNotificationType.COMMENT_MENTIONING, processItem.getId(), processItem.getTitle(),
                    usersIds, savedComment.getText(), savedComment.getTimeStamp(), targetType));
    }

    /**
     * Extracts the mentioned users from a given text. A mentioning is specified @id
     *
     * @param text to extract the mentioned users from. The text can contain multiple users, separated by spaces.
     * @return a list of mentioned users. If no users are mentioned, an empty list is returned.
     */
    private List<User> findMentionedUsers(String text) {
        List<Long> mentionedIds = Arrays.stream(text.split("\\s+"))
                .filter(word -> word.startsWith("@"))
                .map(word -> word.substring(1))   // remove @
                .map(word -> word.replaceAll("\\D.*$", ""))   // remove everything after the id
                .map(Long::parseLong)
                .toList();

        return mentionedIds.stream()
                .map(userService::getUserById)
                .toList();
    }

    private TargetType determineTargetType(ProcessItem processItem) {
        TargetType targetType = null;
        if (processItem instanceof CustomerRequest) {
            targetType = TargetType.CUSTOMER_REQUEST;
        } else if (processItem instanceof Task) {
            targetType = TargetType.TASK;
        } else if (processItem instanceof Project) {
            targetType = TargetType.PROJECT;
        }

        return targetType;
    }

}
