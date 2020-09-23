package com.example.democards;

import org.springframework.boot.SpringApplication;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PokerApplication {

    static final Color SELECTEDRGB = new Color(120, 120, 120);
    static final Integer selected = SELECTEDRGB.getRGB();
    static final Integer white = -1;
    static final int cardWidth = 72;
    static final int fieldUp = 590;
    static final int fieldLeft = 147;

    public static void main(String[] args) {
        SpringApplication.run(PokerApplication.class, args);
        for (String path : listFiles(args[0]))
            try {
                readDataFile(new File(path));
            } catch (IOException e) {
                System.out.println("File error " + path);
            }
    }

    static void readDataFile(File file) throws IOException {
        BufferedImage img = ImageIO.read(file); // 636:1166
        StringBuilder cards = new StringBuilder();
        for (int i = 0; i < 5; i++)
            cards.append(getSuit(img, fieldLeft + 20 + cardWidth * i, fieldUp + 43))
                .append(getValue(img, i, fieldLeft + cardWidth * i, fieldUp, 33, 26));
        System.out.println(file.getName() + " " + cards.toString());
    }

    static Set<String> listFiles(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getAbsolutePath)
                .collect(Collectors.toSet());
    }

    static String getValue(BufferedImage img, int pos, int x, int y, int w, int h) {
        Color c = new Color(img.getRGB(x + w, y + 1));
        if (!c.equals(Color.WHITE) && !c.equals(SELECTEDRGB)) return "";
        int sharp = 0;
        int fuzzy = 0;
        int j = 0;
        int[] res = new int[h + 1];
        int[] data = img.getRGB(x, y, w, h, null, 0, w);
        for (int i = 0; i < data.length; i++) {
            if ((i % w) == 0) j++;
            if (data[i] == white || data[i] == selected) continue;
            if (checkArea(img, white, x + i % w, y + j)
                || checkArea(img, selected, x + i % w, y + j)) fuzzy++;
            else sharp++;
            res[j]++;
        }
        return getRank(pos, fuzzy, sharp, res[11] + res[12] + res[13] + res[14]);
    }

    private static boolean checkArea(BufferedImage img, int color, int x, int y) {
        return (img.getRGB(x + 1, y) == color || img.getRGB(x - 1, y) == color
                || img.getRGB(x, y + 1) == color || img.getRGB(x, y - 1) == color
                || img.getRGB(x + 1, y + 1) == color
                || img.getRGB(x - 1, y - 1) == color
                || img.getRGB(x + 1, y - 1) == color
                || img.getRGB(x - 1, y + 1) == color);
    }

    static String getSuit(BufferedImage img, int x, int y) {
        Color c = new Color(img.getRGB(x + 1, y + 1));
        if (!c.equals(Color.WHITE) && !c.equals(SELECTEDRGB)) return "";
        int[] data = img.getRGB(x, y, 35, 34, null, 0, 35);
        int criteria = 0;
        for (int datum : data) if (datum == white || datum == selected) criteria++;
        if (640 < criteria && criteria < 660) return "d";
        if (590 < criteria && criteria < 610) return "h";
        if (560 < criteria && criteria < 580) return "s";
        if (530 < criteria && criteria <= 560) return "c";
        return " ";
    }

    static String getRank(int pos, int fuzzy, int sharp, int a235) {
        int s = sharp + fuzzy;
        if (100 <= fuzzy && fuzzy <= 109) return "4";
        if (269 <= s && s <= 279)
            return (pos == 0 && fuzzy == 137) ? "9" : "6";
        if (pos == 0 && fuzzy == 136) return "6";
        if (167 <= s && s <= 177) return "7";
        if (144 <= fuzzy && fuzzy <= 149) return "8";
        if (280 <= s && s <= 289) return "9";
        if (380 <= s && s <= 406) return "10";
        if (153 <= s && s <= 161) return "J";
        if (336 <= s && s <= 346) return "Q";
        if (pos == 0 && s == 249) return "K";
        if (pos == 1 && s == 244) return "K";
        if (pos == 2 && s == 251) return "K";
        if (248 <= s && s <= 255) return "A";
        if (135 <= fuzzy && fuzzy <= 137) return "K";
        if (120 <= fuzzy && fuzzy <= 130)
            return a235 < 30 ? "2" : a235 < 50 ? "3" : a235 < 70 ? "5" : " ";
        return " ";
    }

}
