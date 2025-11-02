package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.calendar.CalendarEntry;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

/**
 * Mapper class for {@link Task} and {@link TaskDto}.
 */
@Mapper(
        componentModel = "spring", uses = {ProcessItemMapper.class}
)
public interface TaskMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "status", source = "status")
    TaskReferenceDto toReferenceDto(Task task); //Needed to map the blocks and blockedBy fields

    @Mapping(target = "processItem", source = ".")
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "requestTitle", source = "request.title")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectTitle", source = "project.title")
    @Mapping(target = "blockedBy", source = "blockedBy")
    @Mapping(target = "blocks", source = "blocks")
    @Mapping(target = "calendarEntryDates", source = "calendarEntry")
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
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "blocks", ignore = true)
    @Mapping(target = "blockedBy", ignore = true)
    Task toEntity(TaskDto taskDto);


    /**
     * Maps a list of CalendarEntries to a list of LocalDate objects.
     */
    default List<LocalDate> mapCalendarEntryDates(List<CalendarEntry> entries) {
        if (entries == null) {
            return List.of();
        }
        return entries.stream()
                .map(CalendarEntry::getDate)
                .toList();
    }
}