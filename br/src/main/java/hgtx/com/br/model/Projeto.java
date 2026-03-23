package hgtx.com.br.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projeto")
public class Projeto {



  @Id
  private Long id;

  @PrePersist
  public void gerarId() {
    if (id == null) {
      UUID uuid = UUID.randomUUID();
      id = uuid.getMostSignificantBits() & Long.MAX_VALUE; // evita negativo
    }
  }

  @Column(nullable = false)
  private String nome;

  private String descricao;

  // 🔥 Agora é ManyToMany
  @ManyToMany
  @JoinTable(name = "projeto_usuario", joinColumns = @JoinColumn(name = "projeto_id"), inverseJoinColumns = @JoinColumn(name = "usuario_id"))
  private List<Usuario> usuarios;

  @OneToMany(mappedBy = "projeto")
  private List<Bot> bots;

  public Projeto() {
  }

  public Long getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public List<Usuario> getUsuarios() {
    return usuarios;
  }

  public void setUsuarios(List<Usuario> usuarios) {
    this.usuarios = usuarios;
  }

  public List<Bot> getBots() {
    return bots;
  }

  public void setBots(List<Bot> bots) {
    this.bots = bots;
  }
}