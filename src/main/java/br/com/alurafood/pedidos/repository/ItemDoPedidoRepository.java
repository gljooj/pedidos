package br.com.alurafood.pedidos.repository;

import br.com.alurafood.pedidos.model.ItemDoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDoPedidoRepository  extends JpaRepository<ItemDoPedido, Long> {
}
