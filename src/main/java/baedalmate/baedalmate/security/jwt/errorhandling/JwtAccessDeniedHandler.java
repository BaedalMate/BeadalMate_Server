package baedalmate.baedalmate.security.jwt.errorhandling;

import baedalmate.baedalmate.errors.errorcode.ErrorCode;
import baedalmate.baedalmate.errors.errorcode.UserErrorCode;
import baedalmate.baedalmate.errors.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        setResponse(response, UserErrorCode.ACCESS_DENIED);
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