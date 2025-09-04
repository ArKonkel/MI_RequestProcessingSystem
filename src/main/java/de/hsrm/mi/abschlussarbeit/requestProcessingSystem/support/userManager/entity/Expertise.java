package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.ExpertiseLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expertise extends Competence{

    @Enumerated(EnumType.STRING)
    private ExpertiseLevel expertiseLevel;
}
