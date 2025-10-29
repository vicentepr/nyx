package com.ecommerce.controller;

import com.ecommerce.model.Usuario;
import com.ecommerce.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService service;

    @Autowired
    private ObjectMapper mapper;

    private Usuario usuario;

    @BeforeEach
void setup() {
    usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNome("Danielle");
    usuario.setEmail("danielle@example.com");
    usuario.setTelefone("11999999999"); // ✅ adiciona campo obrigatório
}


    // ✅ TESTE POST - Criar usuário
    @Test
    @DisplayName("Deve criar usuário com sucesso (201)")
    void deveCriarUsuarioComSucesso() throws Exception {
        Mockito.when(service.salvar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuário criado com sucesso!"))
                .andExpect(jsonPath("$.usuario.nome").value("Danielle"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao salvar usuário inválido")
    void deveRetornarErroAoSalvarUsuario() throws Exception {
        Mockito.when(service.salvar(any(Usuario.class)))
                .thenThrow(new RuntimeException("Erro ao salvar usuário"));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro ao salvar usuário: Erro ao salvar usuário"));
    }

    // ✅ TESTE GET - Listar todos
    @Test
    @DisplayName("Deve listar todos os usuários com sucesso (200)")
    void deveListarUsuarios() throws Exception {
        Mockito.when(service.listarTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Danielle"));
    }

    // ✅ TESTE GET - Buscar por ID
    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso (200)")
    void deveBuscarPorIdComSucesso() throws Exception {
        Mockito.when(service.buscarPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Danielle"));
    }

    @Test
    @DisplayName("Deve retornar 404 se usuário não for encontrado por ID")
    void deveRetornar404SeUsuarioNaoEncontradoPorId() throws Exception {
        Mockito.when(service.buscarPorId(anyLong()))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado: Usuário não encontrado"));
    }

    // ✅ TESTE PUT - Atualizar usuário
    @Test
    @DisplayName("Deve atualizar usuário com sucesso (200)")
    void deveAtualizarUsuario() throws Exception {
        usuario.setNome("Danielle Atualizada");
        Mockito.when(service.atualizar(anyLong(), any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Danielle Atualizada"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao atualizar usuário inválido")
    void deveRetornarErroAoAtualizarUsuario() throws Exception {
        Mockito.when(service.atualizar(anyLong(), any(Usuario.class)))
                .thenThrow(new RuntimeException("Erro ao atualizar usuário"));

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro ao atualizar usuário: Erro ao atualizar usuário"));
    }

    // ✅ TESTE DELETE - Excluir usuário
    @Test
    @DisplayName("Deve excluir usuário com sucesso (204)")
    void deveExcluirUsuario() throws Exception {
        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 500 ao ocorrer erro na exclusão")
    void deveRetornarErroAoExcluirUsuario() throws Exception {
        Mockito.doThrow(new RuntimeException("Erro ao excluir usuário"))
                .when(service).deletar(anyLong());

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro ao excluir usuário: Erro ao excluir usuário"));
    }

    // ✅ TESTE GET - Buscar por nome
    @Test
    @DisplayName("Deve buscar usuário por nome com sucesso (200)")
    void deveBuscarPorNomeComSucesso() throws Exception {
        Mockito.when(service.buscarPorNome(anyString())).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/nome")
                        .param("nome", "Danielle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Danielle"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar usuário inexistente por nome")
    void deveRetornar404AoBuscarUsuarioPorNomeInexistente() throws Exception {
        Mockito.when(service.buscarPorNome(anyString()))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/nome").param("nome", "Desconhecido"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado: Usuário não encontrado"));
    }

    // ✅ TESTE GET - Buscar por email
    @Test
    @DisplayName("Deve buscar usuário por email com sucesso (200)")
    void deveBuscarPorEmailComSucesso() throws Exception {
        Mockito.when(service.buscarPorEmail(anyString())).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/email")
                        .param("email", "danielle@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("danielle@example.com"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar usuário inexistente por email")
    void deveRetornar404AoBuscarUsuarioPorEmailInexistente() throws Exception {
        Mockito.when(service.buscarPorEmail(anyString()))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/email").param("email", "inexistente@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado: Usuário não encontrado"));
    }
}
