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
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "requestTitle", source = "request.title")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    TaskDto toDto(Task task);

    @Mapping(target = "title", source = "processItem.title")
    @Mapping(target = "description", source = "processItem.description")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "calendarEntry", ignore = true)
    @Mapping(target = "expertise", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task toEntity(TaskDto taskDto);
}