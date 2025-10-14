package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "url", expression = "java(buildUrl(file.getId()))")
    @Mapping(target = "size", expression = "java(file.getData() != null ? (long) file.getData().length : 0L)")
    FileDto toDto(File file);

    @Named("buildUrl")
    default String buildUrl(String id) {
        return "/api/files/" + id;
    }
}
