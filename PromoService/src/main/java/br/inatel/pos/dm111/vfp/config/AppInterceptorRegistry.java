package br.inatel.pos.dm111.vfp.config;

import br.inatel.pos.dm111.vfp.api.core.Interceptor.AuthenticationInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class AppInterceptorRegistry implements WebMvcConfigurer {
    private static final List<String> ENDPOINTS_PATTERN = List.of(
            "/valefood/restaurants/promos**",
            "/valefood/restaurants/promos/**"
    );

    private final AuthenticationInterceptor interceptor;

    public AppInterceptorRegistry(AuthenticationInterceptor interceptor){
        this.interceptor = interceptor;
    }

}
