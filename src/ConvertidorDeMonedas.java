import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertidorDeMonedas {

    // Reemplaza con tu clave API
    private static final String API_KEY = "c2691e20715ccd748d05aa44";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        // Saludo de bienvenida con fecha y hora actual
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("¡Bienvenido al convertidor de monedas!");
        System.out.println(ahora.format(formatoFecha)); // Modificación aquí

        Scanner scanner = new Scanner(System.in);
        boolean continuar = true; // Variable para controlar el bucle

        while (continuar) {
            // Solicitar cantidad a convertir
            System.out.print("Ingrese la cantidad a convertir: ");
            double cantidad = scanner.nextDouble();

            // Solicitar moneda de origen
            System.out.print("Ingrese la moneda de origen (Ejemplo: USD, EUR, JPY): ");
            String monedaDesde = scanner.next().toUpperCase();

            // Solicitar moneda de destino
            System.out.print("Ingrese la moneda de destino (Ejemplo: MXN, GBP, AUD): ");
            String monedaHasta = scanner.next().toUpperCase();

            double resultado = convertir(cantidad, monedaDesde, monedaHasta);

            if (resultado != -1) {
                System.out.printf("Resultado: %.2f %s%n", resultado, monedaHasta);
            } else {
                System.out.println("Error al realizar la conversión.");
            }

            // Preguntar si el usuario desea realizar otra conversión
            System.out.print("¿Desea realizar otra conversión? (s/n): ");
            char respuesta = scanner.next().toLowerCase().charAt(0);
            continuar = (respuesta == 's');
        }

        scanner.close();
        System.out.println("Gracias por usar el convertidor de monedas. ¡Hasta luego!");
    }

    private static double convertir(double cantidad, String monedaDesde, String monedaHasta) {
        try {
            // Hacer la conexión a la API
            URL url = new URL(API_URL + monedaDesde);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Verificar si la conexión es exitosa
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Error: " + responseCode);
            } else {
                // Leer la respuesta JSON
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // Cerrar conexiones
                in.close();
                conn.disconnect();

                // Procesar la respuesta JSON
                JSONObject jsonResponse = new JSONObject(content.toString());
                JSONObject conversionRates = jsonResponse.getJSONObject("conversion_rates");

                // Verificar si las monedas están disponibles
                if (conversionRates.has(monedaDesde) && conversionRates.has(monedaHasta)) {
                    double tasaDesde = conversionRates.getDouble(monedaDesde);
                    double tasaHasta = conversionRates.getDouble(monedaHasta);

                    // Calcular el resultado en la moneda de destino
                    return cantidad * (tasaHasta / tasaDesde); // Retorna la cantidad en la moneda de destino
                } else {
                    System.out.println("Una de las monedas no está disponible.");
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
