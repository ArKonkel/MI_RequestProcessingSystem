package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class FileController {

    private final FileServiceImpl fileServiceImpl;

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String id) {
        log.info("REST request to download file with id {}", id);

        File file = fileServiceImpl.getFileById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"") //change attachment to inline if you want to view it inside the browser
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(new ByteArrayResource(file.getData()));
    }

    @GetMapping
    public ResponseEntity<List<FileDto>> listAllFiles() {
        log.info("REST request to get all files");
        List<FileDto> files = fileServiceImpl.getAllFiles();

        return ResponseEntity.ok(files);
    }
}
