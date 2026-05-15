package com.resumeanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.response.AiAnalysisResponse;
import com.resumeanalyzer.entity.AiFeedback;
import com.resumeanalyzer.entity.Resume;
import com.resumeanalyzer.repository.AiFeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class AiAnalysisService {

    private static final Logger log = Logger.getLogger(AiAnalysisService.class.getName());

    private final AiFeedbackRepository aiFeedbackRepository;
    private final ObjectMapper objectMapper;
    private final WebClient webClient = WebClient.create();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public AiAnalysisService(AiFeedbackRepository aiFeedbackRepository, ObjectMapper objectMapper) {
        this.aiFeedbackRepository = aiFeedbackRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiFeedback generateAndSaveFeedback(Resume resume) {
        log.info("Generating AI feedback for resume ID: " + resume.getId());
        String feedbackText;
        try {
            feedbackText = callGeminiApi(resume.getExtractedText());
        } catch (Exception e) {
            log.warning("Gemini API failed, using fallback: " + e.getMessage());
            feedbackText = generateFallbackFeedback(resume.getExtractedText());
        }

        List<String> suggestions   = extractSection(feedbackText, "SUGGESTIONS");
        List<String> missingSkills = extractSection(feedbackText, "MISSING SKILLS");
        List<String> improvements  = extractSection(feedbackText, "IMPROVEMENTS");

        AiFeedback feedback = AiFeedback.builder()
                .resume(resume)
                .user(resume.getUser())
                .feedbackText(feedbackText)
                .suggestions(toJson(suggestions))
                .missingSkills(toJson(missingSkills))
                .improvements(toJson(improvements))
                .build();

        AiFeedback saved = aiFeedbackRepository.save(feedback);
        log.info("AI feedback saved with ID: " + saved.getId());
        return saved;
    }

    public AiAnalysisResponse getFeedbackByResumeId(Long resumeId) {
        Resume resume = new Resume();
        resume.setId(resumeId);
        AiFeedback feedback = aiFeedbackRepository.findTopByResumeOrderByGeneratedAtDesc(resume)
                .orElseThrow(() -> new RuntimeException("AI feedback not found for resume: " + resumeId));

        return AiAnalysisResponse.builder()
                .id(feedback.getId())
                .resumeId(feedback.getResume().getId())
                .feedbackText(feedback.getFeedbackText())
                .suggestions(fromJson(feedback.getSuggestions()))
                .missingSkills(fromJson(feedback.getMissingSkills()))
                .improvements(fromJson(feedback.getImprovements()))
                .generatedAt(feedback.getGeneratedAt())
                .build();
    }

    private String callGeminiApi(String resumeText) {
        String truncated = resumeText.length() > 3000 ? resumeText.substring(0, 3000) + "..." : resumeText;
        String prompt = buildPrompt(truncated);

        Map<String, Object> requestBody = Map.of(
            "contents", new Object[]{
                Map.of("parts", new Object[]{ Map.of("text", prompt) })
            }
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return extractTextFromResponse(response);
    }

    private String buildPrompt(String resumeText) {
        return "You are an expert ATS resume analyzer for software engineering roles.\n" +
               "Analyze the resume and provide structured feedback with these sections:\n\n" +
               "STRENGTHS:\n- [strength 1]\n- [strength 2]\n\n" +
               "WEAKNESSES:\n- [weakness 1]\n\n" +
               "MISSING SKILLS:\n- [skill 1]\n- [skill 2]\n\n" +
               "SUGGESTIONS:\n- [suggestion 1]\n- [suggestion 2]\n\n" +
               "IMPROVEMENTS:\n- [improvement 1]\n\n" +
               "RESUME:\n" + resumeText;
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<String, Object> response) {
        try {
            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> first    = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content  = (Map<?, ?>) first.get("content");
            List<?> parts      = (List<?>) content.get("parts");
            Map<?, ?> part     = (Map<?, ?>) parts.get(0);
            return (String) part.get("text");
        } catch (Exception e) {
            log.warning("Failed to parse Gemini response: " + e.getMessage());
            return "AI analysis could not be completed. Please check your API key.";
        }
    }

    private String generateFallbackFeedback(String resumeText) {
        String lower = resumeText.toLowerCase();
        StringBuilder sb = new StringBuilder();
        sb.append("STRENGTHS:\n");
        if (lower.contains("java"))   sb.append("- Java programming skills detected\n");
        if (lower.contains("spring")) sb.append("- Spring Framework experience found\n");
        if (lower.contains("project"))sb.append("- Project experience included\n");
        sb.append("\nWEAKNESSES:\n");
        if (!lower.contains("docker"))sb.append("- Docker/containerization skills missing\n");
        if (!lower.contains("aws"))   sb.append("- Cloud platform experience not mentioned\n");
        sb.append("\nMISSING SKILLS:\n");
        if (!lower.contains("docker"))sb.append("- Docker\n");
        if (!lower.contains("aws"))   sb.append("- AWS / Cloud\n");
        if (!lower.contains("kafka")) sb.append("- Apache Kafka\n");
        sb.append("\nSUGGESTIONS:\n");
        sb.append("- Add quantifiable achievements (e.g., 'Reduced API response time by 30%')\n");
        sb.append("- Include GitHub profile link\n");
        sb.append("- Add a professional summary at the top\n");
        sb.append("\nIMPROVEMENTS:\n");
        sb.append("- Describe projects with: Problem → Solution → Impact format\n");
        sb.append("- Add tech stack used in each project\n");
        return sb.toString();
    }

    private List<String> extractSection(String text, String sectionName) {
        List<String> result = new ArrayList<>();
        try {
            String[] lines = text.split("\n");
            boolean inSection = false;
            for (String line : lines) {
                if (line.toUpperCase().contains(sectionName.toUpperCase() + ":")) {
                    inSection = true; continue;
                }
                if (inSection) {
                    if (line.trim().isEmpty()) continue;
                    if (line.matches("^[A-Z ]+:.*")) break;
                    if (line.startsWith("-")) result.add(line.substring(1).trim());
                }
            }
        } catch (Exception e) { /* ignore */ }
        return result;
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (JsonProcessingException e) { return "[]"; }
    }

    @SuppressWarnings("unchecked")
    private List<String> fromJson(String json) {
        try {
            if (json == null || json.isBlank()) return List.of();
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) { return List.of(); }
    }
}
