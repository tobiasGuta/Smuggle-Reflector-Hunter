# Smuggle & Reflector Hunter

**A Burp Suite Extension for Passive Vulnerability Detection**

![Java](https://img.shields.io/badge/Java-21-orange)
![Burp Suite](https://img.shields.io/badge/Burp%20Suite-Montoya%20API-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## Overview
**Smuggle & Reflector Hunter** is a lightweight Burp Suite extension designed for **Community Edition** users. It acts as a passive assistant, analyzing HTTP traffic in real-time to flag potential **HTTP Request Smuggling**, **Cache Poisoning**, and **Reflected XSS** vectors without sending active attack traffic.

It utilizes the modern **Montoya API** to integrate directly into the Burp Proxy, providing immediate visual feedback via color-coded highlighting in the HTTP History tab.

## Features

### 1. The "Leaky Header" Spotter (🟧 Orange Highlight)
The extension scans every HTTP response for specific headers that reveal backend architecture or caching mechanisms. If found, the row is highlighted **ORANGE**.

**Detected Headers & Attack Vectors:**
* `Transfer-Encoding`: Critical indicator for **HTTP Request Smuggling (CL.TE/TE.CL)**.
* `Age` / `X-Cache` / `Via`: Indicators of caching servers (Varnish, Akamai), useful for **Cache Poisoning**.
* `X-Forwarded-Host` / `X-Forwarded-Proto`: Useful for **SSRF** and **Cache Deception**.
* `X-Original-URL` / `X-Rewrite-URL`: Indicators of framework routing that may allow **Access Control Bypasses**.
* `Upgrade`: Checks for `h2c` (HTTP/2 over Cleartext) tunneling opportunities.

### 2. The Reflection Detector (🟦 Blue Highlight)
The extension analyzes URL Query Parameters from the request and checks if they appear verbatim in the response body. If a match is found (and is >4 characters), the row is highlighted **BLUE**.

* **Goal:** Quickly identify **Reflected XSS** (Cross-Site Scripting) or **HTML Injection** entry points.
* **Logic:** Matches `Request Input` -> `Response Output` automatically.

## Installation

### Prerequisites
* Java Development Kit (JDK) 21 or higher.
* Burp Suite (Community or Professional) v2022.9+.
* Gradle.

### Build from Source
1.  Clone this repository:
    ```bash
    git clone https://github.com/tobiasGuta/Smuggle-Reflector-Hunter.git
    cd Smuggle-Reflector-Hunter
    ```
2.  Build the JAR file using Gradle:
    ```bash
    # On Linux/Mac
    ./gradlew clean jar

    # On Windows
    gradlew.bat clean jar
    ```
3.  The extension file will be generated at: `build/libs/SmuggleReflector.jar`

### Load into Burp Suite
1.  Open Burp Suite.
2.  Navigate to **Extensions** -> **Installed**.
3.  Click **Add**.
4.  Select **Extension Type:** Java.
5.  Select the `SmuggleReflector.jar` file you just built.
6.  You should see the output: *"Extension Loaded! Watching for Leaky Headers and Reflections..."*

## Usage Guide

Once loaded, simply browse your target website using Burp's embedded browser. Watch the **HTTP History** tab in the **Proxy**.

| Color | Meaning | Suggested Manual Testing |
| :--- | :--- | :--- |
| **🟧 ORANGE** | **Interesting Header Found** | Check the "Notes" column. If `Age` is present, test for Cache Poisoning. If `Transfer-Encoding` is present, check for CL.TE discrepancies. |
| **🟦 BLUE** | **Input Reflected** | The parameter mentioned in the "Notes" column is reflected in the HTML. Send to **Repeater** and attempt XSS payloads (e.g., `<script>alert(1)</script>`). |

🟦 BLUE

<img width="1912" height="886" alt="image" src="https://github.com/user-attachments/assets/029c039c-8a86-465f-bba4-cb25e13d8d37" />

🟧 ORANGE

<img width="1913" height="882" alt="image" src="https://github.com/user-attachments/assets/7448b447-a4d6-44fb-9c53-c8820d9d766a" />


## Tech Stack
* **Language:** Java (JDK 21)
* **API:** Burp Suite Montoya API (2023.12.1)
* **Build System:** Gradle

## Disclaimer
This tool is for educational purposes and authorized security testing only. Do not use this tool on systems you do not have permission to test. The author is not responsible for any misuse.

# Support
If my tool helped you land a bug bounty, consider buying me a coffee ☕️ as a small thank-you! Everything I build is free, but a little support helps me keep improving and creating more cool stuff ❤️
---

<div align="center">
  <h3>☕ Support My Journey</h3>
</div>


<div align="center">
  <a href="https://www.buymeacoffee.com/tobiasguta">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" width="200" />
  </a>
</div>
