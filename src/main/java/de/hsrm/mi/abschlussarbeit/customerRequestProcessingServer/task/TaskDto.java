package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.Expertise;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.EstimationUnit;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;

import java.time.LocalDate;
import java.util.Set;

public record TaskDto(
        ProcessItemDto processItem,
        Long estimatedTime,
        EstimationUnit estimationUnit,
        Long workingTimeInMinutes,
        LocalDate dueDate,
        Priority priority,
        String acceptanceCriteria,
        TaskStatus status,
        Long calendarEntryId, //TODO das brauche ich hier nicht
        Set<Expertise> expertise,
        Long blockerId,
        Long blockedId,
        Long referenceTaskId,
        //Set<Long> referencedByIds,
        Long requestId,
        Long projectId
) {}