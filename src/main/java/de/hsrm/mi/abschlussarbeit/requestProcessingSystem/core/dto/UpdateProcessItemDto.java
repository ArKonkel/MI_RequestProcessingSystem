package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.statusManager.dto.StatusDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateProcessItemDto {
    private String title;
    private String description;
    private Long assigneeId;
    private StatusDto status;
}