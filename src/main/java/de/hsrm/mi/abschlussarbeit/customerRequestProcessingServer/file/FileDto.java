package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file;

import lombok.Data;

@Data
public class FileDto {
    private String name;
    private String url;
    private String contentType;
    private Long size;
}
