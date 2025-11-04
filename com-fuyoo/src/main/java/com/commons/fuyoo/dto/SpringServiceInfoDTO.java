package com.commons.fuyoo.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpringServiceInfoDTO {

    private GitInfo git;

    private BuildInfo build;

    private Long upTime;

    private List<String> springProfilesActive;

    private Long startTime;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GitInfo {
        private Map<String, Object> commit;
        private String id;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BuildInfo {
        private String name;
        private Long time;
        private Map<String, String> java;
        private Map<String, String> encoding;
        private String version;
        private String group;
        private String artifact;
    }
}
