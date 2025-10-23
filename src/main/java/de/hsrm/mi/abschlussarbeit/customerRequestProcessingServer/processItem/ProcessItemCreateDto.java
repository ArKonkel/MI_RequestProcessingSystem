package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessItemCreateDto {
    private String title;
    private String description;
}