package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.employee;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("REST request to get all dto employees");

        List<EmployeeDto> employees = employeeService.getAllDtoEmployees();

        return ResponseEntity.ok(employees);
    }

    @PostMapping("/expertises")
    public ResponseEntity<Void> addExpertiseToEmployee(@RequestParam Long employeeId, @RequestParam Long expertiseId, @RequestParam ExpertiseLevel level) {
        log.info("REST request to add expertise to employee");

        employeeService.addEmployeeExpertise(employeeId, expertiseId, level);

        return ResponseEntity.ok().build();
    }
}
