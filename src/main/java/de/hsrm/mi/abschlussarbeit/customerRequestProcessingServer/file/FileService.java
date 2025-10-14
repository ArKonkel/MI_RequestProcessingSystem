package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    File getFileById(String id);

    File upload(MultipartFile multipartFile) throws IOException;

    List<FileDto> getAllFiles();
}
