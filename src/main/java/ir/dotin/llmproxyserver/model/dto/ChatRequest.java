package ir.dotin.llmproxyserver.model.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ChatRequest {
    private String model;
    private List<Map<String, Object>> messages;
    private Double temperature;
    private Double top_p;
    private Integer max_tokens;
    private Double frequency_penalty;
    private Double presence_penalty;
    private List<String> stop;
    private Boolean stream;
    private Object response_format;
    private Object tools;
    private Object tool_choice;
    private Boolean logprobs;
    private Integer top_logprobs;
    private Integer n;
    private Integer seed;
    private Map<String, Object> extra_body;
}