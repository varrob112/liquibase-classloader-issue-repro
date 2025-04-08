import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import liquibase.Scope;

public class Repro {

  public Repro() {
  }

  public void doRepro() {
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    try {
      File liquibaseJar = new File("lib/liquibase-core.jar");

      // This CL will load e.g. the liquibase.GlobalConfiguration class.
      // The liquibase.configuration.AutoloadedConfigurations class already loaded by AppClassLoader
      URLClassLoader customLoader = new URLClassLoader(
          new URL[]{liquibaseJar.toURI().toURL()},
          null // No parent
      );

      executorService.submit(() -> {
        Thread.currentThread().setContextClassLoader(customLoader);
        Scope.getCurrentScope();
      });
      Scope.getCurrentScope();

    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } finally {
      executorService.shutdown();
    }
  }
}