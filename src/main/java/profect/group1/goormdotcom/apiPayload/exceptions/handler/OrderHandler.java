package profect.group1.goormdotcom.apiPayload.exceptions.handler;

import profect.group1.goormdotcom.apiPayload.code.BaseErrorCode;
import profect.group1.goormdotcom.apiPayload.exceptions.GeneralException;

public class OrderHandler extends GeneralException {
    public OrderHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
