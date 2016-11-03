package com.algaworks.socialbooks.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.algaworks.socialbooks.domain.Livro;
import com.algaworks.socialbooks.repository.LivrosRepository;
import com.algaworks.socialbooks.services.LivrosService;
import com.algaworks.socialbooks.services.exceptions.LivroNaoEncontradoException;

//diz que essa classe é um controler (MVC) para o SprintMVC
@RestController
@RequestMapping("/livros")//define a URI a ser utilizado nos métodos da classe
public class LivrosResources {
	
	@Autowired
	private LivrosService livrosServices;
	
	// diz qual a URI que será usada para acessar o método, por meio do método GET do HTTP
	/*@RequestMapping(value = "/livros", method = RequestMethod.GET)
	public List<Livro> listar(){
		
		Exemplo de retorno de uma lista de objetos
		
		Livro l1 = new Livro("Rest Aplicado");
		Livro l2 = new Livro("Git passo-a-passo");
		
		Livro[] livros = {l1,l2};
		
		return Arrays.asList(livros);
	}*/
	
	//Método que busca dados do banco de dados
	//@RequestMapping(value = "/livros", method = RequestMethod.GET)
	@RequestMapping(method = RequestMethod.GET)//Quando é usado a anotation @RequestMapping não é necessário passar o value
	public ResponseEntity<List<Livro>>listar(){
		return ResponseEntity.status(HttpStatus.OK).body(livrosServices.lista());
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> salvar(@RequestBody Livro livro){//Pega o que está vindo no Post e coloque no parâmetro Livro
		//livrosRepository.save(livro);//o método save é do Spring
		
		livro = livrosServices.salvar(livro);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(livro.getId()).toUri();//Cria a URI para acessar a informação inserida
		
		return ResponseEntity.created(uri).build();//Cria a resposta correta usando a URI criada
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)//Vai realizar um GET e na URI livros/id o Spring pega o id e realiza a consulta
	public ResponseEntity<?> buscar(@PathVariable("id") Long id){
		
		/*
		 * return sem tratamento de no_data_found
		 * return livrosRepository.findOne(id);
		 */
		
		Livro livro = null;
		
		try{
			livro = livrosServices.buscar(id);
		}catch (LivroNaoEncontradoException e){
			return ResponseEntity.notFound().build();
	    }
		
		return ResponseEntity.status(HttpStatus.OK).body(livro);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deletar(@PathVariable("id") Long id){
		
		try{
			livrosServices.deletar(id);
		}catch(LivroNaoEncontradoException e){
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> atualizar(@RequestBody Livro livro, @PathVariable("id") Long id){
		
		livro.setId(id);
		
		try {
			livrosServices.atualizar(livro);
		} catch (LivroNaoEncontradoException e) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.noContent().build();
	}
}
