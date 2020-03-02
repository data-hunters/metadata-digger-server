package ai.datahunters.md.server.photos.indexing.mdrunner;

import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
public class MetadataDiggerService {
    private Path executablePath;
    private Path configurationPath;
    private Path extractedPhotosPath;

    public MetadataDiggerService(Path executablePath, Path configurationPath, Path extractedPhotosPath) {
        this.executablePath = executablePath;
        this.configurationPath = configurationPath;
        this.extractedPhotosPath = extractedPhotosPath;
    }

    public Mono<MetadataDiggerRunFinished> startDigger(List<Path> imagesToBeIndexed) {
        return copyAllImagesToPath(imagesToBeIndexed)
                .flatMap(() -> runCommandLine())
                .filterWhen()
    }

    private Mono<Long> copyAllImagesToPath(List<Path> images) {
        return Flux.fromIterable(images)
                .map(file -> {
                    try {
                        Path target = extractedPhotosPath.resolve(file.getFileName());
                        log.debug("Coping" + file + "to" + target);
                        return Files.copy(file, target);
                    } catch (IOException e) {
                        throw Exceptions.bubble(e);
                    }
                }).collect(Collectors.counting());
    }

    private Mono<Integer> runCommandLine() {
        return Mono.fromSupplier(() -> {
            try {
                return new ProcessExecutor().command("sh", executablePath.toAbsolutePath().toString(), configurationPath.toAbsolutePath().toString())
                        .redirectOutput(Slf4jStream.of("Metadata Digger").asInfo()).execute();
            } catch (IOException e) {
                throw Exceptions.bubble(e);
            } catch (InterruptedException e) {
                throw Exceptions.bubble(e);
            } catch (TimeoutException e) {
                throw Exceptions.bubble(e);
            }
        }).map(ProcessResult::getExitValue);
    }
}
