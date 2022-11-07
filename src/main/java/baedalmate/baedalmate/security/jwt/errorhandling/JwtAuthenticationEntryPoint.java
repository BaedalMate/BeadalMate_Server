package baedalmate.baedalmate.security.jwt.errorhandling;

import com.eomyoosang.securityexample.error.errorcode.ErrorCode;
import com.eomyoosang.securityexample.error.errorcode.UserErrorCode;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorCode exception = (ErrorCode) request.getAttribute("exception");
        if(exception == null) {
//            setResponse(response, ErrorCode.FAILED_MESSAGE);
        }
        //잘못된 타입의 토큰인 경우
        else if(exception.equals(UserErrorCode.WRONG_TYPE_TOKEN)) {
            setResponse(response, UserErrorCode.WRONG_TYPE_TOKEN);
        }
        else if(exception.equals(UserErrorCode.WRONG_TYPE_SIGNATURE)) {
            setResponse(response, UserErrorCode.WRONG_TYPE_SIGNATURE);
        }
        //토큰 만료된 경우
        else if(exception.equals(UserErrorCode.EXPIRED_ACCESS_TOKEN)) {
            setResponse(response, UserErrorCode.EXPIRED_ACCESS_TOKEN);
        }
        else {
            setResponse(response, UserErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

//    한글 출력을 위해 getWriter() 사용
    private void setResponse(HttpServletResponse response, ErrorCode UserErrorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(UserErrorCode.getHttpStatus().value());

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", UserErrorCode.getMessage());
        responseJson.put("code", UserErrorCode.getHttpStatus().value());

        response.getWriter().print(responseJson);
    }
}
