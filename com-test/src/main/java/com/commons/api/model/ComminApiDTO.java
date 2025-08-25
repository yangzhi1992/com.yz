package com.commons.api.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComminApiDTO implements Serializable {

    private static final long serialVersionUID = 2054484400672743236L;

    private String name;

    private String value;
}

