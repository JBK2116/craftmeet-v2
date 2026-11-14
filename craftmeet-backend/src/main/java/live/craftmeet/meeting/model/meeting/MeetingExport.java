package live.craftmeet.meeting.model.meeting;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingExport {

    @Column(name = "pdf_url", nullable = true, length = 2048)
    private String pdfUrl;

    @Column(name = "last_exported_at", nullable = true)
    private Instant lastExportedAt;

}
