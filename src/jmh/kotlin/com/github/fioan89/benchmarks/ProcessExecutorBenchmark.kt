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
import org.openjdk.jmh.annotations.Timeout
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.InvalidExitValueException
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = ["-Xms2G", "-Xmx2G"])
@Warmup(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 2, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 30, timeUnit = TimeUnit.SECONDS)
open class ProcessExecutorBenchmark {
    private lateinit var validCommand: String
    private lateinit var invalidCommand: String
    private lateinit var validArgs: List<String>
    private lateinit var invalidArgs: List<String>
    private lateinit var environmentVars: Map<String, String>

    @Setup(Level.Trial)
    fun setup() {
        val osName = System.getProperty("os.name").lowercase()
        val pathEnv = System.getenv("PATH") ?: ""
        environmentVars = mapOf(
            "BENCHMARK_VAR" to "test_value",
            "PATH" to pathEnv
        )

        when {
            osName.contains("windows") -> {
                validCommand = "cmd"
                validArgs = listOf("/c", "echo", "hello")
                invalidCommand = "nonexistent_command_12345"
                invalidArgs = listOf("arg1", "arg2")
            }
            else -> { // macOS, Linux and other Unix-like systems
                validCommand = "echo"
                validArgs = listOf("hello")
                invalidCommand = "nonexistent_command_12345"
                invalidArgs = listOf("arg1", "arg2")
            }
        }

        println("OS detected: $osName")
        println("Valid command: $validCommand ${validArgs.joinToString(" ")}")
        println("Invalid command: $invalidCommand ${invalidArgs.joinToString(" ")}")

        // Verify the valid command works
        try {
            val result = ProcessExecutor()
                .command(validCommand, *validArgs.toTypedArray())
                .environment(environmentVars)
                .exitValues(0)
                .readOutput(true)
                .execute()
                .outputUTF8()
            println("Setup verification successful: '$result'")
        } catch (e: Exception) {
            println("Setup verification failed: ${e.message}")
            throw e
        }
    }

    @Benchmark
    fun processExecutorWithExistingCommand(bh: Blackhole) {
        try {
            val result = ProcessExecutor()
                .command(validCommand, *validArgs.toTypedArray())
                .environment(environmentVars)
                .exitValues(0)
                .readOutput(true)
                .execute()
                .outputUTF8()
            bh.consume(result)
        } catch (e: Exception) {
            bh.consume(e.message)
        }
    }

    @Benchmark
    fun processExecutorWithNonExistingCommand(bh: Blackhole) {
        try {
            val result = ProcessExecutor()
                .command(invalidCommand, *invalidArgs.toTypedArray())
                .environment(environmentVars)
                .exitValues(0)
                .readOutput(true)
                .execute()
                .outputUTF8()
            bh.consume(result)
        } catch (e: InvalidExitValueException) {
            // Expected for invalid commands
            bh.consume(e.message)
        } catch (e: Exception) {
            // Command not found, etc.
            bh.consume(e.message)
        }
    }

    @Benchmark
    fun processExecutorWithRandomExistingCommand(bh: Blackhole, state: MultipleCommandsState) {
        val (cmd, args) = state.getNextExistingCommand()
        try {
            val result = ProcessExecutor()
                .command(cmd, *args.toTypedArray())
                .environment(environmentVars)
                .exitValues(0)
                .readOutput(true)
                .execute()
                .outputUTF8()
            bh.consume(result)
        } catch (e: Exception) {
            bh.consume(e.message)
        }
    }

    @Benchmark
    fun processExecutorWithRandomNonExistingCommand(bh: Blackhole, state: MultipleCommandsState) {
        val (cmd, args) = state.getNextNonExistingCommand()
        try {
            val result = ProcessExecutor()
                .command(cmd, *args.toTypedArray())
                .environment(environmentVars)
                .exitValues(0)
                .readOutput(true)
                .execute()
                .outputUTF8()
            bh.consume(result)
        } catch (e: Exception) {
            bh.consume(e.message)
        }
    }
}

/**
 * Benchmark different command variations per platform
 */
@State(Scope.Thread)
open class MultipleCommandsState {
    private var counter = 0
    private lateinit var validCommands: List<Pair<String, List<String>>>
    private lateinit var invalidCommands: List<Pair<String, List<String>>>

    @Setup(Level.Trial)
    fun setupCommands() {
        val osName = System.getProperty("os.name").lowercase()

        when {
            osName.contains("windows") -> {
                validCommands = listOf(
                    "cmd" to listOf("/c", "echo", "test1"),
                    "cmd" to listOf("/c", "dir", "/b"),
                    "cmd" to listOf("/c", "type", "nul"),
                    "powershell" to listOf("-Command", "Write-Host", "test2")
                )
                invalidCommands = listOf(
                    "nonexistent_cmd_1" to listOf("arg1"),
                    "nonexistent_cmd_2" to listOf("arg2"),
                    "nonexistent_cmd_3" to listOf("arg3")
                )
            }
            else -> { // macOS, Linux and other Unix-like systems
                validCommands = listOf(
                    "echo" to listOf("test1"),
                    "ls" to listOf("/tmp"),
                    "date" to listOf("+%Y-%m-%d"),
                    "uname" to listOf("-s")
                )
                invalidCommands = listOf(
                    "nonexistent_cmd_1" to listOf("arg1"),
                    "nonexistent_cmd_2" to listOf("arg2"),
                    "nonexistent_cmd_3" to listOf("arg3")
                )
            }
        }
    }

    fun getNextExistingCommand(): Pair<String, List<String>> {
        return validCommands[counter++ % validCommands.size]
    }

    fun getNextNonExistingCommand(): Pair<String, List<String>> {
        return invalidCommands[counter++ % invalidCommands.size]
    }
}
