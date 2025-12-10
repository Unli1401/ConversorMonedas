import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConversorMonedas {

    // Creo variable para la Key
    private static final String API_KEY = "0363b732ad714cecf11919c8";

    private static final String BASE_URL =
            "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    private static final Map<String, Double> tasas = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("============================================");
        System.out.println("   ¡BIENVENIDO AL CONVERSOR DE MONEDAS!");
        System.out.println("   Datos en tiempo real - ExchangeRate API");
        System.out.println("============================================\n");

        if (!cargarTasas()) {
            System.out.println("😭 No se pudieron cargar las tasas.");
            return;
        }

        boolean seguir = true;

        while (seguir) {
            mostrarMenu();

            int opcion;
            while (true) {
                System.out.print("👉 Elige una opción: ");
                try {
                    opcion = Integer.parseInt(scanner.nextLine());
                    if (opcion < 0 || opcion > 6) {
                        System.out.println("⚠️ Opción fuera de rango.");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Debes ingresar un número.");
                }
            }

            if (opcion == 0) {
                System.out.println("👋 Gracias por usar el conversor.");
                break;
            }

            double monto;
            while (true) {
                System.out.print("💰 Ingresa el monto a convertir: ");
                try {
                    monto = Double.parseDouble(scanner.nextLine());
                    if (monto <= 0) {
                        System.out.println("⚠️ El monto debe ser mayor que 0.");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Ingresa un número válido.");
                }
            }

            System.out.println("\n🔄 Realizando conversión...\n");
            double resultado = 0;

            switch (opcion) {
                case 1 -> {
                    resultado = convertir("CLP", "USD", monto);
                    System.out.printf("👉 %.2f CLP = %.2f USD\n", monto, resultado);
                }
                case 2 -> {
                    resultado = convertir("USD", "CLP", monto);
                    System.out.printf("👉 %.2f USD = %.2f CLP\n", monto, resultado);
                }
                case 3 -> {
                    resultado = convertir("CLP", "EUR", monto);
                    System.out.printf("👉 %.2f CLP = %.2f EUR\n", monto, resultado);
                }
                case 4 -> {
                    resultado = convertir("EUR", "CLP", monto);
                    System.out.printf("👉 %.2f EUR = %.2f CLP\n", monto, resultado);
                }
                case 5 -> {
                    resultado = convertir("USD", "EUR", monto);
                    System.out.printf("👉 %.2f USD = %.2f EUR\n", monto, resultado);
                }
                case 6 -> {
                    resultado = convertir("EUR", "USD", monto);
                    System.out.printf("👉 %.2f EUR = %.2f USD\n", monto, resultado);
                }
            }

            System.out.println("\n 👌 Conversión realizada con éxito.");
            System.out.print("\n¿Desea realizar otra conversión? (S/N): ");
            String respuesta = scanner.nextLine().trim().toUpperCase();

            if (!respuesta.equals("S")) {
                seguir = false;
                System.out.println("\n👋 Gracias por usar el conversor. ¡Hasta luego!");
            }
        }

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n========== MENÚ ==========");
        System.out.println("1) CLP → USD");
        System.out.println("2) USD → CLP");
        System.out.println("3) CLP → EUR");
        System.out.println("4) EUR → CLP");
        System.out.println("5) USD → EUR");
        System.out.println("6) EUR → USD");
        System.out.println("0) Salir");
        System.out.println("==========================");
    }

    private static boolean cargarTasas() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.connect();

            InputStreamReader reader =
                    new InputStreamReader((InputStream) request.getContent());

            JsonObject json =
                    JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject rates = json.getAsJsonObject("conversion_rates");

            for (Map.Entry<String, JsonElement> entry : rates.entrySet()) {
                tasas.put(entry.getKey(), entry.getValue().getAsDouble());
            }

            System.out.println("👍 Tasas cargadas correctamente.\n");
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error al cargar tasas: " + e.getMessage());
            return false;
        }
    }

    private static double convertir(String desde, String hacia, double monto) {
        double tasaDesde = tasas.get(desde);
        double tasaHacia = tasas.get(hacia);

        double montoUSD = monto / tasaDesde;
        return montoUSD * tasaHacia;
    }
}
