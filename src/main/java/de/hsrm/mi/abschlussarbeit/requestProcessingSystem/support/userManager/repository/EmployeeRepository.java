package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.repository;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByIdIn(List<Long> ids);
}
