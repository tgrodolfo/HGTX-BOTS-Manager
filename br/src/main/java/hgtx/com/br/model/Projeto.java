package hgtx.com.br.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "projeto")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "projeto")
    private List<Bot> bots;

    public Projeto() {}

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

    public String getDescricao() {
      return descricao;
    }

    public void setDescricao(String descricao) {
      this.descricao = descricao;
    }

    public Usuario getUsuario() {
      return usuario;
    }

    public void setUsuario(Usuario usuario) {
      this.usuario = usuario;
    }

    public List<Bot> getBots() {
      return bots;
    }

    public void setBots(List<Bot> bots) {
      this.bots = bots;
    }

    
}