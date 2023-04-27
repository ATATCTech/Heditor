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

```shell
heditor extract {PATH_TO_YOUR_PYTHON_PROJECT} in Python
```

To save the output as a single `.hexpr` file:

```shell
heditor extract {PATH_TO_YOUR_PYTHON_PROJECT} in Python to example.hepxr
```

To save the output as a file tree:

```shell
heditor extract {PATH_TO_YOUR_PYTHON_PROJECT} in Python to example
```

Heditor recognizes the docstrings as Markdown by default. To specify the comment type, use `as ...`.

```shell
heditor extract {PATH_TO_YOUR_PYTHON_PROJECT} in Python as HTML to example
```

### Read

The following example shows how to open a `.hexpr` file.

```shell
heditor read example.hexpr
```

### Initialize

Hephaestus imports and exports in a strange way by default considering compatibility. Normally structured documents cannot be read directly. To construct from original directories, you can use `initialize` command.

```shell
heditor initialize example
```

### Debug

Add `--debug` anywhere after the third argument to enable debug mode. This gives you a full stack trace that indicates where the error occurs. Report the bug with the command leading to the error and the stack trace.
