package de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.file;

import de.hsrm.mi.abschlussarbeit.customerRequestProcessingServer.processItem.ProcessItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String contentType;

    @Lob
    @Basic(fetch = FetchType.LAZY) //Only fetch when needed
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "process_item_id")
    private ProcessItem processItem;
}
