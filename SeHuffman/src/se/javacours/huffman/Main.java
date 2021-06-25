package se.javacours.huffman;

import java.io.FileWriter;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {

        Scanner InPut = new Scanner(System.in);
        System.out.print("Введіть текст, що перетворится за алгоритмом Хаффмана:");
        String text = InPut.nextLine();

        /*
        Some Text  :
        Навіть розумна людина буде дурною, якщо не буде саморозвиватися
        Даже разумный человек будет глуп, если не будет воспитывать себя.
        "Even a reasonable person will be stupid if he does not cultivate himself"
        Även en rimlig person kommer att vara dum om han inte kultiverar sig själv
        Même une personne raisonnable sera stupide si elle ne se cultive pas
        理にかなった人でも、自分を育てなければバカになります
        אפילו אַ גלייַך מענטש וועט זיין נאַריש אויב ער איז ניט קאַלטיווייטיד זיך

        я знаю що я нічого не знаю але інші не знають і цього"
        творите ценность - создавая дефицит
        "where there's a will there's a way";
         */

        //Створення файлу під назвою HafmanFile.txt  файл ствоюється вкорні проекту src
        try {
            FileWriter writer = new FileWriter("HafmanFile.txt");

            // Обчислюємо частоти символів у тексті
            // ( TreeMap ключь , countFrequency скількиразів у тексті )
            TreeMap<Character, Integer> frequencies = countFrequency(text);

            // генерируємо список листів дерева
            ArrayList<CodeTreeNode> codeTreeNodes = new ArrayList<>();
            for (Character c : frequencies.keySet()) {
                codeTreeNodes.add(new CodeTreeNode(c, frequencies.get(c)));
            }

            // Будуємо кодовое дерево за допомогою алгоритма Хаффмана
            CodeTreeNode tree = huffman(codeTreeNodes);

            // генерируємо таблицю префіксних кодів для кодуємих символів за допомогою дерева
            TreeMap<Character, String> codes = new TreeMap<>();
            for (Character c : frequencies.keySet()) {
                codes.put(c, tree.getCodeForCharacter(c, ""));
            }

            System.out.println("Таблицю префіксних кодів: " + codes.toString());
            writer.write("Таблицю префіксних кодів: " + codes.toString()+"\n" );

            // кодуруєм текст, заміняємо сиволи двійковими кодами
            StringBuilder encoded = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                encoded.append(codes.get(text.charAt(i)));
            }
            // Запис інформації з строки
            writer.write("Введіть текст, що перетворится за алгоритмом Хаффмана: " + text + "\n");

            writer.write("Размір початкового рядка : " + text.getBytes().length * 8 + " біт"+"\n");
            System.out.println("Размір початкового рядка : " + text.getBytes().length * 8 + " біт");

            writer.write("Розмір стислій рядки: " + encoded.length() + " біт"+"\n");
            System.out.println("Розмір стислій рядки: " + encoded.length() + " біт");

            //Створення файлу під назвою CodingFile.txt файл ствоюється вкорні проекту src
            try {
                FileWriter writerHUF = new FileWriter("CodingFile.txt");
                // Запис до файлу кодованого рдка
                writerHUF.write(String.valueOf(encoded));
                // Виводим до консолі кодований рядок
                System.out.println("Біти стислого рядка: " + encoded);
                // Закриваємо поток
                writerHUF.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            // декодуємостислу інформацію назад
            String decoded = huffmanDecode(encoded.toString(), tree);

            System.out.println("Розшифровано: " + decoded);
            writer.write("Розшифровано: " + decoded+"\n");
            // Закриваємо поток
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //функція рахує скільки разів зустрічається символ строкі
    public static TreeMap<Character, Integer> countFrequency(String text) {
        TreeMap<Character, Integer> freqMap = new TreeMap<>();
        for (int i = 0; i < text.length(); i++) {
            Character c = text.charAt(i);
            Integer count = freqMap.get(c);
            freqMap.put(c, count != null ? count + 1 : 1);
        }
        return freqMap;
    }

    // реаоізація алг Хаффмана в вигляді функції
    // приймає список параметрів , а вузлів вертає дерево
    public static CodeTreeNode huffman(ArrayList<CodeTreeNode> codeTreeNodes) {
        while (codeTreeNodes.size() > 1) {
            Collections.sort(codeTreeNodes); // впорядкування по вагам
            CodeTreeNode left = codeTreeNodes.remove(codeTreeNodes.size() - 1); // отримаємо видаляємо
            CodeTreeNode right = codeTreeNodes.remove(codeTreeNodes.size() - 1);
            // Вузел посередник (Вузел корінь) має вагу лівого та правого
            CodeTreeNode parent = new CodeTreeNode(null, right.weight + left.weight, left, right);
            // Кладемо вузел в масив
            codeTreeNodes.add(parent);
        }
        return codeTreeNodes.get(0);
    }

    public static String huffmanDecode(String encoded, CodeTreeNode tree) {
        StringBuilder decoded = new StringBuilder();

        CodeTreeNode node = tree;
        for (int i = 0; i < encoded.length(); i++) {
            node = encoded.charAt(i) == '0' ? node.left : node.right;
            if (node.content != null) {
                decoded.append(node.content);
                node = tree;
            }
        }
        return decoded.toString();
    }

    // клас для подання кодового дерева (двійкового)
    // будемо сортурувати
    public static class CodeTreeNode implements Comparable<CodeTreeNode> {

        Character content; // символ
        int weight; // вага
        CodeTreeNode left;  // бінарне дерево лівий нащадок
        CodeTreeNode right; // бінарне дерево правий  нащадок

        // конструктор вузлів
        public CodeTreeNode(Character content, int weight) {
            this.content = content;
            this.weight = weight;
        }

        public CodeTreeNode(Character content, int weight, CodeTreeNode left, CodeTreeNode right) {
            this.content = content;
            this.weight = weight;
            this.left = left;
            this.right = right;
        }

        // Вираховуємо вагу дерева (для сорт по зменшеню)
        @Override
        public int compareTo(CodeTreeNode o) {
            return o.weight - weight;
        }

        // извлечение кода для символа
        public String getCodeForCharacter(Character ch, String parentPath) {
            if (content == ch) {
                return parentPath;
            } else {
                if (left != null) {
                    String path = left.getCodeForCharacter(ch, parentPath + 0);
                    if (path != null) {
                        return path;
                    }
                }
                if (right != null) {
                    return right.getCodeForCharacter(ch, parentPath + 1);
                }
            }
            return null;
        }
    }

    // клас що реалізує бітовий масив
    public static class BitArray {
        int size;
        byte[] bytes;

        private final byte[] masks = new byte[]{0b00000001, 0b00000010, 0b00000100, 0b00001000,
                0b00010000, 0b00100000, 0b01000000, (byte) 0b10000000};

        public BitArray(int size) {
            this.size = size;
            int sizeInBytes = size / 8;
            if (size % 8 > 0) {
                sizeInBytes = sizeInBytes + 1;
            }
            bytes = new byte[sizeInBytes];
        }

        public BitArray(int size, byte[] bytes) {
            this.size = size;
            this.bytes = bytes;
        }

        public int get(int index) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            return (bytes[byteIndex] & masks[bitIndex]) != 0 ? 1 : 0;
        }

        public void set(int index, int value) {
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            if (value != 0) {
                bytes[byteIndex] = (byte) (bytes[byteIndex] | masks[bitIndex]);
            } else {
                bytes[byteIndex] = (byte) (bytes[byteIndex] & ~masks[bitIndex]);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append(get(i) > 0 ? '1' : '0');
            }
            return sb.toString();
        }

    }
}