package com.framework.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/** JSONPlaceholder /posts resource. A null id is left out of the request JSON (the server assigns it). */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Post(Integer userId, Integer id, String title, String body) {
}
