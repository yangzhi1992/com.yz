package com.commons.fuyoo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequestResultVO {

    private List<?> checkRequestResponseList;

    private boolean allsuccess;

    private String statistis;

    private List normalHosts;

    private boolean contentSame;
}
