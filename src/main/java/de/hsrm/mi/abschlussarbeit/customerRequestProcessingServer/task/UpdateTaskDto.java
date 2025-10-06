package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.TimeUnit;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class UpdateTaskDto {
    private String title;
    private String description;
    private Long assigneeId;
    private BigDecimal estimatedTime;
    private TimeUnit estimationUnit;
    private Long workingTimeInMinutes;
    private LocalDate dueDate;
    private TaskStatus status;
    private Priority priority;
    private String acceptanceCriteria;
    private List<Long> expertiseIds;
}
