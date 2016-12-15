/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package net.sourceforge.marathon.resource.navigator;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.ButtonType;
import net.sourceforge.marathon.resource.ResourceView.Operation;

/**
 * Sample code that copies files in a similar manner to the cp(1) program.
 */

public class Copy {

    /**
     * Copy source file to target location.
     *
     * @return
     */
    static boolean copyFile(Path source, Path target) {
        CopyOption[] options = new CopyOption[] { COPY_ATTRIBUTES, REPLACE_EXISTING };
        target = getUnique(target);
        try {
            Files.copy(source, target, options);
            return true;
        } catch (Exception x) {
            System.err.format("Unable to copy: %s: %s%n", source, x);
            return false;
        }
    }

    private static Pattern pattern1 = Pattern.compile("(.*)_copy");
    private static Pattern pattern2 = Pattern.compile("(.*)_copy_(\\d+)");

    private static Path getUnique(Path target) {
        String fileName = target.getFileName().toString();
        String name = com.google.common.io.Files.getNameWithoutExtension(fileName);
        String ext = com.google.common.io.Files.getFileExtension(fileName);
        if (!ext.equals("")) {
            ext = "." + ext;
        }
        while (!Files.notExists(target)) {
            Matcher matcher = pattern2.matcher(name);
            if (matcher.matches()) {
                name = matcher.group(1) + "_copy_" + (Integer.parseInt(matcher.group(2)) + 1);
            } else {
                matcher = pattern1.matcher(name);
                if (matcher.matches()) {
                    name = matcher.group(1) + "_copy_2";
                } else {
                    name = name + "_copy";
                }

            }
            target = target.resolveSibling(name + ext);
        }
        return target;
    }

    /**
     * A {@code FileVisitor} that copies a file-tree ("cp -r")
     */
    static class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path target;
        private Operation operation;

        TreeCopier(Path source, Path target, Operation operation) {
            this.source = source;
            this.target = target;
            this.operation = operation;
        }

        @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            // before visiting entries in a directory we copy the directory
            // (okay if directory already exists).
            CopyOption[] options = new CopyOption[] { COPY_ATTRIBUTES };

            Path newdir = target.resolve(source.relativize(dir));
            try {
                Files.copy(dir, newdir, options);
            } catch (FileAlreadyExistsException x) {
                // ignore
            } catch (IOException x) {
                System.err.format("Unable to create: %s: %s%n", newdir, x);
                return SKIP_SUBTREE;
            }
            return CONTINUE;
        }

        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (copyFile(file, target.resolve(source.relativize(file)))) {
                if (operation == Operation.CUT) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.err.format("Unable to create: %s: %s%n", file, e);
                    }
                }
            }
            return CONTINUE;
        }

        @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            // fix up modification time of directory when done
            if (exc == null) {
                Path newdir = target.resolve(source.relativize(dir));
                try {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newdir, time);
                } catch (IOException x) {
                    System.err.format("Unable to copy all attributes to: %s: %s%n", newdir, x);
                }
                try {
                    if (operation == Operation.CUT) {
                        Files.delete(dir);
                    }
                } catch (IOException e) {
                    System.err.format("Unable to delete directory: %s: %s%n", newdir, e);
                }
            }
            return CONTINUE;
        }

        @Override public FileVisitResult visitFileFailed(Path file, IOException exc) {
            if (exc instanceof FileSystemLoopException) {
                System.err.println("cycle detected: " + file);
            } else {
                System.err.format("Unable to copy: %s: %s%n", file, exc);
            }
            return CONTINUE;
        }
    }

    static class TreeDeleter implements FileVisitor<Path> {

        private Optional<ButtonType> option;

        public TreeDeleter(Optional<ButtonType> option) {
            this.option = option;
        }

        @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }

        public Optional<ButtonType> getOption() {
            return option;
        }
    }

    public static Path copy(Path source, Path target, Operation operation) throws IOException {
        if (source.equals(target)) {
            return null;
        }
        Path dest = target.resolve(source.getFileName());
        if (operation == Operation.CUT && dest.equals(source)) {
            return null;
        }
        dest = getUnique(dest);
        // follow links when copying files
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        TreeCopier tc = new TreeCopier(source, dest, operation);
        Files.walkFileTree(source, opts, Integer.MAX_VALUE, tc);
        return dest;
    }

    public static Optional<ButtonType> delete(Path path, Optional<ButtonType> option) throws IOException {
        TreeDeleter td = new TreeDeleter(option);
        Files.walkFileTree(path, td);
        return td.getOption();
    }
}
