package com.atatctech.heditor;

import com.atatctech.heditor.pattern.CommentExtractor;
import com.atatctech.heditor.pattern.Styler;
import com.atatctech.heditor.pattern.Type;
import com.atatctech.hephaestus.component.*;
import com.atatctech.hephaestus.export.fs.ComponentFolder;
import com.atatctech.packages.basics.Basics;

import java.io.File;
import java.util.Objects;

public final class Heditor {
    public static final CommentExtractor JAVA = (skeleton, context, type, styler) -> {
        Skeleton currentBranch = skeleton;
        int classDeclarationStartsAt = -1, JavadocEndsAt = -1;
        String javadocBuffer = null;
        for (int i = 0; i < context.length(); i++) {
            char c = context.charAt(i);
            if (context.startsWith("class", i)) {
                classDeclarationStartsAt = i += 5;
            }
            else if (context.startsWith("/**", i)) {
                if ((JavadocEndsAt = context.indexOf("*/", i += 3)) < 0) break;
                javadocBuffer = context.substring(i, JavadocEndsAt);
                i = JavadocEndsAt;
            }
            else if (javadocBuffer != null && (c == '{' || c == '=')) {
                Text.IndexPair indexPair;
                if (classDeclarationStartsAt > 0) {
                    if ((indexPair = Utils.getClassName(context, classDeclarationStartsAt)) == null) break;
                    classDeclarationStartsAt = -1;
                } else if ((indexPair = Utils.getMethodName(context, JavadocEndsAt, i)) == null && (indexPair = Utils.getFieldName(context, JavadocEndsAt, i + 1)) == null) continue;
                Skeleton newSkeleton = new Skeleton(context.substring(indexPair.start(), indexPair.end()));
                newSkeleton.setComponent(Utils.text2component(styler.transform(newSkeleton, Utils.removeRedundantCharactersByLines(javadocBuffer, '*', ' '), type), type));
                currentBranch.appendChild(newSkeleton);
                currentBranch = newSkeleton;
                javadocBuffer = null;
            } else if (c == '}') currentBranch = Objects.requireNonNullElse(currentBranch.getParent(), currentBranch);
        }
        return skeleton;
    };

    public static final CommentExtractor PYTHON = (skeleton, context, type, styler) -> {
        PythonTarget currentBranch = (PythonTarget) skeleton;
        String lineSeparator = System.lineSeparator();
        int currentIndentation = 0, declarationStartsAt = -1;
        boolean newLine = false, declarationIsClass = true;
        for (int i = 0; i < context.length(); i++) {
            char c = context.charAt(i);
            if (context.startsWith(lineSeparator, i)) {
                currentIndentation = 0;
                newLine = true;
            } else if (newLine) {
                if (c == ' ') currentIndentation++;
                else {
                    newLine = false;
                    PythonTarget parent = currentBranch.getParent();
                    if (currentIndentation == parent.getIndentation()) currentBranch = parent;
                }
            } else {
                if (c == ':' && declarationStartsAt > 0) {
                    Text.IndexPair indexPair = declarationIsClass ? Utils.getClassName(context, declarationStartsAt, i) : Utils.getMethodName(context, declarationStartsAt, i);
                    if (indexPair == null) {
                        declarationStartsAt = -1;
                        continue;
                    }
                    PythonTarget newSkeleton = new PythonTarget(context.substring(indexPair.start(), indexPair.end()), currentIndentation);
                    currentBranch.appendChild(newSkeleton);
                    currentBranch = newSkeleton;
                } else if (context.startsWith("class", i)) {
                    declarationStartsAt = i += 5;
                    declarationIsClass = true;
                } else if (context.startsWith("def", i)) {
                    declarationStartsAt = i += 3;
                    declarationIsClass = false;
                } else if (context.startsWith("\"\"\"", i)) {
                    int docstringEndsAt = context.indexOf("\"\"\"", i += 3);
                    currentBranch.setComponent(Utils.text2component(styler.transform(currentBranch, Utils.removeRedundantCharactersByLines(context.substring(i, docstringEndsAt), ' '), type), type));
                    i = docstringEndsAt;
                }
            }
        }
        return skeleton;
    };

