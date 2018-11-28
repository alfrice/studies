package com.iovation.service.clearkey.replayer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iovation.service.clearkey.replayer.ReplayUtils;
import com.iovation.service.clearkey.replayer.model.ReplayResult;
import com.iovation.service.tlm.model.publish.MapEvent;
import com.iovation.service.tlm.model.publish.MapRequest;
import com.iovation.service.tlm.model.publish.TransactionLogMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import javax.servlet.http.Part;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 12/14/16
 * Time: 3:26 PM
 * Description: ${CARET}
 */
@Service
@Slf4j
@ServerEndpoint("/topic/replayer/results")
public class ReplayService {


    private final SimpMessagingTemplate msgTemplate;
    private final ObjectMapper objectMapper;
    private final ReplayObserver observer;
    private final RestTemplate restTemplate = new RestTemplate();
    private Scheduler scheduler = Schedulers.io();


    @Autowired
    public ReplayService(ObjectMapper objectMapper, SimpMessagingTemplate msgTemplate, ReplayObserver observer) {
        this.objectMapper = objectMapper;
        this.msgTemplate = msgTemplate;

        this.observer = observer;
    }


    /**
     * replay a tlm
     *
     * @param file  The tlm file
     * @param parts The {@link Part} parts to parse un pwd, etc
     */
    public void replay(MultipartFile file, Collection<Part> parts) {

        _replay(file, parts)
                .subscribeOn(scheduler)
                .subscribe(observer);


    }

    private Observable<ReplayResult> _replay(MultipartFile file, Collection<Part> parts) {
        final ReplayResult result = new ReplayResult(file.getOriginalFilename());

        return Observable.create(subscriber -> {
            try {

                subscriber.onNext(_replay(file.getBytes(), result, parts));

            } catch (Exception e) {

                onError(e, result);


            } finally {

                subscriber.onCompleted();
                push(result);
            }


        });

    }


    /**
     * Replay tlm bytes
     *
     * @param bytes  bytes of tlm file
     * @param result The {@link ReplayResult} to be returned
     * @param parts  The list of {@link Part}s to parse username/pwd etc
     * @return a {@link ReplayResult}
     * @throws Exception exceptions are caught and inserted into the results at {@link ReplayService#replay(MultipartFile, Collection)}
     */
    private ReplayResult _replay(byte[] bytes, ReplayResult result, Collection<Part> parts) throws Exception {

        Map<String, String> props = getProperties(parts, result);

        TransactionLogMessage tlm = objectMapper.readValue(bytes, ReplayUtils.TLM_REF);

        String requestType = tlm.getHdr().getRequestType();
        MapEvent originalEvent = tlm.getBody().get(requestType);
        Integer status = originalEvent.getResponse().getStatus();

        if (originalEvent.getRequest().getBody() == null && originalEvent.getRequest().getMethod().equals("POST")) {
            log.error("no body on this tlm request post");
        }
        HttpEntity<?> request = getHeaders(originalEvent.getRequest(), props);


        _replay(originalEvent, request, result, props);



        return result;


    }


    private void _replay(MapEvent originalEvent, HttpEntity<?> newRequest, ReplayResult result, Map<String, String> props) {

        MapRequest originalRequest = originalEvent.getRequest();

        String method = originalRequest.getMethod();

        String url = props.getOrDefault("url", null);

        url = !StringUtils.isEmpty(url) ? updateEndpoint(url, originalRequest.getUrl()) : originalRequest.getUrl();

        result.setUrl(url);

        StringBuilder message = new StringBuilder();

        String returnValue = null;

        ResponseEntity<String> response = null;

        if (url != null) {
            switch (method.toUpperCase()) {
                case "POST":
                    response = post(newRequest, url);
                    break;
                case "GET":
                    response = get(newRequest, url);
                    break;
                case "PUT":
                    returnValue = put(newRequest, url);
                    break;
                default:
                    response = post(newRequest, url);
            }
            if (response != null) {
                Integer statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : -1;
                result.setStatusCode(statusCode);
                result.setResponse(response.getBody());
            } else {
                result.setResponse(returnValue);
            }
            result.setSuccess(true);

            return;
        } else {
            message.append("URL CANNOT BE NULL!!  ");
        }

        message.append("Response is null");
        result.setMessage(message.toString());

    }

