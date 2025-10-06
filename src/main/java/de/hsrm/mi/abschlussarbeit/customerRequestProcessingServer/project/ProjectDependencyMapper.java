package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper class for {@link ProjectDependency} and {@link ProjectDependencyDto}.
 */
@Mapper(componentModel = "spring")
public interface ProjectDependencyMapper {

    @Mapping(target = "sourceProjectId", source = "sourceProject.id")
    @Mapping(target = "targetProjectId", source = "targetProject.id")
    @Mapping(target = "targetProjectTitle", source = "targetProject.title")
    ProjectDependencyDto toDto(ProjectDependency projectDependency);
}
