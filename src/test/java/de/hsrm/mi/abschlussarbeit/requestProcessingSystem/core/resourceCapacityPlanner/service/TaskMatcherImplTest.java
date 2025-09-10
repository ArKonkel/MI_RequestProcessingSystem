package de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.resourceCapacityPlanner.service;

import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.dto.ProcessItemDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.enums.Priority;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.core.taskManager.dto.TaskDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.CompetenceType;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.enums.ExpertiseLevel;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.CompetenceDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.dto.EmployeeExpertiseDto;
import de.hsrm.mi.abschlussarbeit.requestProcessingSystem.support.userManager.service.UserManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskMatcherImplTest {

    @Mock
    private UserManager userManager;

    @InjectMocks
    private TaskMatcherImpl taskMatcher;

    @Test
    void findBestMatchingEmployees() {
        // GIVEN
        CompetenceDto expertise1 = new CompetenceDto(1L, "Customizing", "", CompetenceType.EXPERTISE);
        CompetenceDto expertise2 = new CompetenceDto(2L, "Coding", "", CompetenceType.EXPERTISE);
        CompetenceDto expertise3 = new CompetenceDto(3L, "Printing", "", CompetenceType.EXPERTISE);

        TaskDto taskDto = createTaskDto(1L, "Customizing der Software", 120L, LocalDate.parse("2025-11-05"),
                Priority.HIGH, Set.of(expertise1, expertise3));

        EmployeeExpertiseDto employee1Expertise1 = new EmployeeExpertiseDto(1L, 1L, expertise1, ExpertiseLevel.ADVANCED);
        EmployeeExpertiseDto employee1Expertise3 = new EmployeeExpertiseDto(2L, 1L, expertise3, ExpertiseLevel.EXPERT);
        EmployeeExpertiseDto employee2Expertise1 = new EmployeeExpertiseDto(3L, 2L, expertise1, ExpertiseLevel.BEGINNER);
        EmployeeExpertiseDto employee2Expertise2 = new EmployeeExpertiseDto(4L, 2L, expertise2, ExpertiseLevel.EXPERT);

        EmployeeDto employee1 = createEmployee(1L, "Max", Set.of(
                employee2Expertise1, employee2Expertise2
        ));

        EmployeeDto employee2 = createEmployee(2L, "Sabine", Set.of(
                employee1Expertise1, employee1Expertise3
        ));

        EmployeeDto employee3 = createEmployee(3L, "Freddy", Set.of());


        Map<EmployeeDto, Integer> expected = Map.of(
                employee1, 7, //Employee1 has a sum of 7 points because he has expertise1 and expertise3
                employee2, 1
        );

        // WHEN
        when(userManager.getAllEmployeeExpertises()).thenReturn(List.of(employee1Expertise1, employee1Expertise3, employee2Expertise1, employee2Expertise2));
        when(userManager.getEmployeesByIds(List.of(employee1.id(), employee2.id()))).thenReturn(List.of(employee1, employee2));

        Map<EmployeeDto, Integer> result = taskMatcher.findBestMatchingEmployees(taskDto);

        // THEN
        assertEquals(expected, result);
    }


    private EmployeeDto createEmployee(long id, String vorname, Set<EmployeeExpertiseDto> expertise) {
        return new EmployeeDto(id, vorname, "Nachname", "email@test.de",
                null, null, null, expertise, Set.of(), null, null, null);
    }

    private TaskDto createTaskDto(long id, String title, Long estimatedTime, LocalDate dueDate, Priority priority, Set<CompetenceDto> competenceIds) {
        ProcessItemDto processItem = new ProcessItemDto(
                id,
                title,
                "",
                LocalDate.parse("2025-09-05"),
                null,
                null
        );

        return new TaskDto(
                processItem,
                estimatedTime,
                dueDate,
                priority,
                null,
                competenceIds,
                null,
                null,
                null,
                null,
                null
        );

    }

}