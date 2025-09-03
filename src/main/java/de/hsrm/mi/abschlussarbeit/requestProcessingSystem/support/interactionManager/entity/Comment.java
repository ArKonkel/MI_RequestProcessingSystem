package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.interactionManager.entity;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String text;

    private LocalDateTime writtenOn;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
}

