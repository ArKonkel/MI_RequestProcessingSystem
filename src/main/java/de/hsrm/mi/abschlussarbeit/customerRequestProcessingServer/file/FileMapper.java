package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "size", expression = "java(file.getData() != null ? (long) file.getData().length : 0L)")
    FileDto toDto(File file);

}