    private static final String HELP_TEXT = """
            extract [language] [target]
                    (--type=[comment type])
                    (--wrapperfile=[custom wrapper file])
                    ([output path])
                language: Java | Python
                target: path to the target file
                comment type:
                    md -> Markdown  *DEFAULT
                    html -> HTML
                    p -> plain text
                custom wrapper file: path to the wrapper file, `.wrapper` by default
                output path: path to output
                    By filling this argument, Heditor will save the result into a file.
                    If path ends with ".hexpr", the result will be saved in form of Hexpr.
                    If not, the result will be unpacked into directories and files.
            """;

    public static void main(String[] args) {
        try {
            int lengthOfArgs = args.length;
            if (lengthOfArgs < 2) {
                if (args[0].equals("help")) {
                    System.out.println(HELP_TEXT);
                    return;
                }
                throw new IllegalArgumentException("Must have at least 3 arguments.");
            }
            String command = args[0].toLowerCase();
            File file = new File(args[2]);
            String language = args[1].toLowerCase();
            CommentExtractor commentExtractor;
            Type type = Type.MARKDOWN;
            Styler styler = new Styler();
            File outputFile = null;
            File wrapperFile = null;
            switch (language) {
                case "java" -> commentExtractor = JAVA;
                case "python" -> commentExtractor = PYTHON;
                default -> throw new IllegalStateException("Unexpected language: \"" + language + "\".");
            }
            for (int i = 3; i < lengthOfArgs; i++) {
                String arg = args[i];
                if (arg.startsWith("--type=")) {
                    String typeString = arg.substring(7);
                    type = switch (typeString) {
                        case "md" -> Type.MARKDOWN;
                        case "html" -> Type.HTML;
                        case "p" -> Type.PLAIN_TEXT;
                        default -> throw new IllegalStateException("Unknown type: \"" + typeString + "\".");
                    };
                } else if (arg.startsWith("--wrapperfile=")) {
                    wrapperFile = new File(arg.substring(14));
                } else if (arg.startsWith("--styler=")) {
                    styler = switch (Integer.parseInt(arg.substring(9))) {
                        case 0 -> new Styler() {
                            @Override
                            public Text transform(Skeleton currentContainer, String comment, Type type) {
                                return super.transform(currentContainer, comment
                                        .replaceAll("@param", "<b style='font-size:10px;color:orange;'>PARAM</b>")
                                        .replaceAll("@return", "<b style='font-size:10px;color:green;'>RETURN</b>")
                                        , type);
                            }
                        };
                        case 1 -> new Styler() {
                            @Override
                            public Text transform(Skeleton currentContainer, String comment, Type type) {
                                return super.transform(currentContainer, comment
                                        .replaceAll(":param", "<b style='font-size:10px;color:orange;'>PARAM</b>")
                                        .replaceAll(":return", "<b style='font-size:10px;color:green;'>RETURN</b>")
                                        , type);
                            }
                        };
                        default ->
                                throw new IllegalStateException("Unexpected value: " + Integer.valueOf(arg.substring(9)));
                    };
                } else {
                    outputFile = new File(arg);
                }
            }
            switch (command) {
                case "extract" -> {
                    Skeleton skeleton = Utils.extract(file, commentExtractor, type, styler);
                    if (outputFile == null) System.out.println(skeleton);
                    else if (outputFile.getName().endsWith(".hexpr")) Basics.NativeHandler.writeFile(outputFile, skeleton.expr());
                    else
                        (wrapperFile == null ? new ComponentFolder(skeleton) : new ComponentFolder(skeleton, wrapperFile)).write(outputFile + "/" + skeleton.getName());
                }
                case "inject" -> throw new UnsupportedOperationException("Injection not supported yet.");
                default -> System.out.println("WARNING: Unrecognized command: `" + command + "`.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            System.out.println("Use `heditor help` to learn more.");
            throw new RuntimeException(e);
        }
    }
}
