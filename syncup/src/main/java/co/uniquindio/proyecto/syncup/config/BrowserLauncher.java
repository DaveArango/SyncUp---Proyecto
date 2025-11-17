package co.uniquindio.proyecto.syncup.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import java.awt.Desktop;
import java.net.URI;

@Configuration
public class BrowserLauncher {

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserAfterStartup() {
        String swaggerUrl = "http://localhost:8080/swagger-ui.html";

        try {
            // Esperar un poco para que la aplicación esté completamente lista
            Thread.sleep(2000);

            System.out.println("Abriendo Swagger UI: " + swaggerUrl);

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(swaggerUrl));
                } else {
                    System.out.println("Browse action not supported");
                    openFallback(swaggerUrl);
                }
            } else {
                System.out.println("Desktop not supported");
                openFallback(swaggerUrl);
            }
        } catch (Exception e) {
            System.err.println("Error al abrir el navegador: " + e.getMessage());
            System.out.println("Puedes acceder manualmente a: " + swaggerUrl);
        }
    }

    private void openFallback(String url) {
        try {
            // Intentar con comandos del sistema operativo
            String os = System.getProperty("os.name").toLowerCase();
            Runtime runtime = Runtime.getRuntime();

            if (os.contains("win")) {
                // Windows
                runtime.exec("cmd /c start " + url);
            } else if (os.contains("mac")) {
                // macOS
                runtime.exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux
                runtime.exec("xdg-open " + url);
            } else {
                System.out.println("Sistema operativo no soportado para apertura automática");
            }
        } catch (Exception e) {
            System.err.println("Error en método alternativo: " + e.getMessage());
        }
    }
}
