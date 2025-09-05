package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums;

import lombok.Getter;

@Getter
public enum ExpertiseLevel {
    BEGINNER(1),
    INTERMEDIATE(2),
    ADVANCED(3),
    EXPERT(4);

    private final int points;

    ExpertiseLevel(int points) {
        this.points = points;
    }

}
