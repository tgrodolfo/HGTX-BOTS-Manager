package hgtx.com.br.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import hgtx.com.br.model.Usuario;
import hgtx.com.br.repository.BotRepository;
import hgtx.com.br.repository.MensagemRepository;
import hgtx.com.br.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class RotesController {

  @Autowired
  UsuarioRepository usuarioRepository;

  @Autowired
  BotRepository botRepository;

  @Autowired
  MensagemRepository mensagemRepository;

  public Usuario getUsuarioLogado() {

    Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();

    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getPrincipal().equals("anonymousUser")) {

      return null; // ou lançar exceção personalizada
    }

    String email = authentication.getName();

    return usuarioRepository.findByEmail(email)
        .orElse(null); // evita quebrar
  }

  @GetMapping("/")
  public String indexString() {
    return "index";
  }

  @GetMapping("/bots")
  public String bots(Model model) {
    model.addAttribute("user", getUsuarioLogado());
    model.addAttribute("bots", botRepository.findAllByUserEmail(getUsuarioLogado().getEmail()));
    return "bots";
  }

  @GetMapping("/login")
  public String login() {
    if (getUsuarioLogado() != null) {
      return "redirect:/bots";
    }
    return "signin";
  }

  @GetMapping("/login/signup")
  public String signup() {
    if (getUsuarioLogado() != null) {
      return "redirect:/bots";
    }
    return "signup";
  }

  @GetMapping("/bots/{botid}")
  public String bot(Model model, @PathVariable Long botid) {
    model.addAttribute("bot", botRepository.findById(botid));
    model.addAttribute("mensagens", mensagemRepository.findAllByBotId(botid));
    model.addAttribute("user", getUsuarioLogado());
    model.addAttribute("bots", botRepository.findAllByUserEmail(getUsuarioLogado().getEmail()));
    return "bots";
  }
}
