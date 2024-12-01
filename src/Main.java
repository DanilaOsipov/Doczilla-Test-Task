import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Main {
    private static final String CONTENT_PATH = "content";

    public static void main(String[] args) {
        Path contentPath = Paths.get(CONTENT_PATH);
        ArrayList<Path> rawFiles;

        try {
            rawFiles = getFiles(contentPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Collections.sort(rawFiles);

        HashMap<Path, ArrayList<Path>> dependencies = getDependencies(rawFiles, contentPath);

        ArrayList<PathsPair> cyclicDependencies = getCyclicDependencies(dependencies);
        if (!cyclicDependencies.isEmpty()) {
            // TODO error
        }

        var orderedFiles = orderFiles(dependencies, rawFiles);

        // TODO output
        orderedFiles.forEach(System.out::println);
    }

    private record PathsPair(Path a, Path b) { }

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

    private static ArrayList<Path> getFiles(Path rootPath) throws IOException {
        var files = new ArrayList<Path>();

        Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (!Files.isDirectory(file)) {
                    files.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return files;
    }
}