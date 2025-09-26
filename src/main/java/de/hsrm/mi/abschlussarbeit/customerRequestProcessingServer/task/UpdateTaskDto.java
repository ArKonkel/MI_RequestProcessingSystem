package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.UpdateProcessItemDto;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class UpdateTaskDto {
    private UpdateProcessItemDto processItem;
    private BigDecimal estimatedTime;
    private TimeUnit estimationUnit;
    private Long workingTimeInMinutes;
    private LocalDate dueDate;
    private TaskStatus status;
    private Priority priority;
    private String acceptanceCriteria;
}
