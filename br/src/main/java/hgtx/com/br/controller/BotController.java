package hgtx.com.br.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import hgtx.com.br.model.Bot;
import hgtx.com.br.model.Mensagem;
import hgtx.com.br.model.Usuario;
import hgtx.com.br.repository.BotRepository;
import hgtx.com.br.repository.MensagemRepository;
import hgtx.com.br.repository.ProjetoRepository;
import hgtx.com.br.repository.UsuarioRepository;

@Controller
public class BotController {

    @Autowired
    BotRepository botRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ProjetoRepository projetoRepository;

    @Autowired
    MensagemRepository mensagemRepository;

    public Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @PostMapping("/CreateBot")
    public String createBot() {
        Bot bot = new Bot();
        bot.setNome("Bot " + (botRepository.contbotsbyUserEmail(getUsuarioLogado().getEmail()) + 1));
        bot.setUsuario(getUsuarioLogado());
        botRepository.save(bot);
        return "redirect:/bots/" + bot.getId();
    }

    @PostMapping("/bots/{id}/messages")
    public String newMensage(@PathVariable Long id,
            @RequestParam String mensage, Model model, RedirectAttributes redirectAttributes) {

        if (mensage.equals("")) {
            redirectAttributes.addFlashAttribute("error", "Mensagem não pode ser vazia");
            return "redirect:/bots/" + id;
        }

        Bot bot = botRepository.findById(id);

        if (!bot.getUsuario().getEmail().equals(getUsuarioLogado().getEmail())) {
            throw new RuntimeException("Acesso negado");
        }

        Mensagem mensagem = new Mensagem();
        mensagem.setConteudo(mensage);
        mensagem.setBot(bot);
        mensagem.setRemetente(getUsuarioLogado().getNome());
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagemRepository.saveMensagem(mensagem);

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://hmg-hgtx-n8n.hgtx.com.br/webhook/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of(
                "botId", bot.getId(),
                "botNome", bot.getNome(),
                "usuarioEmail", bot.getUsuario().getEmail(),
                "mensagem", mensage,
                "remetente", "USER");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/bots/" + id;
    }

    @PostMapping("/bots/{id}/rename")
    @ResponseBody
    public void renameBot(@PathVariable Long id, @RequestBody Map<String, String> body) {

        String novoNome = body.get("nome");

        Bot bot = botRepository.findById(id);

        bot.setNome(novoNome);
        botRepository.save(bot);
    }

    @PostMapping("/bots/{id}/delete")
    public String deleteBot(@PathVariable Long id) {

        Bot bot = botRepository.findById(id);

        botRepository.delete(bot);

        return "redirect:/bots";
    }

    @PostMapping("/bots/{id}/changeProject")
    public String changeProject(@PathVariable Long id, @RequestParam Long idProjeto) {

        Bot bot = botRepository.findById(id);
        bot.setProjeto(projetoRepository.findById(idProjeto));

        botRepository.update(bot);

        return "redirect:/bots/" + id;

    }

    @PostMapping("/BotNewMessage/{idbot}")
    @ResponseBody
    public Map<String, Object> newMessage(
            @PathVariable Long idbot,
            @RequestParam String message) {

        Bot bot = botRepository.findById(idbot);

        Mensagem mensagem = new Mensagem();
        mensagem.setConteudo(message);
        mensagem.setBot(bot);
        mensagem.setRemetente("BOT");
        mensagem.setDataEnvio(LocalDateTime.now());

        mensagemRepository.saveMensagem(mensagem);

        return Map.of(
                "status", "success",
                "message", "Mensagem salva com sucesso");
    }
}