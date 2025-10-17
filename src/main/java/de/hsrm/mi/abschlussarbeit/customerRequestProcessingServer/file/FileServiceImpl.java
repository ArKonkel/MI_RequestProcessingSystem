package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.globalExceptionHandler.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public File getFileById(String id) {
        log.info("Getting file with id {}", id);

        return fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File with id " + id + " not found"));
    }

    @Override
    @Transactional
    public File upload(MultipartFile multipartFile) throws IOException {
        log.info("Uploading file {}", multipartFile.getOriginalFilename());

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        File file = new File();
        file.setName(fileName);
        file.setContentType(multipartFile.getContentType());
        file.setData(multipartFile.getBytes());

        return fileRepository.save(file);
    }
}