    private Map<String, String> getProperties(Collection<Part> parts, ReplayResult result) throws UncheckedIOException {

        Map<String, String> results = new HashMap<>();
        parts.forEach(entry -> this.getPartValues.apply(entry, results));

        return results;
    }


    private CheckedFunction<Part, Map<String, String>> getPartValues = (entry, map) -> {

        try {
            InputStream is = entry.getInputStream();
            String value = IOUtils.toString(is, "UTF-8");
            map.put(entry.getName(), value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return map;

    };


    @FunctionalInterface
    private interface CheckedFunction<T, R> {
        R apply(T t, R r) throws UncheckedIOException;
    }


    /**
     * adds stack trace to result
     *
     * @param elements the {@link StackTraceElement}
     * @param result   the {@link ReplayResult}
     */
    private void addStackTrace(StackTraceElement[] elements, ReplayResult result) {
        String[] stackTrace = new String[elements.length + 1];

        stackTrace[0] = result.getFileName();
        int count = 0;
        for (StackTraceElement element : elements) {
            stackTrace[++count] = element.getLineNumber() + ":" + element.getClassName() + "/" + element.getMethodName();
        }

        result.setStacktrace(stackTrace);
    }

    /**
     * Piece together the new endpoint for user provided server;
     *
     * @param url         the new server provided
     * @param originalUrl the url in the tlm
     * @return the new url
     */
    private String updateEndpoint(String url, String originalUrl) {
        //host:port

        int ordinal = originalUrl.contains("://") ? 3 : 0;
        int indexOfEndpoint = StringUtils.ordinalIndexOf(originalUrl, "/", ordinal);
        String endpoint = originalUrl.substring(indexOfEndpoint);
        return url + endpoint;

    }


    /**
     * Create new headers for this replay;
     *
     * @param originalRequest The original request.
     * @param props           the properties in the replay post headers
     * @return the new headers
     */
    private HttpEntity<?> getHeaders(MapRequest originalRequest, Map<String, String> props) {


        String credsString = props.getOrDefault("creds", null);
        Map<String, String> originalHeaders = originalRequest.getHeaders();
        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        Map<String, Object> body = originalRequest.getBody();


        headers.setAll(originalHeaders);
        if (credsString != null) {
            String[] creds = credsString.split(":");

            String auth = creds[0] + ":" + creds[1];
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));

            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);

        }

        return body != null ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);

    }


    private ResponseEntity<String> post(HttpEntity<?> newRequest, String url) {

        return restTemplate.postForEntity(url, newRequest, String.class);

    }

    private String put(HttpEntity<?> newRequest, String url) {

        restTemplate.put(url, newRequest);
        return "PUT complete at " + url;

    }


    private ResponseEntity<String> get(HttpEntity<?> newRequest, String url) {

        return restTemplate.exchange(url, HttpMethod.GET, newRequest, String.class);

    }


    /**
     * Websocket push notify
     *
     * @param replayResult push the {@link ReplayResult to subscribers of topic/response}
     */
    @SendTo("/topic/response")
    public void push(ReplayResult replayResult)  {

        String str = null;
        try {
            str = objectMapper.writeValueAsString(replayResult);
        } catch (JsonProcessingException e) {

            onError(e, replayResult);
        }


        msgTemplate.convertAndSend("/topic/response", str);


    }

    public void onError(Throwable e, ReplayResult result){
        e.printStackTrace();
        addStackTrace(e.getStackTrace(), result);
        result.setSuccess(false);
        String extraMessage = e.getCause()!=null? ", "+e.getCause().getMessage():"";
        result.setMessage(e.getMessage() + extraMessage);


        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException ex = (HttpStatusCodeException) e;
            result.setStatusCode(ex.getStatusCode().value());
            result.setMessage(ex.getMessage());
            result.setResponse(ex.getResponseBodyAsString());


        } else {
            if (e instanceof UncheckedIOException) {
                e = e.getCause();

            }
            result.setMessage("rest method failed: " + e.getMessage());
            String cause = e.getCause() != null ? e.getCause().getMessage() : e.toString();
            result.setResponse(cause);

        }
    }


}

