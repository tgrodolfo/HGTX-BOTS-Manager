package hgtx.com.br.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import hgtx.com.br.model.Usuario;
import hgtx.com.br.repository.UsuarioRepository;

import org.springframework.web.bind.annotation.GetMapping;
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

        System.out.println("Nome: " + nome);
        System.out.println("Email: " + email);
        System.out.println("Senha: " + senha);
    Usuario usuario = new Usuario(nome, email, passwordEncoder.encode(senha), "ROLE_USER");

    usuarioRepository.save(usuario);

    return "redirect:/login";
  }

  @GetMapping("/user")
  public String perfilUsuario(Model model, Authentication authentication) {

    Usuario usuario = (Usuario) authentication.getPrincipal();
    model.addAttribute("usuario", usuario);

    return "user-profile";
  }

  @GetMapping("/user/edit")
  public String editarPerfil(Model model, Authentication authentication) {

    Usuario usuario = (Usuario) authentication.getPrincipal();
    model.addAttribute("usuario", usuario);

    return "user-edit";
  }

  @PostMapping("/user/update")
  public String atualizarUsuario(@RequestParam String nome,
      @RequestParam String email,
      @RequestParam(required = false) String senha,
      @RequestParam(required = false) String confirmarSenha,
      Authentication authentication) {

    Usuario usuario = (Usuario) authentication.getPrincipal();

    usuario.setNome(nome);
    usuario.setEmail(email);

    if (senha != null && !senha.isBlank()) {

      if (!senha.equals(confirmarSenha)) {
        System.out.println("Nome:"+nome);
        System.out.println("Email:"+email);
        System.out.println("Senha:"+senha);
        System.out.println("Confirmar Senha:"+confirmarSenha);
        return "redirect:/user/edit?erro";
      }

      usuario.setSenha(passwordEncoder.encode(senha));
    }

    usuarioRepository.update(usuario);

    return "redirect:/user";
  }
}