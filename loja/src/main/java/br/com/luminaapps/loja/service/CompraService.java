package br.com.luminaapps.loja.service;

import br.com.luminaapps.loja.client.FornecedorClient;
import br.com.luminaapps.loja.model.CompraDTO;
import br.com.luminaapps.loja.model.InfoFornecedorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraService {
    @Autowired
    private RestTemplate client;

    @Autowired
    private FornecedorClient fornecedorClient;

    //Pega as informções disponíveis que vieram do Eureka
    @Autowired
    private DiscoveryClient eureka;

    public List<String> getInstancesFornecedor() {
        return eureka.getInstances("fornecedor")
                .stream()
                .map(instance -> "localhost:" + instance.getPort())
                .collect(Collectors.toList());
    }

    //Modelo Utilizando RestTemplate
    public void realizarCompraV1(CompraDTO compra) {
        String estado = compra.getEndereco().getEstado();

        String url = String.format("http://fornecedor/info/%s", estado);
        ResponseEntity<InfoFornecedorDTO> exchange = client.exchange(url, HttpMethod.GET, null,
                InfoFornecedorDTO.class);

        InfoFornecedorDTO body = exchange.getBody();
        System.out.println(body.getEndereco());
    }

    //Modelo Utilizando FeingClient
    public void realizarCompra(CompraDTO compra) {
        String estado = compra.getEndereco().getEstado();
        InfoFornecedorDTO info = fornecedorClient.getInforPorEstado(estado);
        System.out.println(info.getEndereco());
    }

}
