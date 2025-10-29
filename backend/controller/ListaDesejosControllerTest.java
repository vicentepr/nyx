package com.ecommerce.controller;

import com.ecommerce.dto.CriarListaDesejosRequest;
import com.ecommerce.model.ListaDesejos;
import com.ecommerce.service.ListaDesejosService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Teste completo do ListaDesejosController compatível com record DTO
 */
@WebMvcTest(ListaDesejosController.class)
class ListaDesejosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListaDesejosService service;

    @Autowired
    private ObjectMapper mapper;

    private ListaDesejos listaDesejos;
    private CriarListaDesejosRequest request;

    @BeforeEach
    void setup() {
        listaDesejos = new ListaDesejos();
        listaDesejos.setId(1L);
        listaDesejos.setNome("Lista de Natal");

        // ✅ usa o construtor do record (usuarioId, produtoIds)
        request = new CriarListaDesejosRequest(1L, List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("Deve criar lista de desejos com sucesso")
    void deveCriarListaDesejos() throws Exception {
        Mockito.when(service.criarComProdutos(any(CriarListaDesejosRequest.class)))
                .thenReturn(listaDesejos);

        mockMvc.perform(post("/lista-desejos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lista de Natal"));
    }

    @Test
    @DisplayName("Deve atualizar lista de desejos com sucesso")
    void deveAtualizarListaDesejos() throws Exception {
        ListaDesejos atualizada = new ListaDesejos();
        atualizada.setId(1L);
        atualizada.setNome("Lista Atualizada");

        Mockito.when(service.atualizarComProdutos(eq(1L), any(CriarListaDesejosRequest.class)))
                .thenReturn(atualizada);

        mockMvc.perform(put("/lista-desejos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lista Atualizada"));
    }

    @Test
    @DisplayName("Deve listar todas as listas de desejos")
    void deveListarTodasListasDesejos() throws Exception {
        Mockito.when(service.listarTodas()).thenReturn(List.of(listaDesejos));

        mockMvc.perform(get("/lista-desejos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Lista de Natal"));
    }

    @Test
    @DisplayName("Deve buscar lista de desejos por ID")
    void deveBuscarListaPorId() throws Exception {
        Mockito.when(service.buscarPorId(1L)).thenReturn(listaDesejos);

        mockMvc.perform(get("/lista-desejos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lista de Natal"));
    }

    @Test
    @DisplayName("Deve buscar lista de desejos por usuário")
    void deveBuscarPorUsuario() throws Exception {
        Mockito.when(service.buscarPorUsuario(1L)).thenReturn(listaDesejos);

        mockMvc.perform(get("/lista-desejos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lista de Natal"));
    }

    @Test
    @DisplayName("Deve deletar lista de desejos com sucesso")
    void deveDeletarListaDesejos() throws Exception {
        mockMvc.perform(delete("/lista-desejos/1"))
                .andExpect(status().isOk()); // ou .isNoContent() se retornar vazio
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao criar lista com dados inválidos")
    void deveRetornarErroAoCriarListaInvalida() throws Exception {
        // ✅ cria um record com valores inválidos (ex: usuarioId nulo)
        CriarListaDesejosRequest invalido = new CriarListaDesejosRequest(null, List.of());

        mockMvc.perform(post("/lista-desejos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
}
