package com.shuvam.service;

import com.shuvam.model.ClassDefinitionRequest;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DynamicClassService {

    private final ApplicationContext context;
    private static final String BASE_PACKAGE = "com.shuvam.generated";
    private static final String SOURCE_PATH = "src/main/java/com/shuvam/generated/";

    public DynamicClassService(ApplicationContext context) {
        this.context = context;
    }

    public String generateAndRegisterClass(ClassDefinitionRequest request) {
        String response = null;
        try {
            String className = request.getClassName();
            Map<String, String> fields = request.getFields();

            // Check if bean already exists
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
            if (beanFactory.containsSingleton(className)) {
                response  = "A bean with the name '" + className + "' is already registered.";
                return response;
            }

            // 1. Generate Java Source
            String sourceCode = generateJavaSource(className, fields);

            // 2. Write Java File
            Path filePath = Paths.get(SOURCE_PATH + className + ".java");
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, sourceCode);

            // 3. Compile
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new IllegalStateException("Java Compiler not available. Ensure you are using a JDK environment.");
            }

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            Path outputDir = Paths.get("target/classes/");
            Files.createDirectories(outputDir); // create if not exists

            Iterable<? extends JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(
                            java.util.List.of(filePath.toFile())
                    );

            compiler.getTask(
                    null,
                    fileManager,
                    null,
                    java.util.List.of("-d", outputDir.toAbsolutePath().toString()), // <-- Important: -d option
                    null,
                    compilationUnits
            ).call();
            fileManager.close();

            // 4. Load compiled class
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("target/classes/com/shuvam/generated/").toURI().toURL()},
                    this.getClass().getClassLoader());
            Class<?> clazz = classLoader.loadClass(BASE_PACKAGE + "." + className);

            // 5. Register bean
            beanFactory = (DefaultListableBeanFactory)
                    context.getAutowireCapableBeanFactory();
            beanFactory.registerSingleton(className, clazz.getDeclaredConstructor().newInstance());

            response = "Class " + className + " registered successfully!";

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate and register class", e);
        }
        return response;
    }

    private String generateJavaSource(String className, Map<String, String> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(BASE_PACKAGE).append(";\n\n");
        sb.append("public class ").append(className).append(" {\n");

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            sb.append("    private ").append(entry.getValue()).append(" ").append(entry.getKey()).append(";\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    public List<String> listGeneratedClasses() {
        try (Stream<Path> paths = Files.walk(Paths.get(SOURCE_PATH))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.substring(0, fileName.lastIndexOf('.'));
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list generated classes", e);
        }
    }

}
