import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReadAndExportFilesByDay {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static void main(String[] args) {
        System.out.println(" -- Danh sach tin nhan trong thu muc Input sap xep theo thu tu thoi gian tang dan la: --\n");
        System.out.println(listAllMessageSortByTimeASC());
        // Ghi file
        writeMessageByDate();
    }

    public static StringBuffer readFile(String url) {
        StringBuffer result = new StringBuffer();
        try {
            FileReader fileReader = new FileReader(url);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }

                result.append(line);
                result.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static StringBuffer readAllFileInputFolder() {
        StringBuffer result = new StringBuffer();
        File folderInput = new File("/home/dinhngocdinh/Downloads/Collections 2/input/");
        File[] folderInputArray = folderInput.listFiles();
        if (folderInputArray != null) {
            for (File subFolderInput : folderInputArray) {
                StringBuffer resultEachFile = readFile(subFolderInput.toString());
                result.append(resultEachFile);
            }
        }
        return result;
    }

    public static StringBuffer listAllMessageSortByTimeASC() {
        StringBuffer result = new StringBuffer();
        StringBuffer listAllMessage = readAllFileInputFolder();
        TreeMap<Date, String> listTimeAndMessage = new TreeMap<>();
        for (String lineListAllMessage : listAllMessage.toString().split("\n")) {
            int indexSeparate = lineListAllMessage.indexOf("|");
            String timeString = lineListAllMessage.substring(indexSeparate + 1);
            Date stringToDate = null;
            try {
                stringToDate = simpleDateFormat.parse(timeString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            listTimeAndMessage.put(stringToDate, lineListAllMessage);
        }
        Set<Date> keySet = listTimeAndMessage.keySet();
        for (Date key : keySet) {
            result.append(listTimeAndMessage.get(key));
            result.append("\n");
        }
        return result;
    }

    public static void writeFile(String outPutFileName, StringBuffer content) {
        try {
            FileWriter fileWriter = new FileWriter(outPutFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content.toString());
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ghi that bai!");
        }
    }

    public static Set<String> listDate() {
        Set<String> listDate = new TreeSet<>();
        StringBuffer listAllMessageSortByTimeASC = listAllMessageSortByTimeASC();
        for (String lineListAllMessageSortByTimeASC : listAllMessageSortByTimeASC.toString().split("\n")) {
            int indexSeparate = lineListAllMessageSortByTimeASC.indexOf("|");
            int indexLastSpace = lineListAllMessageSortByTimeASC.lastIndexOf(" ");
            String date = lineListAllMessageSortByTimeASC.substring(indexSeparate + 1, indexLastSpace);
            listDate.add(date);
        }
        return listDate;
    }

    public static TreeMap<String, StringBuffer> listMessageByPhoneNumber() {
        TreeMap<String, StringBuffer> listMessageByPhoneNumber = new TreeMap<>();
        File folderInput = new File("/home/dinhngocdinh/Downloads/Collections 2/input/");
        File[] folderInputArray = folderInput.listFiles();
        if (folderInputArray != null) {
            for (File subFolderInput : folderInputArray) {
                int indexSlash = subFolderInput.toString().lastIndexOf("/");
                int indexDot = subFolderInput.toString().indexOf(".");
                String phoneNumber = subFolderInput.toString().substring(indexSlash + 1, indexDot);
                StringBuffer resultEachFileByPhoneNumber = readFile(subFolderInput.toString());
                listMessageByPhoneNumber.put(phoneNumber, resultEachFileByPhoneNumber);
            }
        }
        return listMessageByPhoneNumber;
    }

    public static void writeMessageByDate() {
        TreeMap<String, StringBuffer> listMessageByPhoneNumber = listMessageByPhoneNumber();
        Set<String> phoneNumbers = listMessageByPhoneNumber.keySet();
        Set<String> listDate = listDate();
        for (String lineListDate : listDate) {
            StringBuffer listMessageFilterByDate = new StringBuffer();
            for (String phoneNumber : phoneNumbers) {
                for (String line : listMessageByPhoneNumber.get(phoneNumber).toString().split("\n")) {
                    if (line.contains(lineListDate)) {
                        int indexSeparate = line.indexOf("|");
                        String content = line.substring(0, indexSeparate);
                        String timeString = line.substring(indexSeparate + 1);
                        String messageContentRecorded = timeString + "|" + phoneNumber + "|" + content;
                        listMessageFilterByDate.append(messageContentRecorded);
                        listMessageFilterByDate.append("\n");
                    }
                }
            }
            String fileName = lineListDate.replace("/", "");
            writeFile("./output/" + fileName + ".txt", listMessageFilterByDate);
        }
    }

}
