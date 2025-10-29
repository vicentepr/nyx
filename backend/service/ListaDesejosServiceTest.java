package com.ecommerce.service;

import com.ecommerce.dto.CriarListaDesejosRequest;
import com.ecommerce.exception.RegraNegocioException;
import com.ecommerce.exception.RecursoNaoEncontradoException;
import com.ecommerce.model.ListaDesejos;
import com.ecommerce.model.Produto;
import com.ecommerce.model.Usuario;
import com.ecommerce.repository.ListaDesejosRepository;
import com.ecommerce.repository.ProdutoRepository;
import com.ecommerce.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TESTES DE INTEGRAÇÃO – ListaDesejosService (cobertura total)")
class ListaDesejosServiceTest {

    @Mock
    private ListaDesejosRepository listaDesejosRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ListaDesejosService service;

    private Usuario usuario;
    private Produto produto;
    private ListaDesejos lista;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Maria");

        produto = new Produto();
        produto.setId(10L);
        produto.setNome("Produto X");

        lista = new ListaDesejos();
        lista.setId(1L);
        lista.setUsuario(usuario);
        lista.setNome("Lista de Maria");
        lista.setProdutos(new ArrayList<>(List.of(produto)));
    }

    @Test
    @DisplayName("✅ Deve criar lista de desejos com sucesso")
    void deveCriarListaComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(listaDesejosRepository.existsByUsuarioId(1L)).thenReturn(false);
        when(produtoRepository.findAllById(any())).thenReturn(List.of(produto));
        when(listaDesejosRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new CriarListaDesejosRequest(usuario.getId(), List.of(produto.getId()));
        ListaDesejos criada = service.criarComProdutos(req);

        assertEquals("Lista de Maria", criada.getNome());
        assertEquals(1, criada.getProdutos().size());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção se usuário não existe")
    void deveLancarExcecaoUsuarioNaoExiste() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        var req = new CriarListaDesejosRequest(1L, List.of(10L));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.criarComProdutos(req));
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção se produtos vazios")
    void deveLancarExcecaoProdutosVazios() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoRepository.findAllById(any())).thenReturn(List.of());
        when(listaDesejosRepository.existsByUsuarioId(anyLong())).thenReturn(false);

        var req = new CriarListaDesejosRequest(1L, List.of());
        assertThrows(RegraNegocioException.class, () -> service.criarComProdutos(req));
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção se usuário já tem lista")
    void deveLancarExcecaoUsuarioJaTemLista() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(listaDesejosRepository.existsByUsuarioId(1L)).thenReturn(true);

        var req = new CriarListaDesejosRequest(1L, List.of(10L));
        assertThrows(RegraNegocioException.class, () -> service.criarComProdutos(req));
    }

    @Test
    @DisplayName("📝 Deve atualizar lista com produtos novos")
    void deveAtualizarComProdutos() {
        when(listaDesejosRepository.findById(1L)).thenReturn(Optional.of(lista));
        when(produtoRepository.findAllById(any())).thenReturn(List.of(produto));
        when(listaDesejosRepository.save(any())).thenReturn(lista);

        var req = new CriarListaDesejosRequest(1L, List.of(produto.getId()));
        ListaDesejos atualizada = service.atualizarComProdutos(1L, req);

        assertNotNull(atualizada);
        verify(listaDesejosRepository).save(any());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao atualizar lista inexistente")
    void deveLancarExcecaoAtualizarListaInexistente() {
        when(listaDesejosRepository.findById(1L)).thenReturn(Optional.empty());
        var req = new CriarListaDesejosRequest(1L, List.of(produto.getId()));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizarComProdutos(1L, req));
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao atualizar com produtos vazios")
    void deveLancarExcecaoAtualizarSemProdutos() {
        when(listaDesejosRepository.findById(1L)).thenReturn(Optional.of(lista));
        when(produtoRepository.findAllById(any())).thenReturn(List.of());
        var req = new CriarListaDesejosRequest(1L, List.of());
        assertThrows(RegraNegocioException.class, () -> service.atualizarComProdutos(1L, req));
    }

    @Test
    @DisplayName("🗑️ Deve deletar lista existente")
    void deveDeletarComSucesso() {
        when(listaDesejosRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> service.deletar(1L));
        verify(listaDesejosRepository).deleteById(1L);
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao deletar inexistente")
    void deveLancarExcecaoDeletarInexistente() {
        when(listaDesejosRepository.existsById(1L)).thenReturn(false);
        assertThrows(RecursoNaoEncontradoException.class, () -> service.deletar(1L));
    }

    @Test
    @DisplayName("➕ Deve adicionar produto na lista")
    void deveAdicionarProduto() {
        when(listaDesejosRepository.findById(1L)).thenReturn(Optional.of(lista));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(listaDesejosRepository.save(any())).thenReturn(lista);

        ListaDesejos atualizada = service.adicionarProduto(1L, 10L);
        assertEquals(1, atualizada.getProdutos().size());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao adicionar produto inexistente")
    void deveLancarExcecaoAdicionarProdutoInexistente() {
        when(listaDesejosRepository.findById(1L)).thenReturn(Optional.of(lista));
        when(produtoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.adicionarProduto(1L, 10L));
    }

    @Test
    @DisplayName("➖ Deve remover produto existente da lista")
    void deveRemoverProduto() {
        when(listaDesejosRepository.findById(1L)).thenReturn(Optional.of(lista));
        when(listaDesejosRepository.save(any())).thenReturn(lista);

        ListaDesejos atualizada = service.removerProduto(1L, 10L);
        assertTrue(atualizada.getProdutos().isEmpty());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao remover produto inexistente na lista")
    void deveLancarExcecaoRemoverProdutoInexistente() {
        lista.getProdutos().clear();
        when(listaDesejosRepository.findById(1L)).thenReturn(Optional.of(lista));

        assertThrows(RecursoNaoEncontradoException.class, () -> service.removerProduto(1L, 10L));
    }

    @Test
    @DisplayName("🔎 Deve listar todas as listas")
    void deveListarTodas() {
        when(listaDesejosRepository.findAll()).thenReturn(List.of(lista));
        List<ListaDesejos> listas = service.listarTodas();
        assertEquals(1, listas.size());
    }

    @Test
    @DisplayName("🔎 Deve buscar lista por ID")
    void deveBuscarPorId() {
        when(listaDesejosRepository.findById(1L)).thenReturn(Optional.of(lista));
        ListaDesejos encontrada = service.buscarPorId(1L);
        assertNotNull(encontrada);
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção buscar lista inexistente")
    void deveLancarExcecaoBuscarInexistente() {
        when(listaDesejosRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("🔍 Deve buscar lista por usuário")
    void deveBuscarPorUsuario() {
        when(listaDesejosRepository.findByUsuarioId(1L)).thenReturn(Optional.of(lista));
        ListaDesejos encontrada = service.buscarPorUsuario(1L);
        assertNotNull(encontrada);
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao buscar lista de usuário sem lista")
    void deveLancarExcecaoBuscarPorUsuarioSemLista() {
        when(listaDesejosRepository.findByUsuarioId(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorUsuario(1L));
    }
}
