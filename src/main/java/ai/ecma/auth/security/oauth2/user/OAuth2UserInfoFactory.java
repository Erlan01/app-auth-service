package ai.ecma.auth.security.oauth2.user;

import ai.ecma.auth.enums.AuthProvider;
import ai.ecma.auth.exception.RestException;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.github.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.linkedin.toString())) {
            return new LinkedinOAuth2UserInfo(attributes);
        } else {
            throw RestException.restThrow("Sorry! Login with " + registrationId + " is not supported yet.", HttpStatus.BAD_REQUEST);
        }
    }
}
