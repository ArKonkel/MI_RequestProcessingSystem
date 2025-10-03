package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemMapper;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper class for {@link Task} and {@link TaskDto}.
 */
@Mapper(
        componentModel = "spring", uses = {ProcessItemMapper.class}
)
public interface TaskMapper {

    @Mapping(target = "processItem", source = ".")
    @Mapping(target = "calendarEntryId", source = "calendarEntry.id")
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "requestTitle", source = "request.title")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "projectId", source = "project.id")
    TaskDto toDto(Task task);

    @Mapping(target = "title", source = "processItem.title")
    @Mapping(target = "description", source = "processItem.description")
    @Mapping(target = "assignee", source = "processItem.assigneeId", qualifiedByName = "mapAssigneeIdToUser")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "calendarEntry", ignore = true)
    @Mapping(target = "expertise", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Task toEntity(TaskDto taskDto);

    @Named("mapAssigneeIdToUser")
    default User mapAssigneeIdToUser(Long assigneeId) {
        if (assigneeId == null) return null;
        User user = new User();
        user.setId(assigneeId);
        return user;
    }
}