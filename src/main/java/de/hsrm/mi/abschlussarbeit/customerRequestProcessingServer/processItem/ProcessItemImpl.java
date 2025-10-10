package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.Comment;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentCreateDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentRepository;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.customerRequest.CustomerRequest;
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

    @Override
    @Transactional
    public void assignProcessItemToUser(Long processItemId, Long userId) {
        log.info("Assigning process item {} to user {}", processItemId, userId);

        ProcessItem processItem = getProcessItemById(processItemId);
        User user = userService.getUserById(userId);

        processItem.setAssignee(user);

        processItemRepository.save(processItem);

        TargetType targetType = null;
        switch (processItem) {
            case Task task -> targetType = TargetType.TASK;
            case CustomerRequest customerRequest -> targetType = TargetType.CUSTOMER_REQUEST;
            case Project project -> targetType = TargetType.PROJECT;
            default -> {
            }
        }

        notificationService.sendChangeNotification(new ChangeNotificationEvent(processItem.getId(), ChangeType.UPDATED, targetType));

        notificationService.sendUserNotification(new UserNotificationEvent(UserNotificationType.ASSIGNED, processItem.getId(), processItem.getTitle(),
                List.of(user.getId()), "", Instant.now()));
    }

    @Override
    public ProcessItem getProcessItemById(Long id) {

        return processItemRepository.findById(id).orElseThrow(() -> new NotFoundException("Process item with id " + id + " not found"));
    }

    @Override
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
                    usersIds, savedComment.getText(), savedComment.getTimeStamp()));
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
