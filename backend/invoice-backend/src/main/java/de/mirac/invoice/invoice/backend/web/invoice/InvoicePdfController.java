package de.mirac.invoice.invoice.backend.web.invoice;
// Möglichkeit, die PDF umzuwandeln und automatisch herunterzuladen.
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin(origins = "http://localhost:5173")
public class InvoicePdfController {

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody PdfRequest request) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(request.html(), out);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + request.filename() + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(out.toByteArray());
    }

    public record PdfRequest(String html, String filename) {}
}