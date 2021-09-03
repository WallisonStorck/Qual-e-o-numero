package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author storck
 */
public class Animal
{
    /*
    * A lógica do programa se baseia em obter um valor da requisição
    * e entrar num laço de repetição até que o usuário acerte o seu palpite
    * mostrando se o número obtido é maior ou menor em relação ao seu palpite,
    * para isso usei o "do...while" visto que o usuário terá que
    * digitar no mínimo 1 palpite para acertar
    */
    public static void main( String[] args ) throws IOException, InterruptedException {

        /*
        * A variável "status" vai controlar o fluxo, possíveis valores:
        * 0 --> jogador ainda não acertou o número!
        * 1 --> jogador acertou o número! ou erro!
        */
        int status = 0; //Ela já inicia considerando que o jogador ainda não acertou o número.

        /*
        * 1ª Etapa: fazendo a requisição a API,
        * nessa parte definimos as variáveis de requisição
        * usando o HttpClient implementado no Java 11,
        * a requisição é feita e guardada na variável "response"
        * e transformada em JSON na variável "resultInJson"
        * pois só assim conseguiremos obter o valor do objeto na chave "value"
        */
        HttpClient client = HttpClient.newHttpClient(); //Criando o client
        HttpRequest request = HttpRequest.newBuilder() //Criando o request
                .GET() //Define método
                .header("accept", "application/json") //Define cabeçalho
                .uri(URI.create("https://us-central1-ss-devops.cloudfunctions.net/rand?min=1&max=300")) //Cria a URI
                .build(); //Compila
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); //Envia a requisição e guarda a reposta em "response"
        JSONObject resultInJson = new JSONObject(response.body()); //Converte o resultado da requisição em JSON e guarda em "resultInJson"

        /*
        * 2ª Etapa: Lê a entrada de usuário e compara com valor da requisição,
        * nesse ponto temos que importar e implementar uma variável que vai guardar
        * o valor digitado pelo usuário, esse valor vai ser comparado com valor obtido pela API
        */
        Scanner readNumber = new Scanner(System.in); //Monitora a entrada do teclado
        System.out.println("\nEntre com um número: ");

        /*
        * Com o resultado da requisição a API guardado, agora precisamos
        * aceitar e entrada do usuário e comparar até que ele acerte o número
        */
        do {
            /*
            * Mas, temos a possibilidade do conteúdo de "resultInJson" não ser um valor numérico,
            * ou seja, temos aquela pequena possibilidade de vir um erro de StatusCode: 502
            * mesmo com todos os parâmetros da requisição corretos... então vamos verificar isso
            * tentando obter o valor numérico de dentro de uma possível chave "value" usando o try...catch,
            * caso seja obtido com sucesso então fazemos a comparação, senão,
            * no catch exibimos a seguinte mensagem: "Erro" como manda as instruções.
            */
            try {
//                int hunch = readNumber.nextInt(); //Lê a entrada do usuário e guarda numa variável "palpite".
                int number = resultInJson.getInt("value");
                int hunch = readNumber.nextInt(); //Lê a entrada do usuário e guarda numa variável "palpite".

                if (hunch > number){
                    System.out.println("É menor");
                    System.out.println("\nTente de novo: ");
                    status = 0;
                } else if (hunch < number){
                    System.out.println("É maior");
                    System.out.println("\nTente de novo: ");
                    status = 0;
                } else {
                    System.out.println("Acertou!");
                    status = 1;
                }
            } catch (JSONException e){
                System.out.println("Erro");
                status = 1;
            }

        } while (status != 1); //Sai do loop se o número for 0 ou negativo

        /*
         * Ao fim do programa (quando o usuário acertar o número)
         * ele será pertuntado se deseja jogar novamente, caso a resposta seja "s" ou "S"
         * o método main será chamado novamente reiniciando jogo,
         */
        System.out.println("\nDeseja jogar de novo? (S/N)");
        Scanner readLetter = new Scanner(System.in); //Monitora a entrada do teclado
        String resUser = readLetter.nextLine(); //Lê a resposta do usuário se deseja jogar novamente.
        if (resUser.equals("S") || resUser.equals("s")){
            main(new String[]{});
        } else {
            System.out.println("Tchau!");
        }
    }
}

