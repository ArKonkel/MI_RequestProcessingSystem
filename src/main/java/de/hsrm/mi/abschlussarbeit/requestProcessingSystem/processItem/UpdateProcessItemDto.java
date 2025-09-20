package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateProcessItemDto {
    private String title;
    private String description;
    private Long assigneeId;
}