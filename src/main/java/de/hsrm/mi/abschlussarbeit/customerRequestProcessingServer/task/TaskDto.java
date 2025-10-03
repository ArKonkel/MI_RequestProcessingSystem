package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.expertise.ExpertiseDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record TaskDto(
        ProcessItemDto processItem,
        BigDecimal estimatedTime,
        TimeUnit estimationUnit,
        Long workingTimeInMinutes,
        LocalDate dueDate,
        Priority priority,
        String acceptanceCriteria,
        TaskStatus status,
        Long calendarEntryId, //TODO das brauche ich hier nicht
        Set<ExpertiseDto> expertise,
        Long requestId,
        String requestTitle,
        Long projectId,
        String projectTitle
) {}