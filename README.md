# Javalution

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=flat&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-23-blue?style=flat&logo=javafx&logoColor=white)](https://openjfx.io/)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.1-02303A?style=flat&logo=gradle&logoColor=white)](https://gradle.org/)

Javalution is a high-performance Conway’s Game of Life simulator featuring a chunk-based grid for virtually infinite worlds, smooth zoom/pan rendering, and RLE pattern support.

## Key Features
- **Massive Grid:** Efficient chunk-based storage handles extremely large populations.
- **Fluid Rendering:** JavaFX Canvas with live zoom, panning, and grid toggling.
- **Multithreaded Engine:** Parallel generation updates for high-speed simulation.
- **RLE Support:** Load and parse standard Run-Length Encoded patterns.

## Controls
| Action | Input |
| :--- | :--- |
| **Draw/Place Cell** | Left Click / Drag |
| **Erase Cell** | Right Click / Drag |
| **Pan Camera** | Middle Click + Drag |
| **Zoom** | Scroll Wheel |
| **Play/Pause** | `Space` |
| **Step Forward** | `S` |
| **Clear Grid** | `C` |
| **Randomize** | `R` |

## Getting Started

### Prerequisites
- Java 25 (OpenJDK)
- Internet connection (for first-time Gradle/JavaFX dependency download)

### Build and Run
1. **Clone the repository:**
   ```bash
   git clone https://github.com/maskedsyntax/javalution.git
   cd javalution
   ```
2. **Launch the application:**
   ```bash
   ./gradlew run
   ```
3. **Build a JAR:**
   ```bash
   ./gradlew build
   ```
   The runnable JAR will be located in `build/libs/`.
