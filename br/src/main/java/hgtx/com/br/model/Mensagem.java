package hgtx.com.br.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
public class Mensagem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "usuario_id", nullable = true) // null = mensagem do BOT
  private Usuario usuario;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String conteudo;

  private LocalDateTime dataEnvio = LocalDateTime.now();

  @ManyToOne
  @JoinColumn(name = "bot_id", nullable = false)
  private Bot bot;

  @Column(nullable = false)
  private String remetente;

  public Mensagem() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getConteudo() {
    return conteudo;
  }

  public void setConteudo(String conteudo) {
    this.conteudo = conteudo;
  }

  public LocalDateTime getDataEnvio() {
    return dataEnvio;
  }

  public void setDataEnvio(LocalDateTime dataEnvio) {
    this.dataEnvio = dataEnvio;
  }

  public Bot getBot() {
    return bot;
  }

  public void setBot(Bot bot) {
    this.bot = bot;
  }

  public String getRemetente() {
    return remetente;
  }

  public void setRemetente(String remetente) {
    this.remetente = remetente;
  }
}