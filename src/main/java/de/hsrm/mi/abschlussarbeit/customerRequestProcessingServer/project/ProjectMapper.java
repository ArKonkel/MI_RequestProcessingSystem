package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItemMapper;
import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.task.TaskMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * Mapper class for {@link Project} and {@link ProjectDto}
 */
@Mapper(componentModel = "spring", uses = {ProjectDependencyMapper.class, ProcessItemMapper.class, TaskMapper.class})
public interface ProjectMapper {

    @Mapping(target = "processItem", source = ".")
    @Mapping(target = "projectDependencies", source = "incomingDependencies")
    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "requestTitle", source = "request.title")
    ProjectDto toDto(Project project);

    List<ProjectDependencyDto> mapIncomingDependencies(Set<ProjectDependency> incomingDependencies);
}
