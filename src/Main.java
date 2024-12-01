import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Main {
    private static final String INPUT_PATH = "input";
    private static final String OUTPUT_PATH = "output/output.txt";

    private static final String CYCLIC_DEPENDENCIES_ERROR = "Error: files has cyclic dependencies:";
    private static final String ORDERED_FILES_MESSAGE = "Ordered files:";

    public static void main(String[] args) {
        ArrayList<Path> rawFiles = getFiles(getInputPath(args));
        Collections.sort(rawFiles);
        HashMap<Path, ArrayList<Path>> dependencies = getDependencies(rawFiles, getInputPath(args));
        ArrayList<PathsPair> cyclicDependencies = getCyclicDependencies(dependencies);

        if (!cyclicDependencies.isEmpty()) {
            printCyclicDependenciesError(cyclicDependencies, getInputPath(args));
            return;
        }

        var orderedFiles = orderFiles(dependencies, rawFiles);
        printOrderedFiles(orderedFiles, getInputPath(args));
        writeOrderedFiles(orderedFiles, getOutputPath(args));
    }

    private record PathsPair(Path a, Path b) { }

    private static void writeOrderedFiles(SequencedSet<Path> orderedFiles, Path outputPath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath.toString())) {
            for (Path file : orderedFiles) {
                byte[] bytes = Files.readAllBytes(file);
                fileOutputStream.write(bytes);
                fileOutputStream.write("\n\n".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getOutputPath(String[] args) {
        if (args.length > 1) {
            return Paths.get(args[1]);
        } else {
            return Paths.get(OUTPUT_PATH);
        }
    }

    private static Path getInputPath(String[] args) {
        if (args.length > 0) {
            return Paths.get(args[0]);
        } else {
            return Paths.get(INPUT_PATH);
        }
    }

    private static void printOrderedFiles(SequencedSet<Path> orderedFiles, Path inputPath) {
        StringBuilder stringBuilder = new StringBuilder(ORDERED_FILES_MESSAGE);
        for (Path file : orderedFiles) {
            stringBuilder.append(String.format("\n%s", inputPath.relativize(file)));
        }
        System.out.println(stringBuilder);
    }

    private static void printCyclicDependenciesError(ArrayList<PathsPair> cyclicDependencies, Path inputPath) {
        StringBuilder stringBuilder = new StringBuilder(CYCLIC_DEPENDENCIES_ERROR);
        for (PathsPair cyclicDependency : cyclicDependencies) {
            stringBuilder.append(String.format("\n%s -> %s", inputPath.relativize(cyclicDependency.a), inputPath.relativize(cyclicDependency.b)));
        }
        System.out.println(stringBuilder);
    }

    private static SequencedSet<Path> orderFiles(HashMap<Path, ArrayList<Path>> dependencies, ArrayList<Path> rawFiles) {
        for (Map.Entry<Path, ArrayList<Path>> entry : dependencies.entrySet()) {
            int index = rawFiles.indexOf(entry.getKey()) + 1;
            rawFiles.addAll(index, entry.getValue());
        }

        return new LinkedHashSet<>(rawFiles.reversed()).reversed();
    }

    private static ArrayList<PathsPair> getCyclicDependencies(HashMap<Path, ArrayList<Path>> dependencies) {
        ArrayList<PathsPair> cyclicDependencies = new ArrayList<>();

        for (Map.Entry<Path, ArrayList<Path>> entry : dependencies.entrySet()) {
            for (Path path : entry.getValue()) {
                if (dependencies.containsKey(path)) {
                    if (dependencies.get(path).contains(entry.getKey())) {
                        cyclicDependencies.add(new PathsPair(entry.getKey(), path));
                    }
                }
            }
        }

        return cyclicDependencies;
    }

    private static HashMap<Path, ArrayList<Path>> getDependencies(ArrayList<Path> files, Path contentPath) {
        HashMap<Path, ArrayList<Path>> dependencies = new HashMap<>();

        for (Path file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file.toString()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    tryAddDependency(contentPath, file, line, dependencies);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return dependencies;
    }

    private static void tryAddDependency(Path contentPath, Path file, String line, HashMap<Path, ArrayList<Path>> dependencies) {
        String beginStr = "*require ‘";
        int beginIndex = line.indexOf(beginStr) + beginStr.length();
        int endIndex = line.lastIndexOf("’*");

        if (beginIndex > 0 && endIndex > 0) {
            String substring = line.substring(
                    beginIndex,
                    endIndex);

            Path path = Paths.get(substring + ".txt");
            path = contentPath.resolve(path);

            if (!dependencies.containsKey(path)) {
                var paths = new ArrayList<Path>();
                paths.add(file);
                dependencies.put(path, paths);
            } else {
                dependencies.get(path).add(file);
            }
        }
    }

    private static ArrayList<Path> getFiles(Path rootPath) {
        var files = new ArrayList<Path>();

        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!Files.isDirectory(file)) {
                        files.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return files;
    }
}