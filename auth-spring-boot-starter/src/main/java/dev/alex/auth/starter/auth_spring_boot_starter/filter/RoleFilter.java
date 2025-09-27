package dev.alex.auth.starter.auth_spring_boot_starter.filter;

import dev.alex.auth.starter.auth_spring_boot_starter.model.UserContext;
import dev.alex.auth.starter.auth_spring_boot_starter.util.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RoleFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        UserContext userContext = new UserContext();

        userContext.setRole(request.getHeader("X-user-role"));
        userContext.setUserId(request.getIntHeader("X-user-id"));

        UserContextHolder.setContext(userContext);

        filterChain.doFilter(request, response);
    }
}
