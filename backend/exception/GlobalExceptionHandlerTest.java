package com.ecommerce.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar RecursoNaoEncontradoException corretamente")
    void deveTratarRecursoNaoEncontrado() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Produto não encontrado");

        ResponseEntity<Object> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Recurso não encontrado", body.get("error"));
    }

    @Test
    @DisplayName("Deve tratar RegraNegocioException corretamente")
    void deveTratarRegraNegocio() {
        RegraNegocioException ex = new RegraNegocioException("Estoque insuficiente");

        ResponseEntity<Object> response = handler.handleBusiness(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Regra de negócio violada", body.get("error"));
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException com mensagens de campo")
    void deveTratarMethodArgumentNotValid() {
        FieldError error = new FieldError("produto", "nome", "não pode ser vazio");
        FieldError error2 = new FieldError("produto", "preco", "deve ser maior que zero");

        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error, error2));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Object> response = handler.handleBeanValidation(methodArgumentNotValidException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(((Map<?, ?>) body.get("details")).containsKey("nome"));
        assertTrue(((Map<?, ?>) body.get("details")).containsKey("preco"));
    }

    @Test
    @DisplayName("Deve tratar ConstraintViolationException com mensagens")
    void deveTratarConstraintViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("inválido");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<Object> response = handler.handleConstraint(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Parâmetros inválidos", body.get("message"));
        assertTrue(((Map<?, ?>) body.get("details")).containsKey("email"));
    }

    @Test
    @DisplayName("Deve tratar Exception genérica com sucesso")
    void deveTratarExceptionGenerica() {
        Exception ex = new Exception("Erro inesperado");

        ResponseEntity<Object> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Erro interno", body.get("error"));
    }

    @Test
    @DisplayName("Deve montar body sem detalhes com sucesso")
    void deveMontarBodySemDetalhes() {
        ResponseEntity<Object> response = invokeBody(HttpStatus.OK, "Sucesso", "Tudo certo", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Sucesso", body.get("error"));
        assertFalse(body.containsKey("details"));
    }

    // Reflection helper para testar método privado "body"
    private ResponseEntity<Object> invokeBody(HttpStatus status, String error, String msg, Map<String, ?> details) {
        try {
            var method = GlobalExceptionHandler.class.getDeclaredMethod(
                    "body", HttpStatus.class, String.class, String.class, Map.class);
            method.setAccessible(true);
            return (ResponseEntity<Object>) method.invoke(handler, status, error, msg, details);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
