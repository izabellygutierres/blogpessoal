package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	  @Autowired
	    private TestRestTemplate testRestTemplate;

	    @Autowired
	    private UsuarioService usuarioService;

	    @Autowired
	    private UsuarioRepository usuarioRepository;

	    @BeforeAll
	    void start() {
	    	
	        usuarioRepository.deleteAll();

	        usuarioService.cadastrarUsuario(new Usuario(0L,
	                "Root", "root@root.com", "rootroot", " "));
	        
	    }
	    
	    @Test
	    @DisplayName("Cadastrar Um Usuário")
	    public void deveCriarUmUsuario() {

	        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
	                "Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "-"));

	        ResponseEntity<Usuario> corpoResposta = testRestTemplate
	                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

	        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	        
	    }
    
	    @Test
	    @DisplayName("Não deve permitir duplicação do Usuário")
	    public void naoDeveDuplicarUsuario() {
	        // Cria um novo usuário e o cadastra no sistema
	        usuarioService.cadastrarUsuario(new Usuario(0L,
	                "Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));
	        // Cria uma requisição HTTP para tentar cadastrar o mesmo usuário novamente
	        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<>(new Usuario(0L,
	                "Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));
	        ResponseEntity<Usuario> corpoResposta = testRestTemplate
	                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

	        // Verifica se a resposta HTTP indica um erro de requisição ruim (BAD_REQUEST)
	        assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	    }
	    
	    @Test
	    @DisplayName("Atualizar um Usuário")
	    public void deveAtualizarUmUsuario() {
	        // Cadastra um usuário
	        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
	                "Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));

	        // Cria um objeto para atualizar o usuário com novos dados
	        Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
	                "Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123", "-");

	        // Prepara a requisição HTTP para atualizar o usuário
	        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<>(usuarioUpdate);
	        ResponseEntity<Usuario> corpoResposta = testRestTemplate
	                .withBasicAuth("root@root.com", "rootroot")
	                .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
	        // Verifica se a resposta da atualização foi bem-sucedida (status 200)
	        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	        
	    }
	    
	    @Test
	    @DisplayName("Listar todos os Usuários")
	    public void deveMostrarTodosUsuarios() {
	        // Cadastra dois usuários como pré-condição para o teste
	        usuarioService.cadastrarUsuario(new Usuario(0L,
	                "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
	        usuarioService.cadastrarUsuario(new Usuario(0L,
	                "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));

	        // Realiza uma requisição HTTP GET para listar todos os usuários
	        ResponseEntity<String> resposta = testRestTemplate
	                .withBasicAuth("root@root.com", "rootroot")
	                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);

	        // Verifica se a resposta da requisição foi bem-sucedida (status 200)
	        assertEquals(HttpStatus.OK, resposta.getStatusCode());
	    }  
}
