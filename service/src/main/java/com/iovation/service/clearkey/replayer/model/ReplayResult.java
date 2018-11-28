package com.iovation.service.clearkey.replayer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 1/23/17
 * Time: 3:19 PM
 * Description: Results of replay model item
 */
@Slf4j
@Data
public class ReplayResult {

    String fileName;
    Integer statusCode;
    Boolean success;
    String message;
    String url;
    String response;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String[] stacktrace;

    public ReplayResult(String originalFilename) {
        this.fileName=originalFilename;
    }
}
