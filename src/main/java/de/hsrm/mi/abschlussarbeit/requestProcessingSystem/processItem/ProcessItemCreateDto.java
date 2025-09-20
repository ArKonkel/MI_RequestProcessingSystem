package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.processItem;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProcessItemCreateDto {
    private String title;
    private String description;
}