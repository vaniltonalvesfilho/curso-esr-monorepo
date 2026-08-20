package com.algaworks.algafoodapi;

import com.algaworks.algafoodapi.api.exceptionhandler.ProblemType;
import com.algaworks.algafoodapi.domain.model.Cidade;
import com.algaworks.algafoodapi.domain.model.Cozinha;
import com.algaworks.algafoodapi.domain.model.Estado;
import com.algaworks.algafoodapi.domain.model.Restaurante;
import com.algaworks.algafoodapi.domain.repository.CidadeRepository;
import com.algaworks.algafoodapi.domain.repository.CozinhaRepository;
import com.algaworks.algafoodapi.domain.repository.EstadoRepository;
import com.algaworks.algafoodapi.domain.repository.RestauranteRepository;
import com.algaworks.algafoodapi.util.AccessTokenFactory;
import com.algaworks.algafoodapi.util.DatabaseCleaner;
import com.algaworks.algafoodapi.util.ResourceUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CadastroRestauranteIT {

    private static final String VIOLACAO_REGRA_DE_NEGOCIO = ProblemType.ERRO_NEGOCIO.getTitle();
    private static final String DADOS_INVALIDOS = ProblemType.DADOS_INVALIDOS.getTitle();
    private static final int RESTAURANTE_ID_INEXISTENTE = 100;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private CozinhaRepository cozinhaRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    @Autowired
    private AccessTokenFactory accessTokenFactory;


    private String jsonRestaurante;
    private String jsonRestauranteSemFrete;
    private String jsonRestauranteSemCozinha;
    private String jsonRestauranteComCozinhaInexistente;


    private Restaurante burguerTopRestaurante;

    @BeforeEach
    public void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/v1/restaurantes";
        RestAssured.authentication = RestAssured.oauth2(accessTokenFactory.gerarToken("EDITAR_RESTAURANTES"));

        jsonRestaurante = ResourceUtils.getContentFromResource("/json/correto/restaurante-new-york-barbecue.json");
        jsonRestauranteSemFrete = ResourceUtils.getContentFromResource("/json/incorreto/restaurante-new-york-barbecue-sem-frete.json");
        jsonRestauranteSemCozinha = ResourceUtils.getContentFromResource("/json/incorreto/restaurante-new-york-barbecue-sem-cozinha.json");
        jsonRestauranteComCozinhaInexistente = ResourceUtils.getContentFromResource("/json/incorreto/restaurante-new-york-barbecue-com-cozinha-inexistente.json");

        databaseCleaner.clearTables();
        prepararDados();

    }

    /**
     * Deve retornar status code 200 quando a requisão for realizada para obter
     * uma lista de restaurantes.
     */
    @Test
    public void shouldReturnStatus200_WhenFetchRestaurantes() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldReturnStatus201_WhenCreateRestaurante() {
        RestAssured.given()
                .body(jsonRestaurante)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void shouldReturnStatus400_WhenCreateRestauranteWithoutTaxaFrete() {
        RestAssured.given()
                .body(jsonRestauranteSemFrete)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", Matchers.equalTo(DADOS_INVALIDOS));
    }

    @Test
    public void shouldReturnStatus400_WhenCreateRestauranteWithoutCozinha() {
        RestAssured.given()
                .body(jsonRestauranteSemCozinha)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", Matchers.equalTo(DADOS_INVALIDOS));
    }

    @Test
    public void shouldReturnStatus400_WhenCreateRestauranteWithCozinhaNotExists() {
        RestAssured.given()
                .body(jsonRestauranteComCozinhaInexistente)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", Matchers.equalTo(VIOLACAO_REGRA_DE_NEGOCIO));
    }

    @Test
    public void shouldReturnStatus404_WhenFetchCozinhaNotExists() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .pathParam("restauranteId", RESTAURANTE_ID_INEXISTENTE)
                .when()
                .get("/{restauranteId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void shouldReturnStatus200_WhenFetchCozinhaExists() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .pathParam("restauranteId", burguerTopRestaurante.getId())
                .when()
                .get("/{restauranteId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("nome", Matchers.equalTo(burguerTopRestaurante.getNome()));
    }



    public void prepararDados() {
        // O endereço passou a ser obrigatório no RestauranteInput, e o cadastro
        // resolve a cidade pelo id — sem essa massa o POST válido volta 400.
        Estado estado = new Estado();
        estado.setNome("Minas Gerais");
        estadoRepository.save(estado);

        Cidade cidade = new Cidade();
        cidade.setNome("Uberlândia");
        cidade.setEstado(estado);
        cidadeRepository.save(cidade);

        Cozinha cozinhaBrasileira = new Cozinha();
        cozinhaBrasileira.setNome("Brasileira");
        cozinhaRepository.save(cozinhaBrasileira);

        Cozinha cozinhaAmericana = new Cozinha();
        cozinhaAmericana.setNome("Americana");
        cozinhaRepository.save(cozinhaAmericana);

        burguerTopRestaurante = new Restaurante();
        burguerTopRestaurante.setNome("Cogobongo");
        burguerTopRestaurante.setTaxaFrete(new BigDecimal(10));
        burguerTopRestaurante.setCozinha(cozinhaAmericana);
        restauranteRepository.save(burguerTopRestaurante);

        Restaurante comidaMineiraRestaurante = new Restaurante();
        comidaMineiraRestaurante.setNome("Comida Mineira");
        comidaMineiraRestaurante.setTaxaFrete(new BigDecimal(10));
        comidaMineiraRestaurante.setCozinha(cozinhaBrasileira);
        restauranteRepository.save(comidaMineiraRestaurante);
    }
}
