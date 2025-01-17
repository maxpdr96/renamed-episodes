package com.hidarisoft.renamedepisodes;

import java.io.File;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenamedEpisodesApplication {

    public static void main(String[] args) {
        String directoryPath = "C:\\Users\\maxqr\\Downloads\\[1temp] - Rakshasa Street (Zhen Hun Jie)-20250104T175030Z-001\\[2temp] - Rakshasa Street (Zhen Hun Jie)";

        String[] videoExtensions = {".mp4", ".mkv", ".avi", ".mov"};

        File directory = new File(directoryPath);

        if (directory.isDirectory()) {
            File[] videoFiles = directory.listFiles((_, name) -> {
                for (String ext : videoExtensions) {
                    if (name.toLowerCase().endsWith(ext)) {
                        return true;
                    }
                }
                return false;
            });

            if (videoFiles != null && videoFiles.length > 0) {
                System.out.println("Arquivos de vídeo encontrados:");
                for (File videoFile : videoFiles) {
                    Optional<String> newFileNameOptional = matchNameAndEpisode(videoFile.getName());
                    if (newFileNameOptional.isPresent()) {
                        String newFileName = newFileNameOptional.get();
                        File newFile = new File(directory, newFileName);

                        // Renomear o arquivo
                        if (videoFile.renameTo(newFile)) {
                            System.out.println("Arquivo renomeado: " + videoFile + " -> " + newFileName);
                        } else {
                            System.out.println("Erro ao renomear: " + videoFile);
                        }
                    }

                }
            } else {
                System.out.println("Nenhum arquivo de vídeo encontrado no diretório especificado.");
            }
        } else {
            System.out.println("O caminho fornecido não é um diretório válido.");
        }


    }

    private static Optional<String> matchNameAndEpisode(String input) {
        // Regex para capturar nome, número opcional e número do episódio

        String[] regexPatterns = {
                ".*?\\[([^\\]]+?)(?: (\\d+))?\\] - Episódio (\\d+)",
                "\\[Ep\\. (\\d+)(?:-[a-zA-Z0-9]+)?\\] (.+?) - (\\d+)T"
        };

        for (int i = 0; i < regexPatterns.length; i++) {

            Pattern pattern = Pattern.compile(regexPatterns[i]);
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                String name;
                String optionalNumber;
                String episodeNumber;
                if (regexPatterns.length != i + 1) {
                    name = matcher.group(1);           // Nome do arquivo (antes do número)
                    optionalNumber = matcher.group(2); // Número opcional (pode ser nulo)
                    episodeNumber = matcher.group(3);  // Número do episódio
                } else {
                    name = matcher.group(2);           // Nome do arquivo (antes do número)
                    optionalNumber = matcher.group(3); // Número opcional (pode ser nulo)
                    episodeNumber = matcher.group(1);  // Número do episódio
                }

                System.out.println("Nome: " + name);
                System.out.println("Número Opcional: " + (optionalNumber != null ? optionalNumber : "Nenhum"));
                System.out.println("Número do Episódio: " + episodeNumber);

                String episodeNumberFormatted = String.format("%03d", Integer.parseInt(episodeNumber));
                String newFileName = name;
                if (optionalNumber != null) {
                    String optionalNumberFormatted = String.format("%02d", Integer.parseInt(optionalNumber));
                    newFileName += " - S" + optionalNumberFormatted;
                } else {
                    newFileName += " - S" + "01";
                }
                newFileName += "E" + episodeNumberFormatted + getFileExtension(input);
                System.out.printf("New name: %s", newFileName);
                return Optional.of(newFileName);

            } else {
                System.out.println("Nenhuma correspondência encontrada!");
            }
        }

        return Optional.empty();
    }

    private static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return (lastDot != -1) ? fileName.substring(lastDot) : "";
    }
}
