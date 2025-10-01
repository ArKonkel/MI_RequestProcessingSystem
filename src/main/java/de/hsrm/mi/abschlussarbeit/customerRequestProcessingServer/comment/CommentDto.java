package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserDto;

import java.time.Instant;

public record CommentDto(Long id, String text, Instant timeStamp, UserDto author) {
}
