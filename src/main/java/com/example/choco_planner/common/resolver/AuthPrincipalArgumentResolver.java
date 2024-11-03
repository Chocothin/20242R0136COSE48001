package com.example.choco_planner.common.resolver;

import com.example.choco_planner.common.aop.AuthPrincipal;
import com.example.choco_planner.common.domain.CustomUser;
import com.example.choco_planner.common.domain.CustomUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 권한과 같은 사용자 정보가 필요할 때 사용하는 Argument Resolver
 * @AuthPrincipal Annotation을 사용하여 사용자 정보를 가져올 수 있음
 */
@Component
public class AuthPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CustomUser.class) &&
                parameter.hasParameterAnnotation(AuthPrincipal.class);
    }
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            System.out.println("Authentication is null in SecurityContext"); // 디버그 로그
            return null;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUser customUser = ((CustomUserDetails) authentication.getPrincipal()).getCustomUser();
            System.out.println("Resolved CustomUser: " + customUser.getUsername()); // 디버그 로그
            return customUser;
        } else {
            System.out.println("Authentication principal is not instance of CustomUserDetails"); // 디버그 로그
            return null;
        }
    }

}
