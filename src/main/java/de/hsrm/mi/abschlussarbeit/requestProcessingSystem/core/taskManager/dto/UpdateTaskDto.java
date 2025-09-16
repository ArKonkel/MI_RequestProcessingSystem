package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UpdateTaskDto {
    private UpdateProcessItemDto processItem;
    private Long estimatedTime;
    private Long workingTime;
    private LocalDate dueDate;
    private TaskStatus status;
    private Priority priority;
    private String acceptanceCriteria;
    private Long blockerId;
    private Long blockedId;
    private Long referenceTaskId;
    private Long requestId;
    private Long projectId;
}
