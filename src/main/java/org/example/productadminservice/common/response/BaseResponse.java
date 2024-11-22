package org.example.productadminservice.common.response;

import static org.example.productadminservice.common.response.BaseResponseStatus.*;
import static org.example.productadminservice.common.response.BaseResponseStatus.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public record BaseResponse<T>(HttpStatusCode httpStatus, Boolean isSuccess, String message, T result) {
	/**
	 * 필요값: Http 상태코드, 성공여부, 메시지, 에러코드, 결과값
	 */

	// 요청 성공 -> return 객체 필요의 경우
	public BaseResponse(T result) {
		this(HttpStatus.OK, true, SUCCESS.getMessage(), result);
	}

	// 요청 성공 -> return 객체 필요없는 경우
	public BaseResponse() {
		this(HttpStatus.OK, true, SUCCESS.getMessage(),  null);
	}

	// 요청 실패
	public BaseResponse(BaseResponseStatus status) {
		this(status.getHttpStatusCode(), false, status.getMessage(), null);
	}

	public T getResult() {
		return result;
	}
}