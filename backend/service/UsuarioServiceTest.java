package com.ecommerce.service;

import com.ecommerce.exception.RegraNegocioException;
import com.ecommerce.exception.RecursoNaoEncontradoException;
import com.ecommerce.model.Usuario;
import com.ecommerce.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TESTES UNITÁRIOS – UsuarioService (Fluxos completos e exceções)")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");
        usuario.setEmail("joao@email.com");
        usuario.setTelefone("123456");
    }

    @Test
    @DisplayName("✅ Deve salvar usuário com sucesso")
    void deveSalvarUsuarioComSucesso() {
        when(repository.findByEmailIgnoreCase(usuario.getEmail())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(usuario);

        Usuario salvo = service.salvar(usuario);

        assertEquals("João", salvo.getNome());
        verify(repository).save(usuario);
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção quando e-mail já existe")
    void deveLancarExcecaoEmailDuplicado() {
        when(repository.findByEmailIgnoreCase(usuario.getEmail())).thenReturn(Optional.of(usuario));
        assertThrows(RegraNegocioException.class, () -> service.salvar(usuario));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("📞 Deve definir status INCOMPLETO se telefone for nulo")
    void deveDefinirStatusIncompleto() {
        usuario.setTelefone(null);
        when(repository.findByEmailIgnoreCase(usuario.getEmail())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(usuario);

        Usuario salvo = service.salvar(usuario);

        assertEquals("INCOMPLETO", salvo.getStatusCadastro());
    }

    @Test
    @DisplayName("🔍 Deve listar todos os usuários")
    void deveListarTodosUsuarios() {
        when(repository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> lista = service.listarTodos();

        assertEquals(1, lista.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("🔍 Deve buscar usuário por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        Usuario result = service.buscarPorId(1L);
        assertEquals("João", result.getNome());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao buscar usuário inexistente")
    void deveLancarExcecaoBuscarInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(1L));
    }

    @Test
    @DisplayName("📝 Deve atualizar usuário com sucesso")
    void deveAtualizarComSucesso() {
        Usuario novo = new Usuario();
        novo.setNome("Maria");
        novo.setEmail("maria@email.com");
        novo.setTelefone("98765");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.findByEmailIgnoreCase(novo.getEmail())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(novo);

        Usuario atualizado = service.atualizar(1L, novo);

        assertEquals("Maria", atualizado.getNome());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao atualizar com e-mail duplicado")
    void deveLancarExcecaoAtualizarEmailDuplicado() {
        Usuario novo = new Usuario();
        novo.setEmail("outro@email.com");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.findByEmailIgnoreCase("outro@email.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(RegraNegocioException.class, () -> service.atualizar(1L, novo));
    }

    @Test
    @DisplayName("🗑️ Deve deletar usuário existente")
    void deveDeletarComSucesso() {
        when(repository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> service.deletar(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao deletar inexistente")
    void deveLancarExcecaoDeletarInexistente() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(RecursoNaoEncontradoException.class, () -> service.deletar(1L));
    }

    @Test
    @DisplayName("🔎 Deve buscar por nome")
    void deveBuscarPorNome() {
        when(repository.findByNomeIgnoreCase("João")).thenReturn(Optional.of(usuario));
        Usuario result = service.buscarPorNome("João");
        assertEquals("João", result.getNome());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao buscar nome inexistente")
    void deveLancarExcecaoBuscarPorNomeInexistente() {
        when(repository.findByNomeIgnoreCase("Pedro")).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorNome("Pedro"));
    }

    @Test
    @DisplayName("🔎 Deve buscar por e-mail")
    void deveBuscarPorEmail() {
        when(repository.findByEmailIgnoreCase("joao@email.com")).thenReturn(Optional.of(usuario));
        Usuario result = service.buscarPorEmail("joao@email.com");
        assertEquals("João", result.getNome());
    }

    @Test
    @DisplayName("🚫 Deve lançar exceção ao buscar e-mail inexistente")
    void deveLancarExcecaoBuscarEmailInexistente() {
        when(repository.findByEmailIgnoreCase("naoexiste@email.com")).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorEmail("naoexiste@email.com"));
    }
}
