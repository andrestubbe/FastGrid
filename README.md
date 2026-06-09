# FastGrid 0.1.0 [ALPHA] — Ultra-Fast Zero-Allocation Layout Engine

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastGrid/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastGrid)

---

**⚡ High-performance multi-item layout engine for the FastJava ecosystem.**

**FastGrid** provides pure math pipelines for computing Grid, Masonry, and Gallery layouts. It operates entirely on `float[]` arrays to achieve **true zero-allocation** during layout passes, completely eliminating Garbage Collection stutter during high-FPS animations and window resizing.
[**Watch the Demo**](https://youtu.be/mk-N_5ywSCU)

[![FastGrid Showcase](docs/screenshot.png)](https://youtu.be/mk-N_5ywSCU)

---

## Zero-Allocation Architecture

Standard layout engines (like AWT LayoutManagers, JavaFX, or Swing) instantiate objects (like `Rectangle`, `Cell`, `Bounds`) for every item during every layout pass. When calculating dynamic layouts at 60+ FPS, this creates immense garbage collection pressure.

FastGrid takes a different approach:
- **No Objects:** It reads from a flat input array of aspect ratios and writes to a pre-allocated flat `float[]` output array.
- **Pure Math:** Decoupled from rendering APIs. It computes the math and returns the raw coordinates `[x1, y1, w1, h1, x2, y2, w2, h2, ...]`.
- **Cache Locality:** By packing everything tightly into primitive arrays, it achieves insane throughput by leveraging CPU cache lines.

---

## Installation (JitPack)

```xml
<dependency>
    <groupId>com.github.andrestubbe</groupId>
    <artifactId>fastgrid</artifactId>
    <version>0.1.0</version>
</dependency>
```

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
