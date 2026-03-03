package hgtx.com.br.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import hgtx.com.br.model.Usuario;
import hgtx.com.br.repository.UsuarioRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("/saveUser")
  public String createUser(@RequestParam String nome,
      @RequestParam String email,
      @RequestParam String senha) {

    Usuario usuario = new Usuario(nome, email, passwordEncoder.encode(senha), "ROLE_USER");

    usuarioRepository.save(usuario);

    return "redirect:/login";
  }
}