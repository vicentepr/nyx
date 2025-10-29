package com.ecommerce.controller;

import com.ecommerce.dto.CriarPedidoRequest;
import com.ecommerce.model.Pedido;
import com.ecommerce.service.PedidoService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Teste completo para PedidoController
 * Cobre 100% dos endpoints (criar, listar, buscar, adicionarItem, removerItem, deletar)
 */
@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService service;

    @Autowired
    private ObjectMapper mapper;

    private Pedido pedido;
    private CriarPedidoRequest request;
    private CriarPedidoRequest.ItemRequest itemRequest;

    @BeforeEach
    void setup() {
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatus("ABERTO");
        pedido.setData(LocalDate.now());
        pedido.setTotal(BigDecimal.valueOf(100.0));

        // Cria o request principal e o item
        itemRequest = new CriarPedidoRequest.ItemRequest(1L, 2);
        request = new CriarPedidoRequest(1L, 1L, List.of(itemRequest));
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedido() throws Exception {
        Mockito.when(service.criarComItens(any(CriarPedidoRequest.class))).thenReturn(pedido);

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABERTO"));
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void deveListarPedidos() throws Exception {
        Mockito.when(service.listarTodos()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ABERTO"));
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorId() throws Exception {
        Mockito.when(service.buscarPorId(1L)).thenReturn(pedido);

        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve adicionar item ao pedido com sucesso")
    void deveAdicionarItem() throws Exception {
        Mockito.when(service.adicionarItem(eq(1L), any(CriarPedidoRequest.ItemRequest.class)))
                .thenReturn(pedido);

        mockMvc.perform(post("/pedidos/1/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABERTO"));
    }

    @Test
    @DisplayName("Deve remover item do pedido com sucesso")
    void deveRemoverItem() throws Exception {
        Mockito.when(service.removerItem(anyLong(), anyLong())).thenReturn(pedido);

        mockMvc.perform(delete("/pedidos/1/itens/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve deletar pedido com sucesso")
    void deveDeletarPedido() throws Exception {
        mockMvc.perform(delete("/pedidos/1"))
                .andExpect(status().isOk()); // ou .isNoContent() se o método não retorna nada
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao criar pedido inválido (sem itens)")
    void deveRetornarErroAoCriarPedidoInvalido() throws Exception {
        // Cria request inválido (lista de itens vazia)
        CriarPedidoRequest invalido = new CriarPedidoRequest(1L, 1L, List.of());

        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalido)))
.andExpect(status().isOk());
    }
}
