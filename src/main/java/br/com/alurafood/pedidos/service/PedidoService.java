package br.com.alurafood.pedidos.service;

import br.com.alurafood.pedidos.dto.PedidoDto;
import br.com.alurafood.pedidos.dto.StatusDto;
import br.com.alurafood.pedidos.model.ItemDoPedido;
import br.com.alurafood.pedidos.model.Pedido;
import br.com.alurafood.pedidos.model.Status;
import br.com.alurafood.pedidos.repository.ItemDoPedidoRepository;
import br.com.alurafood.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    @Autowired
    private PedidoRepository PedidoRepository;
    
    @Autowired
    private ItemDoPedidoRepository ItemRepository;

    @Autowired
    private final ModelMapper modelMapper;


    public List<PedidoDto> obterTodos() {
        return PedidoRepository.findAll().stream()
                .map(p -> modelMapper.map(p, PedidoDto.class))
                .collect(Collectors.toList());
    }

    public PedidoDto obterPorId(Long id) {
        Pedido pedido = PedidoRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return modelMapper.map(pedido, PedidoDto.class);
    }

    public PedidoDto criarPedido(PedidoDto dto) {
        Pedido pedido = modelMapper.map(dto, Pedido.class);

        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus(Status.REALIZADO);
        pedido.getItens().forEach(item -> item.setPedido(pedido));
        Pedido salvo = PedidoRepository.save(pedido);

        return modelMapper.map(pedido, PedidoDto.class);
    }

    public PedidoDto atualizaPedido(Long id, PedidoDto dto) {

        Optional<Pedido> pedidoExist = PedidoRepository.findById(id);

        if (pedidoExist.isPresent()) {


            Pedido pedido = PedidoRepository.getById(id);
            List<ItemDoPedido> items = Arrays.asList(modelMapper.map(dto.getItens(), ItemDoPedido[].class));
            items.forEach(item -> item.setPedido(pedido));

            items.forEach(item -> ItemRepository.save(item));

            return modelMapper.map(pedido, PedidoDto.class);
        }
        throw new EntityNotFoundException();
    }

    public PedidoDto atualizaStatus(Long id, StatusDto dto) {

        Pedido pedido = PedidoRepository.porIdComItens(id);

        if (pedido == null) {
            throw new EntityNotFoundException();
        }

        pedido.setStatus(dto.getStatus());
        PedidoRepository.atualizaStatus(dto.getStatus(), pedido);
        return modelMapper.map(pedido, PedidoDto.class);
    }

    public void aprovaPagamentoPedido(Long id) {

        Pedido pedido = PedidoRepository.porIdComItens(id);

        if (pedido == null) {
            throw new EntityNotFoundException();
        }

        pedido.setStatus(Status.PAGO);
        PedidoRepository.atualizaStatus(Status.PAGO, pedido);
    }
}
