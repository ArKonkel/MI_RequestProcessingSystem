package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.task;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.competence.CompetenceDto;

import java.time.LocalDate;
import java.util.Set;

public record TaskDto(
        ProcessItemDto processItem,
        Long estimatedTime,
        Long workingTime,
        LocalDate dueDate,
        Priority priority,
        String acceptanceCriteria,
        TaskStatus status,
        Long calendarEntryId, //TODO das brauche ich hier nicht
        Set<CompetenceDto> competences,
        Long blockerId,
        Long blockedId,
        Long referenceTaskId,
        //Set<Long> referencedByIds,
        Long requestId,
        Long projectId
) {}