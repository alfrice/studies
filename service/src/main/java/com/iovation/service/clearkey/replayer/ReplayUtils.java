package com.iovation.service.clearkey.replayer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.iovation.service.tlm.model.publish.TransactionLogMessage;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 12/20/16
 * Time: 10:44 AM
 * Description: Replayer Utilty class
 */
public class ReplayUtils {

    public static final TypeReference<TransactionLogMessage> TLM_REF = new TypeReference<TransactionLogMessage>() {
    };
}
