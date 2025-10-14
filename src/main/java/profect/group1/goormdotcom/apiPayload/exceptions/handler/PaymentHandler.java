package profect.group1.goormdotcom.apiPayload.exceptions.handler;

import profect.group1.goormdotcom.apiPayload.code.BaseErrorCode;
import profect.group1.goormdotcom.apiPayload.exceptions.GeneralException;

public class PaymentHandler extends GeneralException {
    public PaymentHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
