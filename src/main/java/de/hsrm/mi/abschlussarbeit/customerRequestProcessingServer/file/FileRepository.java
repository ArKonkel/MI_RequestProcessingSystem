package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, String> {
}
