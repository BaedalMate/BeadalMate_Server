package baedalmate.baedalmate.security.oauth2.provider;

import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.security.oauth2.authentication.AccessTokenSocialTypeToken;
import baedalmate.baedalmate.security.oauth2.exception.ExpiredAccessTokenException;
import baedalmate.baedalmate.security.oauth2.service.LoadUserService;
import baedalmate.baedalmate.security.repository.AuthRepository;
import baedalmate.baedalmate.security.user.OAuth2UserDetails;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {//AuthenticationProvider을 구현받아 authenticate와 supports를 구현해야 한다.

    private final LoadUserService loadUserService;  //restTemplate를 통해서 AccessToken을 가지고 회원의 정보를 가져오는 역할을 한다.
    private final AuthRepository authRepository;    //받아온 정보를 통해 DB에서 회원을 조회하는 역할을 한다.

    @Value("${spring.servlet.multipart.location}")
    private String path;

    @SneakyThrows
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {//ProviderManager가 호출한다. 인증을 처리한다
        try {
            OAuth2UserDetails oAuth2User = loadUserService.getOAuth2UserDetails((AccessTokenSocialTypeToken) authentication);
            //OAuth2UserDetails는  UserDetails를 상속받아 구현한 클래스이다. 이후 일반 회원가입 시 UserDetails를 사용하는 부분과의 다형성을 위해 이렇게 구현하였다.
            //getOAuth2UserDetails에서는 restTemplate과 AccessToken을 가지고 회원정보를 조회해온다 (식별자 값을 가져옴)

            User user = saveOrGet(oAuth2User);//받아온 식별자 값과 social로그인 방식을 통해 회원을 DB에서 조회 후 없다면 새로 등록해주고, 있다면 그대로 반환한다.
            oAuth2User.setRoles(user.getRole());//우리의 Role의 name은 ADMIN, USER, GUEST로 ROLE_을 붙여주는 과정이 필요하다. setRolse가 담당한다.
            oAuth2User.setId(user.getId());
            return AccessTokenSocialTypeToken.builder().principal(oAuth2User).authorities(oAuth2User.getAuthorities()).build();
            //AccessTokenSocialTypeToken객체를 반환한다. principal은 OAuth2UserDetails객체이다. (formLogin에서는 UserDetails를 가져와서 결국 ContextHolder에 저장하기 때문에)
            //이렇게 구현하면 UserDetails 타입으로 회원의 정보를 어디서든 조회할 수 있다.
        } catch (ExpiredAccessTokenException e) {
            throw e;
        }
    }

    @Transactional
    private User saveOrGet(OAuth2UserDetails oAuth2User) throws Exception {
        //socailID(식별값)과 어떤 소셜 로그인 유형인지를 통해 DB에서 조회
        Optional<User> userBySocial = authRepository.findBySocialTypeAndSocialId(oAuth2User.getSocialType(), oAuth2User.getSocialId());
        if (userBySocial.isPresent())
            return userBySocial.get();
        else {
            String nickname = oAuth2User.getUsername().length() > 5 ? oAuth2User.getUsername().substring(0, 5) : oAuth2User.getUsername();
            String imageUrl = download(oAuth2User.getImage());
            User user = User.builder()
                    .socialType(oAuth2User.getSocialType())
                    .socialId(oAuth2User.getSocialId())
                    .profileImage(imageUrl)
                    .nickname(oAuth2User.getUsername())
                    .role("GUEST").build();
            return authRepository.save(user);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenSocialTypeToken.class.isAssignableFrom(authentication); //AccessTokenSocialTypeToken타입의  authentication 객체이면 해당 Provider가 처리한다.
    }

    private String download(String fileUrl) throws Exception {
        URI url = URI.create(fileUrl);
        // 원격 파일 다운로드
        RestTemplate rt = new RestTemplate();
        ResponseEntity<byte[]> res = rt.getForEntity(url, byte[].class);
        byte[] buffer = res.getBody();

        // 로컬 서버에 저장
        Date date = new Date();
        String fileName = date.getTime() + "_profile"; // 파일명 (랜덤생성)
        String ext = "." + StringUtils.getFilenameExtension(fileUrl); // 확장자 추출
        Path target = Paths.get(path, fileName + ext); // 파일 저장 경로

        try {
            FileCopyUtils.copy(buffer, target.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "/images/" + fileName + ext;
    }
}