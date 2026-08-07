package com.algaworks.algafoodapi;

import static io.restassured.RestAssured.*;

import com.algaworks.algafoodapi.domain.model.Cozinha;
import com.algaworks.algafoodapi.domain.repository.CozinhaRepository;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
class CadastroCozinhaIT {

    private static final int COZINHA_ID_INEXISTENTE = 100;

    private int totalCozinhas;
    private String jsonCozinhaChinesa;
    private Cozinha cozinhaAmericana;
    private Cozinha cozinhaTailandesa;


    @LocalServerPort
    private int port;

//    @Autowired
//    private Flyway flyway;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private CozinhaRepository cozinhaRepository;

    @Autowired
    private AccessTokenFactory accessTokenFactory;

    @BeforeEach
    public void setup() {
        enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/v1/cozinhas";
        RestAssured.authentication = RestAssured.oauth2(accessTokenFactory.gerarToken("EDITAR_COZINHAS"));
        jsonCozinhaChinesa = ResourceUtils.getContentFromResource("/json/correto/cozinha_chinesa.json");

//        flyway.migrate();
        databaseCleaner.clearTables();
        prepararDados();
    }

    @Test
    public void shouldReturnStatus200_WhenFindCozinhas() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldContainCozinhas_WhenFindCozinhas() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                // A listagem virou PagedModel (HAL): a coleção agora fica sob _embedded.
                .body("_embedded.cozinhas", Matchers.hasSize(this.totalCozinhas));
    }

    @Test
    public void shouldReturnResponseAndStatus_WhenFindCozinhaExists() {
        given()
                .pathParam("cozinhaId", cozinhaAmericana.getId())
                .accept(ContentType.JSON)
                .when()
                .get("/{cozinhaId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("nome", Matchers.equalTo(cozinhaAmericana.getNome()));
    }

    @Test
    public void shouldReturnResponseAndStatus404_WhenFindCozinhaNotExists() {
        given()
                .pathParam("cozinhaId", COZINHA_ID_INEXISTENTE)
                .accept(ContentType.JSON)
                .when()
                .get("/{cozinhaId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void shouldReturnStatus201_WhenCreateCozinha() {
        given()
                .body(jsonCozinhaChinesa)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

//    @Autowired
//    private CadastroCozinhaService cadastroCozinhaService;
//
//    @Test
//    public void testarCadastroCozinhaComSucesso() {
//        // cenário
//        Cozinha novaCozinha = new Cozinha();
//        novaCozinha.setNome("Chinesa");
//        // ação
//        novaCozinha = cadastroCozinhaService.save(novaCozinha);
//        // validação
//        assertThat(novaCozinha).isNotNull();
//        assertThat(novaCozinha.getId()).isNotNull();
//
//    }
//
//    @Test
//    public void failPathWithoutName() {
//        assertThrows(ConstraintViolationException.class, () -> {
//            Cozinha novaCozinha = new Cozinha();
//            novaCozinha.setNome(null);
//
//            cadastroCozinhaService.save(novaCozinha);
//        });
//    }
//
//    @Test
//    public void failPathWhenDeleteCozinhaEmUso() {
//       assertThrows(EntidadeEmUsoException.class, () -> {
//          cadastroCozinhaService.excluirPorId(1L);
//        });
//
//    }
//
//    @Test
//    public void shouldFailWhenDeleteCozinhaInexistente() {
//        assertThrows(CozinhaNaoEncontradaException.class, () -> {
//                    cadastroCozinhaService.excluirPorId(100L);
//                });
//    }


    private void prepararDados() {
        cozinhaTailandesa = new Cozinha();
        cozinhaTailandesa.setNome("Tailandesa");
        cozinhaRepository.save(cozinhaTailandesa);

        cozinhaAmericana = new Cozinha();
        cozinhaAmericana.setNome("Americana");
        cozinhaRepository.save(cozinhaAmericana);

        this.totalCozinhas = (int) cozinhaRepository.count();
    }


}
