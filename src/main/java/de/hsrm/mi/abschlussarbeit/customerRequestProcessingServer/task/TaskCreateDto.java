package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.shared.Priority;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreateDto {
    String title;
    String description;
    LocalDate dueDate;
    Priority priority;
    Long requestId;
    //Long projectId;
}
