package hr.java.game.monopoly.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocumentationUtils {

    public static void generateDocumentation() {
        StringBuilder documentationGenerator = new StringBuilder();

        String path = "C:/Users/zeljk/Desktop/patrik_faks/java_aplikacije/monopoly/src/main/java";


        try {
            List<Path> classNameList =  Files.walk(Paths.get(path))
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .filter(p -> !p.getFileName().toString().equals("module-info.java"))
                    .toList();

            for(Path classPath : classNameList) {

                int indexOfHr = classPath.toString().indexOf("hr");
                String fqcn = classPath.toString().substring(indexOfHr);
                fqcn = fqcn.replace('\\', '.');
                fqcn = fqcn.substring(0, fqcn.length() - 5);

                Class<?> documentationClass = Class.forName(fqcn);

                String classModifiers = Modifier.toString(documentationClass.getModifiers());

                documentationGenerator.append("<h2>"
                        + classModifiers
                        + fqcn
                        + "</h2>\n");

                Field[] classVariables = documentationClass.getFields();

                for(Field field : classVariables) {
                    String modifiers = Modifier.toString(field.getModifiers());
                    documentationGenerator.append("<h3>"
                            + modifiers + " "
                            + field.getType().getName() + " "
                            + field.getName()
                            + "</h3>\n");
                }

                //Constructor[] classConstructors = documentationClass.getConstructors();

                //classConstructors[0].get
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                <title>Documentation</title>
                </head>
                <body>
                <h1>List of classes</h1>
                """
                + documentationGenerator.toString() +
                """
                </body>
                </html>
                """;

        try {
            Path directoryPath = Paths.get("documentation");
            Files.createDirectories(directoryPath);

            try(BufferedWriter writer = new BufferedWriter(new FileWriter("documentation/doc.html"))) {
                writer.write(html);
                DialogUtils.showSuccessDialog("Documentation was successfully generated!");
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
