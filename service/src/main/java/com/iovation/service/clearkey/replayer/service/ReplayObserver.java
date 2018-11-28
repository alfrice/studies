package com.iovation.service.clearkey.replayer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iovation.service.clearkey.replayer.model.ReplayResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observer;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 1/18/17
 * Time: 3:52 PM
 * Description: Observer for the replay of a request on a tlm
 */

@Component
@Slf4j
@Setter
@Getter
public class ReplayObserver implements Observer<ReplayResult> {


    private final ObjectMapper objectMapper;
    private ReplayResult result;

    @Autowired
    public ReplayObserver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {}

    @Override
    public void onNext(ReplayResult result) { this.result = result;}


}
