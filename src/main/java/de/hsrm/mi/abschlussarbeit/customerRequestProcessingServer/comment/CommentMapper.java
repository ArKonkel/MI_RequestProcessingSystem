package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    CommentDto toDto(Comment comment);
}
