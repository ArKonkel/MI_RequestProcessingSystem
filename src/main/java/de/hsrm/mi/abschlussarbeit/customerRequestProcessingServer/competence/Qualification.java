package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.competence;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Qualification extends Competence{

    private LocalDate obtainedDate;
}
