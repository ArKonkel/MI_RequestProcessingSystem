package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.CommentMapper;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file.FileMapper;
import org.mapstruct.Mapper;

/**
 * Mapper for {@link ProcessItem} and {@link ProcessItemDto}.
 */
@Mapper(componentModel = "spring", uses = {CommentMapper.class, FileMapper.class})
public interface ProcessItemMapper {

    ProcessItemDto toDto(ProcessItem processItem);
}
