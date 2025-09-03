package ir.dotin.llmproxyserver.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChatResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    @Data
    public static class Choice {
        private Integer index;
        private Map<String, Object> message;
        private Map<String, Object> delta;
        private String finish_reason;
        private String text;
    }
    
    @Data
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
}