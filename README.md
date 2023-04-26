# Heditor

Heditor is a powerful tool to manage documentation. It supports extracting docstrings from the source code.

## Installation

Download from [releases](https://github.com/ATATCTech/Heditor/releases).

The Windows executable installer will add the installation directory into the environment variable `Path`.

## Usage

```shell
heditor help
```

### Extract

The following example shows how to extract docstrings from a Python project.

```
heditor extract Python {PATH_TO_YOUR_PYTHON_PROJECT}
```

To save the output as a single `.hexpr` file:

```shell
heditor extract Python {PATH_TO_YOUR_PYTHON_PROJECT} example.hepxr
```

To save the output as a file tree:

```shell
heditor extract Python {PATH_TO_YOUR_PYTHON_PROJECT} example
```

### Read

The following example shows how to open a `.hexpr` file.

```shell
heditor read _ example.hexpr
```

### Debug

Add --debug anywhere after the third argument to enable debug mode. This gives you a full stack trace that indicates where the error occurs. Report the bug with the command leading to the error and the stack trace.

## Issues