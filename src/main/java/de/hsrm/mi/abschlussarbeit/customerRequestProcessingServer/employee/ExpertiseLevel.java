package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import lombok.Getter;

@Getter
public enum ExpertiseLevel {
    BEGINNER(1),
    INTERMEDIATE(2),
    ADVANCED(3),
    EXPERT(5);

    private final int points;

    ExpertiseLevel(int points) {
        this.points = points;
    }

}
