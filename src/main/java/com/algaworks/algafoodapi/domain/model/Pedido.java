package com.algaworks.algafoodapi.domain.model;

import com.algaworks.algafoodapi.domain.event.PedidoCanceladoEvent;
import com.algaworks.algafoodapi.domain.event.PedidoConfirmadoEvent;
import com.algaworks.algafoodapi.domain.exception.NegocioException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.AbstractAggregateRoot;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class    Pedido extends AbstractAggregateRoot<Pedido> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.CRIADO;

    @Embedded
    private Endereco enderecoEntrega;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(name = "taxa_frete", nullable = false)
    private BigDecimal taxaFrete;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false)
    private OffsetDateTime dataCriacao;

    @Column(name = "data_confirmacao")
    private OffsetDateTime dataConfirmacao;

    @Column(name = "data_cancelamento")
    private OffsetDateTime dataCancelamento;

    @Column(name = "data_entrega")
    private OffsetDateTime dataEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pagamento_id", nullable = false)
    private FormaPagamento formaPagamento;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @ManyToOne
    @JoinColumn(name = "usuario_cliente_id", nullable = false)
    private Usuario cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Set<ItemPedido> itens = new HashSet<>();

    public void calcularValorTotal() {
        getItens().forEach(ItemPedido::calcularPrecoTotal);

        this.subtotal = getItens().stream()
                .map(item -> item.getPrecoTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorTotal = this.subtotal.add(this.taxaFrete);
    }

    public void confirmar() {
        setStatus(StatusPedido.CONFIRMADO);
        setDataConfirmacao(OffsetDateTime.now());
        
        registerEvent(new PedidoConfirmadoEvent(this));
    }

    public void entregar() {
        setStatus(StatusPedido.ENTREGUE);
        setDataEntrega(OffsetDateTime.now());
    }

    public void cancelar() {
        setStatus(StatusPedido.CANCELADO);
        setDataCancelamento(OffsetDateTime.now());
        
        registerEvent(new PedidoCanceladoEvent(this));
    }

    public void definirFrete() {
        setTaxaFrete(getRestaurante().getTaxaFrete());
    }

    public void atribuirPedidoAosItens() {
        getItens().forEach(item -> item.setPedido(this));
    }
    
    public boolean podeSerConfirmado() {
    	return getStatus().podeAlterarPara(StatusPedido.CONFIRMADO);
    }

    public boolean podeSerEntregue() {
    	return getStatus().podeAlterarPara(StatusPedido.ENTREGUE);
    }
    
    public boolean podeSerCancelado() {
    	return getStatus().podeAlterarPara(StatusPedido.CANCELADO);
    }    
    
    private void setStatus(StatusPedido status) {
       if (getStatus().naoPodeAlterarPara(status)) {
           throw new NegocioException(
                   String.format("Status do pedido %s não pode ser alterado de %s para %s",
                           getCodigo(), getStatus().getDescricao(), status.getDescricao()));
       }

       this.status = status;
    }

    @PrePersist
    private void gerarCodigo() {
        setCodigo(UUID.randomUUID().toString());
    }
}
