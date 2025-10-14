package profect.group1.goormdotcom.apiPayload.exceptions.handler;

import profect.group1.goormdotcom.apiPayload.code.BaseErrorCode;
import profect.group1.goormdotcom.apiPayload.exceptions.GeneralException;

public class BrandHandler extends GeneralException {
    public BrandHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
