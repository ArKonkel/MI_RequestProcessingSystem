package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.role.Role;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.comment.Comment;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee.Employee;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users") // --> needed because 'user' is reserved by the database
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<Comment> comments;

    @OneToMany(mappedBy = "assignee")
    private Set<ProcessItem> processItems;
}
