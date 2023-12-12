package ru.des.file.manager;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class filemanagerapp {
    private static final Scanner SCANNER = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        var startPath =args.length > 0 ? args[0] : ".";
        var startLocation = new File(startPath).getCanonicalPath();
        var command = getCommand(startLocation);
        while (command[0] != "/exit"){
            switch (command[0]) {
                case "/help" -> System.out.println(helpCommand());
                case "/mkdir" -> {
                    var directoryName =command[1];
                    System.out.println(mkdirCommand(startLocation, directoryName));
                }
                case "/delete" -> {
                    var directoryName =command[1];
                    System.out.println(deleteCommand(startLocation, directoryName));
                }
                case "/ls" -> System.out.println(lsCommand(startLocation));
                case "/cd" -> {
                    var neededpath= command[1];
                    startLocation =cdCommand(startLocation, neededpath);
                }
                case "/info" -> {
                    System.out.println(infoCommand(startLocation));
                }
                case "/copy" -> {
                    var from = command[1];
                    var to = command[2];
                    copyCommand(startLocation,from, to);
                }
                case "/rename" ->{
                    var filename = command[1];
                    var newfilename = command[2];
                    renameCommand(startLocation, filename, newfilename);
                }
            }
            command = getCommand(startLocation);
        }
    }

    private static void renameCommand(String startLocation, String filename, String newfilename) {
        File f = new File(startLocation, filename);


        if (f.renameTo(new File(startLocation, newfilename))) {
            System.out.println("File is renamed");
        }
        else {
            System.out.println("File cannot be renamed");
        }
    }

    private static void copyCommand(String startLocation, String from, String to) {
        var Pathfrom = Paths.get(startLocation, from);
        var Pathto = Paths.get(startLocation, to);
        try{
            Files.copy(Pathfrom, Pathto, StandardCopyOption.REPLACE_EXISTING);

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    private static String infoCommand(String startLocation) {
        File file = new File(startLocation);
        var infoMap = new LinkedHashMap<String, Object>();
        try{
            infoMap.put("isExist: ", file.exists());
            infoMap.put("name: ", file.getName());
            infoMap.put("path: ", file.getAbsolutePath());
            infoMap.put("CannoicalPath: ", file.getCanonicalPath());
            infoMap.put("isDirectory: ", file.isDirectory());
            infoMap.put("isFile: ", file.isFile());
            var attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            infoMap.put("isSymboliclink: ", attributes.isSymbolicLink());
            infoMap.put("CreationTime: ", attributes.creationTime());
            infoMap.put("size: ", file.length());
            infoMap.put("canRead: ", file.canRead());
            infoMap.put("canWrite: ", file.canWrite());


        }
        catch (Exception ex){
        System.err.println(ex);
        }
        return infoMap.entrySet().stream().map(it -> it.getKey() + it.getValue()).collect(Collectors.joining("\n"));
    }

    private static String cdCommand(String startLocation, String neededpath) throws IOException {
       return new File(startLocation, neededpath).getCanonicalPath();
    }

    private static boolean deleteCommand(String startLocation, String directoryName) {
        File file = new File(startLocation + File.separator + directoryName);
        return file.delete();
    }

    private static List<String> lsCommand(String startLocation) {
        File file = new File(startLocation);
        return Arrays.stream(Objects.requireNonNull(file.listFiles())).map(File::getName).toList();
    }

    private static boolean mkdirCommand(String startLocation, String directoryName) {
        File file = new File(startLocation + File.separator + directoryName);
        return file.mkdir();
    }

    private static String helpCommand() {
        return
                """
                /mkdir - Создает новую папку
                /cd - переходит в новую папку
                /info - Выводит информацию о папке и файле
                /rename - Переименовать файл
                /delete - Удалить файл
                /copy - Копировать файл
                /ls - возвращает список файлов в директории
                """.stripIndent();

    }

    private static String[] getCommand(String startLocation){
        System.out.print(startLocation + ": ");
        return SCANNER.nextLine().split(" ");
    }
}
