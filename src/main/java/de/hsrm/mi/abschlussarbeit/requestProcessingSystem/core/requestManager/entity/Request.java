package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.requestManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.entity.ProcessItem;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request extends ProcessItem {

    private Priority priority;

    private Long estimatedScope;

    private boolean chargeable;
}
