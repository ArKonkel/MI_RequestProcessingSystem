package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.CompetenceDto;

import java.time.LocalDate;
import java.util.Set;

public record TaskDto(
        ProcessItemDto processItem,
        Long estimatedTime,
        Long workingTime,
        LocalDate dueDate,
        Priority priority,
        String acceptanceCriteria,
        Long calendarEntryId,
        Set<CompetenceDto> competences,
        Long blockerId,
        Long blockedId,
        Long referenceTaskId,
        //Set<Long> referencedByIds,
        Long requestId,
        Long projectId
) {}