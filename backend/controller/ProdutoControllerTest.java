package com.ecommerce.controller;

import com.ecommerce.model.Produto;
import com.ecommerce.service.ProdutoService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Teste completo do ProdutoController
 * Cobre criação, listagem, atualização, busca e validação
 */
@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService service;

    @Autowired
    private ObjectMapper mapper;

    private Produto produto;

    @BeforeEach
    void setup() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setDescricao("Dell Inspiron 15");
        produto.setPreco(BigDecimal.valueOf(3500.00));
        produto.setEstoque(10);
        produto.setImagemUrl("http://exemplo.com/notebook.jpg");
    }

    @Test
    @DisplayName("Deve listar produtos com sucesso")
    void deveListarProdutos() throws Exception {
        Mockito.when(service.listarTodos()).thenReturn(List.of(produto));

        mockMvc.perform(get("/produtos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Notebook"))
                .andExpect(jsonPath("$[0].descricao").value("Dell Inspiron 15"))
                .andExpect(jsonPath("$[0].preco").value(3500.00))
                .andExpect(jsonPath("$[0].estoque").value(10))
                .andExpect(jsonPath("$[0].imagemUrl").value("http://exemplo.com/notebook.jpg"));
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProduto() throws Exception {
        Mockito.when(service.salvar(any(Produto.class))).thenReturn(produto);

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(produto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Notebook"))
                .andExpect(jsonPath("$.descricao").value("Dell Inspiron 15"))
                .andExpect(jsonPath("$.preco").value(3500.00))
                .andExpect(jsonPath("$.estoque").value(10))
                .andExpect(jsonPath("$.imagemUrl").value("http://exemplo.com/notebook.jpg"));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProduto() throws Exception {
        Produto atualizado = new Produto();
        atualizado.setId(1L);
        atualizado.setNome("Notebook Atualizado");
        atualizado.setDescricao("Dell XPS 13");
        atualizado.setPreco(BigDecimal.valueOf(4500.00));
        atualizado.setEstoque(8);
        atualizado.setImagemUrl("http://exemplo.com/notebook-xps.jpg");

        Mockito.when(service.atualizar(eq(1L), any(Produto.class))).thenReturn(atualizado);

        mockMvc.perform(put("/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Notebook Atualizado"))
                .andExpect(jsonPath("$.descricao").value("Dell XPS 13"))
                .andExpect(jsonPath("$.preco").value(4500.00))
                .andExpect(jsonPath("$.estoque").value(8))
                .andExpect(jsonPath("$.imagemUrl").value("http://exemplo.com/notebook-xps.jpg"));
    }

    @Test
@DisplayName("Deve retornar 500 ao buscar produto inexistente")
void deveRetornarErroProdutoNaoEncontrado() throws Exception {
    Mockito.when(service.buscarPorId(anyLong()))
            .thenThrow(new RuntimeException("Produto não encontrado"));

    mockMvc.perform(get("/produtos/99"))
            .andExpect(status().isInternalServerError());
}


    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProduto() throws Exception {
        mockMvc.perform(delete("/produtos/1"))
                .andExpect(status().isOk()); // ou isNoContent() se o método retornar vazio
    }

    @Test
    @DisplayName("Deve buscar produtos por nome")
    void deveBuscarPorNome() throws Exception {
        Mockito.when(service.buscarPorNome("Notebook")).thenReturn(List.of(produto));

        mockMvc.perform(get("/produtos").param("nome", "Notebook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Notebook"));
    }

    @Test
    @DisplayName("Deve buscar produtos por preço entre faixas")
    void deveBuscarPorFaixaDePreco() throws Exception {
        Mockito.when(service.buscarPorPreco(1000.0, 4000.0)).thenReturn(List.of(produto));

        mockMvc.perform(get("/produtos")
                        .param("precoMin", "1000")
                        .param("precoMax", "4000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Notebook"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar produto sem campos obrigatórios (validação)")
    void deveRetornarErroValidacaoAoCriarProduto() throws Exception {
        Produto invalido = new Produto();
        invalido.setId(2L); // faltam campos obrigatórios

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }
}
