package hgtx.com.br.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "bot")
public class Bot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nome;

  @ManyToOne
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne
  @JoinColumn(name = "projeto_id")
  private Projeto projeto;

  @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL)
  private List<Mensagem> mensagens;

  public Bot() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Projeto getProjeto() {
    return projeto;
  }

  public void setProjeto(Projeto projeto) {
    this.projeto = projeto;
  }

  public List<Mensagem> getMensagens() {
    return mensagens;
  }

  public void setMensagens(List<Mensagem> mensagens) {
    this.mensagens = mensagens;
  }

  public Bot orElseThrow(Object object) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
  }

}