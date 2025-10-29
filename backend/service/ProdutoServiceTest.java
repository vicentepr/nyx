package com.ecommerce.service;

import com.ecommerce.exception.RecursoNaoEncontradoException;
import com.ecommerce.model.Produto;
import com.ecommerce.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    private Produto produto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setDescricao("Dell Inspiron");
        produto.setPreco(BigDecimal.valueOf(3000));
        produto.setEstoque(5);
        produto.setImagemUrl("https://example.com/notebook.png");
    }

    @Test
    @DisplayName("Deve salvar produto com sucesso")
    void deveSalvarProduto() {
        when(repository.save(any(Produto.class))).thenReturn(produto);

        Produto salvo = service.salvar(produto);

        assertNotNull(salvo);
        assertEquals("Notebook", salvo.getNome());
        verify(repository).save(produto);
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProduto() {
        Produto atualizado = new Produto();
        atualizado.setNome("Notebook Atualizado");
        atualizado.setDescricao("Dell XPS");
        atualizado.setPreco(BigDecimal.valueOf(4500));
        atualizado.setEstoque(10);
        atualizado.setImagemUrl("https://example.com/xps.png");

        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.save(any(Produto.class))).thenReturn(atualizado);

        Produto result = service.atualizar(1L, atualizado);

        assertEquals("Notebook Atualizado", result.getNome());
        assertEquals(10, result.getEstoque());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar produto inexistente")
    void deveLancarErroAoAtualizarInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Produto atualizado = new Produto();
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(99L, atualizado));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosProdutos() {
        when(repository.findAll()).thenReturn(List.of(produto));

        List<Produto> lista = service.listarTodos();

        assertEquals(1, lista.size());
        assertEquals("Notebook", lista.get(0).getNome());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(produto));

        Produto encontrado = service.buscarPorId(1L);

        assertNotNull(encontrado);
        assertEquals("Notebook", encontrado.getNome());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar produto inexistente")
    void deveLancarErroAoBuscarProdutoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
        verify(repository).findById(99L);
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProduto() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deletar(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao deletar produto inexistente")
    void deveLancarErroAoDeletarProdutoInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.deletar(99L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve buscar produto por nome")
    void deveBuscarProdutoPorNome() {
        when(repository.findByNomeIgnoreCaseContaining("Notebook")).thenReturn(List.of(produto));

        List<Produto> lista = service.buscarPorNome("Notebook");

        assertEquals(1, lista.size());
        assertEquals("Notebook", lista.get(0).getNome());
        verify(repository).findByNomeIgnoreCaseContaining("Notebook");
    }

    @Test
    @DisplayName("Deve buscar produtos por faixa de preço")
    void deveBuscarPorFaixaDePreco() {
        when(repository.findByPrecoBetween(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(List.of(produto));

        List<Produto> lista = service.buscarPorPreco(1000.0, 4000.0);

        assertEquals(1, lista.size());
        assertTrue(lista.get(0).getPreco().compareTo(BigDecimal.valueOf(1000)) > 0);
        verify(repository).findByPrecoBetween(any(), any());
    }

    @Test
    @DisplayName("Deve buscar produtos com estoque baixo")
    void deveBuscarProdutosComEstoqueBaixo() {
        when(repository.produtosComEstoqueBaixo(3)).thenReturn(List.of(produto));

        List<Produto> lista = service.produtosComEstoqueBaixo(3);

        assertEquals(1, lista.size());
        verify(repository).produtosComEstoqueBaixo(3);
    }

    @Test
    @DisplayName("Deve excluir produto com sucesso (alias do deletar)")
    void deveExcluirProduto() {
        when(repository.existsById(1L)).thenReturn(true);

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }
}
