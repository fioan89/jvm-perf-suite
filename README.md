# Java API Benchmarks

This repository contains JMH microbenchmarks for Java APIs, focusing on file system operations and process execution performance across different operating systems and hardware configurations.

## Benchmarks

### 1. File.exists() and Files.exists() Performance
- **File**: `FileExistsBenchmark.kt`
- **Description**: Measures latency and throughput for file existence checking
- **Scenarios**:
    - Existing files vs non-existing files
    - `java.io.File.exists()` vs `java.nio.file.Files.exists()`
    - Multiple file variations to avoid caching effects

### 2. ZeroTurnaround ProcessExecutor Performance
- **File**: `ProcessExecutorBenchmark.kt`
- **Description**: Measures latency for process execution using ZeroTurnaround ProcessExecutor
- **Scenarios**:
    - Valid commands vs invalid commands
    - Different command configurations (minimal, with environment, with timeout)
    - Cross-platform command variations

## Running the Benchmarks

```bash
# Run all benchmarks
./gradlew jmh

# Run specific benchmark
./gradlew jmh --includes="FileExists.*"
./gradlew jmh --includes="ProcessExecutor.*"

# Run with custom JVM options
./gradlew jmh -Pjmh.jvmArgs="-Xms4G -Xmx4G"
```

## Results

#### Windows 11 Version	10.0.22631 Build 22631

##### 11th Gen Intel(R) Core(TM) i7-11850H @ 2.50GHz, 2496 Mhz, 8 Core(s), 16 Logical Processor(s) - 64GB RAM - Model	Samsung SSD 980 1TB

```
# Test Date: 2025-07-18
# VM version: JDK 21.0.7, OpenJDK 64-Bit Server VM, 21.0.7+6-LTS
# VM options: -Xms2G -Xmx2G

Benchmark                                                             Mode  Cnt    Score    Error  Units
FileExistsBenchmark.fileExistsOnExistingFile                          avgt  100   51.980 ±  2.135  us/op
FileExistsBenchmark.fileExistsOnNonExistingFile                       avgt  100   18.162 ±  0.647  us/op
FileExistsBenchmark.nioFilesExistsOnExistingFile                      avgt  100   44.961 ±  1.447  us/op
FileExistsBenchmark.nioFilesExistsOnNonExistingFile                   avgt  100   22.008 ±  0.493  us/op
FileExistsBenchmark.nioFilesExistsOnRandomExistingFiles               avgt  100   43.184 ±  1.286  us/op
FileExistsBenchmark.nioFilesExistsOnRandomNonExistingFiles            avgt  100   24.533 ±  0.731  us/op
ProcessExecutorBenchmark.processExecutorWithExistingCommand           avgt   40  108.191 ±  3.517  ms/op
ProcessExecutorBenchmark.processExecutorWithNonExistingCommand        avgt   40    2.429 ±  0.034  ms/op
ProcessExecutorBenchmark.processExecutorWithRandomExistingCommand     avgt   40  211.267 ± 13.497  ms/op
ProcessExecutorBenchmark.processExecutorWithRandomNonExistingCommand  avgt   40    1.783 ±  0.070  ms/op
```

#### Ubuntu 24.04.2 LTS- Linux 5.15.167.4-microsoft-standard-WSL2 #1 SMP Tue Nov 5 00:21:55 UTC 2024 x86_64 x86_64 x86_64 GNU/Linux

##### 11th Gen Intel(R) Core(TM) i7-11850H @ 2.50GHz, 2496 Mhz, 8 Core(s), 16 Logical Processor(s) - 64GB RAM - Model	Samsung SSD 980 1TB

```
# Test Date: 2025-07-18
# VM version: JDK 21.0.8, OpenJDK 64-Bit Server VM, 21.0.8+9-LTS
# VM options: -Xms2G -Xmx2G

Benchmark                                                             Mode  Cnt   Score   Error  Units
FileExistsBenchmark.fileExistsOnExistingFile                          avgt  100   0.556 ± 0.018  us/op
FileExistsBenchmark.fileExistsOnNonExistingFile                       avgt  100   0.432 ± 0.018  us/op
FileExistsBenchmark.nioFilesExistsOnExistingFile                      avgt  100   0.569 ± 0.017  us/op
FileExistsBenchmark.nioFilesExistsOnNonExistingFile                   avgt  100   0.490 ± 0.013  us/op
FileExistsBenchmark.nioFilesExistsOnRandomExistingFiles               avgt  100   0.582 ± 0.016  us/op
FileExistsBenchmark.nioFilesExistsOnRandomNonExistingFiles            avgt  100   0.564 ± 0.023  us/op
ProcessExecutorBenchmark.processExecutorWithExistingCommand           avgt   40   1.005 ± 0.086  ms/op
ProcessExecutorBenchmark.processExecutorWithNonExistingCommand        avgt   40  48.910 ± 2.822  ms/op
ProcessExecutorBenchmark.processExecutorWithRandomExistingCommand     avgt   40   1.096 ± 0.035  ms/op
ProcessExecutorBenchmark.processExecutorWithRandomNonExistingCommand  avgt   40  52.594 ± 5.750  ms/op
```

#### macOS 15.5 (Sequoia) Darwin Kernel Version 24.5.0

##### Apple M4 Max - 36GB RAM - 1TB SSD

```
# Test Date: 2025-07-18
# VM version: JDK 21.0.7, OpenJDK 64-Bit Server VM, 21.0.7+6-LTS
# VM options: -Xms2G -Xmx2G

Benchmark                                                             Mode  Cnt  Score   Error  Units
FileExistsBenchmark.fileExistsOnExistingFile                          avgt  100  1.264 ± 0.010  us/op
FileExistsBenchmark.fileExistsOnNonExistingFile                       avgt  100  1.045 ± 0.005  us/op
FileExistsBenchmark.nioFilesExistsOnExistingFile                      avgt  100  0.831 ± 0.004  us/op
FileExistsBenchmark.nioFilesExistsOnNonExistingFile                   avgt  100  0.876 ± 0.005  us/op
FileExistsBenchmark.nioFilesExistsOnRandomExistingFiles               avgt  100  0.892 ± 0.004  us/op
FileExistsBenchmark.nioFilesExistsOnRandomNonExistingFiles            avgt  100  0.931 ± 0.006  us/op
ProcessExecutorBenchmark.processExecutorWithExistingCommand           avgt   40  1.953 ± 0.006  ms/op
ProcessExecutorBenchmark.processExecutorWithNonExistingCommand        avgt   40  1.338 ± 0.006  ms/op
ProcessExecutorBenchmark.processExecutorWithRandomExistingCommand     avgt   40  2.205 ± 0.066  ms/op
ProcessExecutorBenchmark.processExecutorWithRandomNonExistingCommand  avgt   40  1.459 ± 0.020  ms/op
```

## Contributing

When adding new benchmark results:

1. **System Info**: Include exact CPU model, RAM amount, storage type
2. **OS Version**: Specific OS version and kernel version for Linux
3. **JVM Version**: Exact JVM version and vendor

### Result Format Template

```
##### [CPU Model] @ [Base Clock] - [RAM Amount] - [Storage Type]
# Test Date: YYYY-MM-DD
# VM: [Version and Vendor]
# VM options: [-Xmx...]
# Additional Notes: [Any relevant system configuration]
```