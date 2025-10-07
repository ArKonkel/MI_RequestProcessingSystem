package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskDto;

import java.time.LocalDate;
import java.util.List;

public record ProjectDto(
        ProcessItemDto processItem,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate,
        Long requestId,
        String requestTitle,
        List<ProjectDependencyDto> incomingDependencies,
        List<ProjectDependencyDto> outgoingDependencies,
        List<TaskDto> tasks
) {}
