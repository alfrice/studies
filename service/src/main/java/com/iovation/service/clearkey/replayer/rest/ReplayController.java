package com.iovation.service.clearkey.replayer.rest;

import com.iovation.service.clearkey.replayer.service.ReplayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 12/14/16
 * Time: 3:29 PM
 * Description: ${CARET}
 */
@SuppressWarnings("MVCPathVariableInspection")
@RestController
@Api(value = "Rest service for the data transformations",
        description = "API for the Replayer")
@Getter
@Setter
public class ReplayController {


    private final ReplayService service;

    private final String READ = "/tlm/{jobId}";


    @Autowired
    public ReplayController(ReplayService service) {

        this.service = service;
    }


    @MessageMapping("/replay")
    @ApiOperation(value = "ingest tlm",
            notes = "multi part file upload.",
            response = ResponseEntity.class,
            httpMethod = "POST")
    @RequestMapping(path = "/replayer/replay", method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> transformFile(@RequestParam MultipartFile file,
                                           HttpServletRequest request) throws Exception {

        service.replay(file, request.getParts());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);

    }


}
