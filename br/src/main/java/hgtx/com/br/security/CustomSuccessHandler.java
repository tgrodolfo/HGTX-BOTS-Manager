package hgtx.com.br.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirect = request.getParameter("redirect");

        if (redirect != null && !redirect.isBlank() && redirect.startsWith("/")) {
            response.sendRedirect(redirect);
        } else {
            response.sendRedirect("/bots"); // fallback padrão
        }
    }
}