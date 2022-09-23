package baedalmate.baedalmate.oauth.entrypoint;

import baedalmate.baedalmate.errors.errorcode.UserErrorCode;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        System.out.println("error");
        String exception = (String) request.getAttribute("exception");
        if (exception == null) {
            setResponse(response, UserErrorCode.UNKNOWN_ERROR);
        }
        //잘못된 타입의 토큰인 경우
        else if (exception.equals(UserErrorCode.WRONG_TYPE_TOKEN.getHttpStatus())) {
            setResponse(response, UserErrorCode.WRONG_TYPE_TOKEN);
        }
        //토큰 만료된 경우
        else if (exception.equals(UserErrorCode.EXPIRED_TOKEN.getHttpStatus())) {
            setResponse(response, UserErrorCode.EXPIRED_TOKEN);
        }
        //지원되지 않는 토큰인 경우
        else if (exception.equals(UserErrorCode.UNSUPPORTED_TOKEN.getHttpStatus())) {
            setResponse(response, UserErrorCode.UNSUPPORTED_TOKEN);
        } else {
            setResponse(response, UserErrorCode.ACCESS_DENIED);
        }
    }

    //한글 출력을 위해 getWriter() 사용
    private void setResponse(HttpServletResponse response, UserErrorCode UserErrorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", UserErrorCode.getMessage());
        responseJson.put("code", UserErrorCode.getHttpStatus());

        response.getWriter().print(responseJson);
    }
}
