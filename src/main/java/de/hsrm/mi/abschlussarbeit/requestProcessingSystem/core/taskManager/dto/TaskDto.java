package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.CompetenceDto;

import java.util.Date;
import java.util.Set;

public record TaskDto(
        ProcessItemDto processItem,
        long estimatedTime,
        Date dueDate,
        Priority priority,
        Long calendarEntryId,
        Set<CompetenceDto> competence,
        Long blockerId,
        Long blockedId,
        Long referenceTaskId,
        Set<Long> referencedByIds,
        Long requestId,
        Long projectId
) {}