package profect.group1.goormdotcom.apiPayload.code.status;

import profect.group1.goormdotcom.apiPayload.code.BaseErrorCode;
import profect.group1.goormdotcom.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    INVALID_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청 파라미터입니다"),
    AUTH_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH400", "잘못된 비밀번호 형식입니다"),
    AUTH_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH409", "이미 존재하는 이메일입니다"),
    AUTH_NOT_EXISTS(HttpStatus.NOT_FOUND, "AUTH404", "존재하지 않는 사용자입니다"),
    
    COMMON_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "존재하지 않는 공통 코드입니다"),
    COMMON_CODE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "COMMON409", "이미 존재하는 공통 코드입니다"),
    COMMON_CODE_INVALID_GROUP_NAME(HttpStatus.BAD_REQUEST, "COMMON400", "허용되지 않은 그룹이름입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}