package hgtx.com.br.controller;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import hgtx.com.br.model.Projeto;
import hgtx.com.br.model.Usuario;
import hgtx.com.br.repository.ProjetoRepository;
import hgtx.com.br.repository.UsuarioRepository;

@Controller
public class ProjetoController {

  @Autowired
  public UsuarioRepository usuarioRepository;

  @Autowired
  public ProjetoRepository projetoRepository;

  public Usuario getUsuarioLogado() {

    Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal().equals("anonymousUser")) {

      return null;
    }

    String email = authentication.getName();

    return usuarioRepository.findByEmail(email)
        .orElse(null);
  }

  @PostMapping("/projeto/newproject")
  public String newProject() {

    Projeto projeto = new Projeto();
    projeto.setNome("Novo Projeto");

    Usuario usuario = getUsuarioLogado();

    projeto.setUsuarios(new ArrayList<>());
    projeto.getUsuarios().add(usuario);

    usuario.getProjetos().add(projeto); // mantém os dois lados sincronizados

    projetoRepository.save(projeto);

    return "redirect:/bots";
  }

  @PostMapping("/projeto/{id}/delete")
  public String deleteProject(@PathVariable Long id) {
    projetoRepository.removerCompartilhamento(id, getUsuarioLogado().getId());
    return "redirect:/bots";
  }

  @PostMapping("/projeto/{id}/rename")
  @ResponseBody
  public String renameProject(@PathVariable Long id, @RequestBody Map<String, String> body) {
    Projeto projeto = projetoRepository.findById(id);
    if (projeto != null) {
      String nome = body.get("nome");
      projeto.setNome(nome);
      projetoRepository.update(projeto);
    }
    return "redirect:/bots";
  }
}
