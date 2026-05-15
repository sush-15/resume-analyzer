package com.resumeanalyzer.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.exception.TikaException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

@Component
public class PdfTextExtractor {

    private static final Logger log = Logger.getLogger(PdfTextExtractor.class.getName());
    private final Tika tika = new Tika();

    public String extractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        log.info("Extracting text from: " + filename + ", type: " + contentType);

        if (isPdf(contentType, filename)) {
            return extractFromPdf(file);
        }
        return extractWithTika(file);
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        // PDFBox 3.x uses Loader.loadPDF() instead of PDDocument.load()
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            log.info("PDF extraction complete. Characters: " + text.length());
            return text;
        }
    }

    private String extractWithTika(MultipartFile file) throws IOException {
        try (InputStream stream = file.getInputStream()) {
            try {
                String text = tika.parseToString(stream);
                log.info("Tika extraction complete. Characters: " + text.length());
                return text;
            } catch (TikaException e) {
                throw new IOException("Failed to parse document: " + e.getMessage(), e);
            }
        }
    }

    private boolean isPdf(String contentType, String filename) {
        return "application/pdf".equals(contentType) ||
               (filename != null && filename.toLowerCase().endsWith(".pdf"));
    }
}
