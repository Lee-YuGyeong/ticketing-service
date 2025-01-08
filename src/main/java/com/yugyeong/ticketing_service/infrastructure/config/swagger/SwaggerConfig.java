package com.yugyeong.ticketing_service.infrastructure.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("티켓팅 서비스 API")
                .version("1.0")
                .description("이 API는 티켓팅 서비스를 제공하며, 사용자 인증 및 티켓 예약, 관리 등의 기능을 제공합니다."));
    }

    @Bean
    public OpenApiCustomizer customizeJwtAuthorization() {
        return openApi -> {
            openApi.getComponents().addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization")
                    .scheme("bearer")
                    .bearerFormat("JWT"));

            openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

            Paths paths = openApi.getPaths();
            paths.forEach((path, pathItem) -> {
                if (!path.startsWith("/auth/")) {
                    pathItem.readOperations().forEach(operation -> {
                        operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                    });
                }
            });
        };
    }

    @Bean
    public OpenApiCustomizer customizeLoginEndpoint() {
        return openApi -> {
            // 로그인 요청 스키마 정의
            Schema<?> loginRequestSchema = new Schema<>()
                .addProperty("email", new Schema<>().type("string").example("test@naver.com"))
                .addProperty("password", new Schema<>().type("string").example("test123456789"));

            // 로그인 성공 응답 정의 (SuccessResponse)
            ApiResponse response200 = new ApiResponse()
                .description("로그인 성공")
                .content(new Content().addMediaType("application/json",
                    new MediaType().schema(new Schema<>()
                        .type("object")
                        .addProperty("title", new Schema<>().type("string"))
                        .addProperty("status", new Schema<>().type("integer"))
                        .addProperty("detail", new Schema<>().type("string"))
                        .addProperty("data", new Schema<>()
                            .type("object")
                            .addProperty("email", new Schema<>().type("string"))
                            .addProperty("username", new Schema<>().type("string"))
                        )
                    )));

            // 로그인 실패 응답 정의 (ErrorResponse)
            ApiResponse response401 = new ApiResponse()
                .description("인증 실패")
                .content(new Content().addMediaType("application/json",
                    new MediaType().schema(new Schema<>()
                        .type("object")
                        .addProperty("type", new Schema<>().type("string"))
                        .addProperty("title", new Schema<>().type("string"))
                        .addProperty("status", new Schema<>().type("integer"))
                        .addProperty("detail", new Schema<>().type("string"))
                        .addProperty("instance", new Schema<>().type("string"))
                    )));

            // 요청 바디 정의
            RequestBody requestBody = new RequestBody()
                .description("로그인 요청 데이터")
                .required(true)
                .content(new Content().addMediaType("application/json",
                    new MediaType().schema(loginRequestSchema)));

            // 엔드포인트 정의
            Operation loginOperation = new Operation()
                .addTagsItem("auth-controller") // auth-controller 태그 추가
                .summary("사용자 로그인")
                .description("사용자 로그인을 진행합니다.")
                .requestBody(requestBody)
                .responses(new ApiResponses()
                    .addApiResponse("200", response200)
                    .addApiResponse("401", response401));

            // 엔드포인트를 Swagger에 추가
            PathItem loginPath = new PathItem().post(loginOperation);
            Paths paths = openApi.getPaths();
            paths.addPathItem("/auth/login", loginPath);
        };
    }
}
