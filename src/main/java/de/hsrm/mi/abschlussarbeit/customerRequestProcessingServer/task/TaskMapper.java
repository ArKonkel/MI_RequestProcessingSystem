package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper class for {@link Task} and {@link TaskDto}.
 */
@Mapper(
        componentModel = "spring", uses = {ProcessItemMapper.class}
)
public interface TaskMapper {

    @Mapping(target = "processItem", source = ".")
    @Mapping(target = "calendarEntryId", source = "calendarEntry.id")
    @Mapping(target = "blockerId", source = "blocker.id")
    @Mapping(target = "blockedId", source = "blocked.id")
    @Mapping(target = "referenceTaskId", source = "referenceTask.id")
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "projectId", source = "project.id")
    TaskDto toDto(Task task);
}