package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem;

import java.time.Instant;

public record ProcessItemDto (
        Long id,
        String title,
        String description,
        Instant creationDate,
        Long assigneeId
        //Set<Long> commentIds
) {}
