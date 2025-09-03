package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Qualification extends Competence{

    private Date obtainedDate;
}
