package com.ecommerce.service;

import com.ecommerce.exception.RecursoNaoEncontradoException;
import com.ecommerce.model.Endereco;
import com.ecommerce.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnderecoServiceTest {

    @Mock
    private EnderecoRepository repository;

    @InjectMocks
    private EnderecoService service;

    private Endereco endereco;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setLogradouro("Rua A");
        endereco.setNumero("100");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01000-000");
    }

    @Test
    @DisplayName("Deve salvar endereço com sucesso")
    void deveSalvarEndereco() {
        when(repository.save(any(Endereco.class))).thenReturn(endereco);

        Endereco salvo = service.salvar(endereco);

        assertNotNull(salvo);
        assertEquals("São Paulo", salvo.getCidade());
        verify(repository).save(endereco);
    }

    @Test
    @DisplayName("Deve listar todos os endereços")
    void deveListarTodosEnderecos() {
        when(repository.findAll()).thenReturn(List.of(endereco));

        List<Endereco> lista = service.listarTodos();

        assertEquals(1, lista.size());
        assertEquals("SP", lista.get(0).getEstado());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve buscar endereço por ID com sucesso")
    void deveBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(endereco));

        Endereco encontrado = service.buscarPorId(1L);

        assertNotNull(encontrado);
        assertEquals("Rua A", encontrado.getLogradouro());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar endereço inexistente")
    void deveLancarErroAoBuscarInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
        verify(repository).findById(99L);
    }

    @Test
    @DisplayName("Deve atualizar endereço com sucesso")
    void deveAtualizarEndereco() {
        Endereco atualizado = new Endereco();
        atualizado.setLogradouro("Rua B");
        atualizado.setNumero("200");
        atualizado.setBairro("Bairro Novo");
        atualizado.setCidade("Campinas");
        atualizado.setEstado("SP");
        atualizado.setCep("13000-000");

        when(repository.findById(1L)).thenReturn(Optional.of(endereco));
        when(repository.save(any(Endereco.class))).thenReturn(atualizado);

        Endereco result = service.atualizar(1L, atualizado);

        assertEquals("Rua B", result.getLogradouro());
        assertEquals("Campinas", result.getCidade());
        verify(repository).save(any(Endereco.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar endereço inexistente")
    void deveLancarErroAoAtualizarInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Endereco atualizado = new Endereco();
        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(99L, atualizado));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve excluir endereço com sucesso")
    void deveExcluirEndereco() {
        when(repository.existsById(1L)).thenReturn(true);

        service.excluir(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar erro ao excluir endereço inexistente")
    void deveLancarErroAoExcluirInexistente() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.excluir(99L));
        verify(repository, never()).deleteById(99L);
    }

    @Test
    @DisplayName("Deve buscar endereços por cidade")
    void deveBuscarPorCidade() {
        when(repository.findByCidadeIgnoreCase("São Paulo")).thenReturn(List.of(endereco));

        List<Endereco> lista = service.buscarPorCidade("São Paulo");

        assertEquals(1, lista.size());
        assertEquals("São Paulo", lista.get(0).getCidade());
        verify(repository).findByCidadeIgnoreCase("São Paulo");
    }

    @Test
    @DisplayName("Deve buscar endereços por estado")
    void deveBuscarPorEstado() {
        when(repository.findByEstadoIgnoreCase("SP")).thenReturn(List.of(endereco));

        List<Endereco> lista = service.buscarPorEstado("SP");

        assertEquals(1, lista.size());
        assertEquals("SP", lista.get(0).getEstado());
        verify(repository).findByEstadoIgnoreCase("SP");
    }
}
