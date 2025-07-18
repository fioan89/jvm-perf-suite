package com.github.fioan89.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = ["-Xms2G", "-Xmx2G"])
@Warmup(iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 50, time = 100, timeUnit = TimeUnit.MILLISECONDS)
open class FileExistsBenchmark {

    private lateinit var existingFile: File
    private lateinit var nonExistingFile: File
    private lateinit var existingPath: Path
    private lateinit var nonExistingPath: Path
    private lateinit var tempDir: Path

    @Setup(Level.Trial)
    fun setup() {
        // Create a temporary directory for our test files
        tempDir = Files.createTempDirectory("jmh-file-exists-benchmark")

        // Create an existing file
        existingPath = tempDir.resolve("existing-file.txt")
        Files.write(existingPath, "test content".toByteArray())
        existingFile = existingPath.toFile()

        // Create references to non-existing files
        nonExistingPath = tempDir.resolve("non-existing-file.txt")
        nonExistingFile = nonExistingPath.toFile()

        // Ensure the non-existing file really doesn't exist
        if (nonExistingFile.exists()) {
            nonExistingFile.delete()
        }

        println("Setup complete:")
        println("  Existing file: ${existingFile.absolutePath} (exists: ${existingFile.exists()})")
        println("  Non-existing file: ${nonExistingFile.absolutePath} (exists: ${nonExistingFile.exists()})")
    }

    @TearDown(Level.Trial)
    fun tearDown() {
        // Clean up test files
        if (existingFile.exists()) {
            existingFile.delete()
        }
        Files.deleteIfExists(tempDir)
    }

    @Benchmark
    fun fileExistsOnExistingFile(bh: Blackhole) {
        val result = existingFile.exists()
        bh.consume(result)
    }

    @Benchmark
    fun fileExistsOnNonExistingFile(bh: Blackhole) {
        val result = nonExistingFile.exists()
        bh.consume(result)
    }

    @Benchmark
    fun nioFilesExistsOnExistingFile(bh: Blackhole) {
        val result = Files.exists(existingPath)
        bh.consume(result)
    }

    @Benchmark
    fun nioFilesExistsOnNonExistingFile(bh: Blackhole) {
        val result = Files.exists(nonExistingPath)
        bh.consume(result)
    }

    // Files.exists() benchmarks with multiple different files
    @Benchmark
    fun nioFilesExistsOnRandomExistingFiles(bh: Blackhole, state: MultipleFilesState) {
        val path = state.getNextExistingPath()
        val result = Files.exists(path)
        bh.consume(result)
    }

    @Benchmark
    fun nioFilesExistsOnRandomNonExistingFiles(bh: Blackhole, state: MultipleFilesState) {
        val path = state.getNextNonExistingPath()
        val result = Files.exists(path)
        bh.consume(result)
    }
}

/**
 * Provider of multiple different existing and non-existing files to avoid potential caching
 */
@State(Scope.Thread)
open class MultipleFilesState {
    private var counter = 0
    private lateinit var tempDir: Path
    private lateinit var existingFiles: List<Path>
    private lateinit var nonExistingFiles: List<Path>

    @Setup(Level.Trial)
    fun setupFiles() {
        tempDir = Files.createTempDirectory("jmh-multiple-files-exists-benchmark")

        // Create multiple existing files
        existingFiles = (1..100).map { i ->
            val path = tempDir.resolve("existing-file-$i.txt")
            Files.write(path, "test content $i".toByteArray())
            path
        }

        // Create references to multiple non-existing files
        nonExistingFiles = (1..100).map { i ->
            tempDir.resolve("non-existing-file-$i.txt")
        }
    }

    @TearDown(Level.Trial)
    fun cleanupFiles() {
        existingFiles.forEach { Files.deleteIfExists(it) }
        Files.deleteIfExists(tempDir)
    }

    fun getNextExistingPath(): Path {
        return existingFiles[counter++ % existingFiles.size]
    }

    fun getNextNonExistingPath(): Path {
        return nonExistingFiles[counter++ % nonExistingFiles.size]
    }
}
