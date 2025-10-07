package com.commons.elasticsearch.simple.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScoreWrapper<T> {
    private float score;
    private T realData;
}
