package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationEvent;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationEventPublisher;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.notification.NotificationType;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemService;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final NotificationEventPublisher publisher;

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final ProcessItemService processItemService;

    @Override
    public void addCommentToProcessItem(Long processItemId, CommentCreateDto comment) {
        log.info("Adding comment {} to process item {}", comment.text(), processItemId);

        ProcessItem processItem = processItemService.getProcessItemById(processItemId);
        User user = userService.getUserById(comment.authorId());

        Comment commentToCreate = new Comment();
        commentToCreate.setProcessItem(processItem);
        commentToCreate.setAuthor(user);
        commentToCreate.setText(comment.text());
        commentToCreate.setTimeStamp(Instant.now());

        Comment savedComment = commentRepository.save(commentToCreate);

        List<User> users = findMentionedUsers(comment.text());
        List<Long> usersIds = users.stream().map(User::getId).toList();

        if (!users.isEmpty())
            publisher.publishNotificationEvent(new NotificationEvent(NotificationType.COMMENT_MENTIONING, processItem.getId(), processItem.getTitle(),
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

}
