
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    public static void main(String args[]) throws IOException {
        try (ServerSocket server = new ServerSocket(3322)) {
            System.out.println("Servidor iniciado na porta 3322");

            while(true) {
                Socket cliente = server.accept();

                Thread.startVirtualThread(() -> {
                   try(cliente) {
                        atenderCliente(cliente);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }

                });
            }
        }

    }

    private static void atenderCliente(Socket cliente) throws IOException {

        System.out.println("Cliente conectado do IP "
                + cliente.getInetAddress().getHostAddress());

        Scanner entrada = new Scanner(
                cliente.getInputStream(), StandardCharsets.UTF_8
        );

        while (entrada.hasNextLine()) {
            String linha = entrada.nextLine();

            if (linha.isEmpty()) {
                break;
            }

            System.out.println(linha);
        }

        byte[] corpo = Files.readAllBytes(Path.of("index.html"));

        String cabecalhos = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Content-Length: " + corpo.length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n";

        OutputStream saida = cliente.getOutputStream();
        saida.write(cabecalhos.getBytes(StandardCharsets.US_ASCII));
        saida.write(corpo);
        saida.flush();
    }

}