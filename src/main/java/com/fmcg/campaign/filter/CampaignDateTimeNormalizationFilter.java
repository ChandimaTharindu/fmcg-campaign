package com.fmcg.campaign.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ReadListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class CampaignDateTimeNormalizationFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!isCampaignCreateRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        JsonNode json = objectMapper.readTree(body);
        normalizeDateField(json, "fromDateTime");
        normalizeDateField(json, "toDateTime");

        byte[] normalizedBody = objectMapper.writeValueAsBytes(json);
        HttpServletRequest wrapped = new CachedBodyRequestWrapper(request, normalizedBody);
        filterChain.doFilter(wrapped, response);
    }

    private boolean isCampaignCreateRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().startsWith("/api/v1/campaigns")
                && request.getContentType() != null
                && request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private void normalizeDateField(JsonNode json, String fieldName) {
        JsonNode node = json.get(fieldName);
        if (node == null || !node.isTextual()) {
            return;
        }

        LocalDateTime parsed = LocalDateTime.parse(node.asText(), ISO);
        ((com.fasterxml.jackson.databind.node.ObjectNode) json).put(fieldName, parsed.format(ISO));
        log.debug("Normalized field {} to {}", fieldName, parsed);
    }

    private static class CachedBodyRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] body;

        CachedBodyRequestWrapper(HttpServletRequest request, byte[] body) {
            super(request);
            this.body = body;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
            return new ServletInputStream() {
                @Override
                public int read() {
                    return inputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    // no-op
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}