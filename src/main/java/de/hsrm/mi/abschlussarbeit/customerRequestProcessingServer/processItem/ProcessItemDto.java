package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class ProcessItemDto {
    Long id;
    String title;
    String description;
    Instant creationDate;
    UserDto assignee;
    List<CommentDto> comments;
}
