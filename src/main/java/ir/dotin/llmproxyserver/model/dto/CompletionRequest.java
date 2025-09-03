package ir.dotin.llmproxyserver.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompletionRequest {
    private String model;
    private String prompt;
    private String suffix;
    private Integer max_tokens;
    private Double temperature;
    private Double top_p;
    private Double frequency_penalty;
    private Double presence_penalty;
    private List<String> stop;
    private Boolean stream;
}