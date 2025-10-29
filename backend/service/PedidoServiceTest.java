package com.ecommerce.service;

import com.ecommerce.dto.CriarPedidoRequest;
import com.ecommerce.exception.RegraNegocioException;
import com.ecommerce.exception.RecursoNaoEncontradoException;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @InjectMocks
    private PedidoService service;

    private Usuario usuario;
    private Produto produto;
    private Endereco endereco;
    private Pedido pedido;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Danielle");

        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCidade("São Paulo");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setPreco(BigDecimal.valueOf(3000));
        produto.setEstoque(10);

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuario);
        pedido.setData(LocalDate.now());
        pedido.setStatus("ABERTO");
        pedido.setTotal(BigDecimal.ZERO);
    }

    // --------------------------
    // CRIAÇÃO DE PEDIDO
    // --------------------------

    @Test
    @DisplayName("Deve criar pedido com itens com sucesso")
    void deveCriarPedidoComItens() {
        CriarPedidoRequest.ItemRequest item = new CriarPedidoRequest.ItemRequest(1L, 2);
        CriarPedidoRequest req = new CriarPedidoRequest(1L, 1L, List.of(item));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido result = service.criarComItens(req);

        assertNotNull(result);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao criar pedido sem itens")
    void deveLancarErroAoCriarPedidoSemItens() {
        CriarPedidoRequest req = new CriarPedidoRequest(1L, 1L, List.of());

        // ✅ Mock usuário e endereço para passar das validações iniciais
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));

        assertThrows(RegraNegocioException.class, () -> service.criarComItens(req));
    }

    @Test
    @DisplayName("Deve lançar erro se usuário não for encontrado")
    void deveLancarErroSeUsuarioNaoEncontrado() {
        CriarPedidoRequest req = new CriarPedidoRequest(1L, 1L, List.of());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.criarComItens(req));
    }

    @Test
    @DisplayName("Deve lançar erro se estoque for insuficiente")
    void deveLancarErroSeEstoqueInsuficiente() {
        CriarPedidoRequest.ItemRequest item = new CriarPedidoRequest.ItemRequest(1L, 20);
        CriarPedidoRequest req = new CriarPedidoRequest(1L, 1L, List.of(item));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        produto.setEstoque(5);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(RegraNegocioException.class, () -> service.criarComItens(req));
    }

    // --------------------------
    // ADICIONAR ITEM
    // --------------------------

    @Test
    @DisplayName("Deve adicionar item ao pedido aberto com sucesso")
    void deveAdicionarItemAoPedido() {
        CriarPedidoRequest.ItemRequest itemReq = new CriarPedidoRequest.ItemRequest(1L, 2);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido atualizado = service.adicionarItem(1L, itemReq);

        assertNotNull(atualizado);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao adicionar item em pedido não aberto")
    void deveLancarErroAoAdicionarItemEmPedidoFechado() {
        pedido.setStatus("FECHADO");

        CriarPedidoRequest.ItemRequest itemReq = new CriarPedidoRequest.ItemRequest(1L, 1);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(RegraNegocioException.class, () -> service.adicionarItem(1L, itemReq));
    }

    // --------------------------
    // REMOVER ITEM
    // --------------------------

    @Test
    @DisplayName("Deve remover item do pedido")
    void deveRemoverItemDoPedido() {
        PedidoItem item = new PedidoItem();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setSubtotal(BigDecimal.valueOf(6000));

        pedido.getItens().add(item);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido result = service.removerItem(1L, 1L);

        assertNotNull(result);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao remover item inexistente")
    void deveLancarErroAoRemoverItemInexistente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(RecursoNaoEncontradoException.class, () -> service.removerItem(1L, 99L));
    }

    // --------------------------
    // ATUALIZAR E DELETAR
    // --------------------------

    @Test
    @DisplayName("Deve atualizar pedido existente")
    void deveAtualizarPedido() {
        Pedido atualizado = new Pedido();
        atualizado.setStatus("FECHADO");
        atualizado.setUsuario(usuario);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(atualizado);

        Pedido result = service.atualizar(1L, atualizado);

        assertEquals("FECHADO", result.getStatus());
    }

    @Test
    @DisplayName("Deve deletar pedido existente")
    void deveDeletarPedido() {
        pedido.getItens().add(new PedidoItem());
        pedido.getItens().get(0).setProduto(produto);
        pedido.getItens().get(0).setQuantidade(2);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        service.deletar(1L);

        verify(pedidoRepository).delete(any(Pedido.class));
    }

    // --------------------------
    // BUSCAR E LISTAR
    // --------------------------

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void deveListarTodos() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<Pedido> lista = service.listarTodos();

        assertEquals(1, lista.size());
        verify(pedidoRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar pedido por ID")
    void deveBuscarPorId() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido result = service.buscarPorId(1L);

        assertNotNull(result);
        verify(pedidoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar pedido inexistente")
    void deveLancarErroAoBuscarInexistente() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    // --------------------------
    // CRIAR PEDIDO DIRETO (SEM ITENS)
    // --------------------------

    @Test
    @DisplayName("Deve criar pedido simples com sucesso")
    void deveCriarPedidoSimples() {
        Pedido novo = new Pedido();
        novo.setUsuario(usuario);

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido salvo = service.criar(novo);

        assertNotNull(salvo);
        verify(pedidoRepository).save(novo);
    }

    @Test
    @DisplayName("Deve lançar erro ao criar pedido sem usuário")
    void deveLancarErroAoCriarSemUsuario() {
        Pedido semUsuario = new Pedido();

        assertThrows(RegraNegocioException.class, () -> service.criar(semUsuario));
    }
}
