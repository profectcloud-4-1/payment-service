package profect.group1.goormdotcom.apiPayload.exceptions.handler;

import profect.group1.goormdotcom.apiPayload.code.BaseErrorCode;
import profect.group1.goormdotcom.apiPayload.exceptions.GeneralException;

public class ProductHandler extends GeneralException {
    public ProductHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
