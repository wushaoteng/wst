package com.alibaba.middleware.race;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiunuo.zdd on 2017/4/8.
 */
public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String TEAM_COED = "71263qsvb5";


    public static void main(String[] args) throws Exception {
        String a = "12312341411231";
        Long b = Long.parseLong(a);
        System.out.println(a.getBytes().length);
        byte[] bs = long2bytes(b);
        long c = bytes2long(bs);
        System.out.println(c);
//        System.out.println(fileSplit());
    }
    public static byte[] long2bytes(long num) {
        byte[] b = new byte[8];
        for (int i=0;i<8;i++) {
            b[i] = (byte)(num>>>(56-(i*8)));
        }
        return b;
    }
    public static long bytes2long(byte[] b) {
        long temp = 0;
        long res = 0;
        for (int i=0;i<8;i++) {
            res <<= 8;
            temp = b[i] & 0xff;
            res |= temp;
        }
        return res;
    }

//    public static String[] readToString(String filePath)
//    {
//        File file = new File(filePath);
//        Long filelength = file.length(); // 获取文件长度
//        byte[] filecontent = new byte[filelength.intValue()];
//        try
//        {
//            FileInputStream in = new FileInputStream(file);
//            in.read(filecontent);
//            in.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String s = new String(filecontent);
//
//        String[] fileContentArr = s.split("\n");
//
//        return fileContentArr;// 返回文件内容,默认编码
//    }
    public static Map<Integer, Long> fileSplit2() throws Exception {
        Map<Integer, Long> zoneSizeMapping = new HashMap<Integer, Long>();
        for (int i = 0; i < 10 ; i++) {
            long startSplit = System.currentTimeMillis();
            String filePath = KNLimit.DATA_DIR + "KNLIMIT_" + i + ".data";
            BufferedReader readerZone0 = new BufferedReader(new FileReader(filePath), 4096 * 2);

            Map<Integer, DataOutputStream> zone0WriterMap = new HashMap<>();
            for (int j = 0; j < 64; j++) {
                zone0WriterMap.put(j, new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(KNLimit.MIDDLE_DIR + TEAM_COED + "/KNLIMIT_" + i + "_" + j + ".data"),4096*2)));
            }

            String readZone0NumberString = null;
            while ((readZone0NumberString = readerZone0.readLine()) != null) {
                Long readZone0Number = Long.parseLong(readZone0NumberString);
                int zoneKey = (int) (readZone0Number >>> 57);
                DataOutputStream writer = zone0WriterMap.get(zoneKey);
                writer.writeLong(readZone0Number);
                Long saveCount = zoneSizeMapping.get(zoneKey);
                if (saveCount == null || saveCount == 0) {
                    zoneSizeMapping.put(zoneKey, 1L);
                } else {
                    zoneSizeMapping.put(zoneKey, ++saveCount);
                }

            }

            readerZone0.close();

            for (DataOutputStream value : zone0WriterMap.values()) {
                value.close();
            }
            System.out.println("SplitFile " + filePath + " cost:" + (System.currentTimeMillis() - startSplit));
        }
        long startSplit = System.currentTimeMillis();
        ObjectOutputStream oos =
            new ObjectOutputStream(new FileOutputStream(KNLimit.MIDDLE_DIR + TEAM_COED + "/index.data"));
        oos.writeObject(zoneSizeMapping);
        oos.close();
        System.out.println("WriteIndex cost:" + (System.currentTimeMillis() - startSplit));
        return zoneSizeMapping;
    }


    public static Map<Integer, Long> fileSplit() throws Exception {

        Map<Integer, Long> zoneSizeMapping = new HashMap<Integer, Long>();
        for (int i = 0; i < 10; i++) {

            BufferedReader readerZone0 = new BufferedReader(new FileReader(KNLimit.DATA_DIR + "KNLIMIT_" + i + ".data"), 4096 * 2);

            Map<Integer, BufferedWriter> zone0WriterMap = new HashMap<Integer, BufferedWriter>();
            for (int j = 0; j < 64; j++) {
                zone0WriterMap.put(j, new BufferedWriter(
                    new FileWriter(KNLimit.MIDDLE_DIR + TEAM_COED + "/KNLIMIT_" + i + "_" + j + ".data"), 4096 * 2));
            }

            String readZone0NumberString = null;
            while ((readZone0NumberString = readerZone0.readLine()) != null) {
                Long readZone0Number = Long.parseLong(readZone0NumberString);
                int zoneKey = (int) (readZone0Number >>> 57);
                BufferedWriter writer = zone0WriterMap.get(zoneKey);
                writer.write(readZone0NumberString + "\n");
                Long saveCount = zoneSizeMapping.get(zoneKey);
                if (saveCount == null || saveCount == 0) {
                    zoneSizeMapping.put(zoneKey, 1L);
                } else {
                    zoneSizeMapping.put(zoneKey, ++saveCount);
                }

            }

            readerZone0.close();

            for (BufferedWriter value : zone0WriterMap.values()) {
                value.close();
            }

        }

        ObjectOutputStream oos =
            new ObjectOutputStream(new FileOutputStream(KNLimit.MIDDLE_DIR + TEAM_COED + "/index.data"));
        oos.writeObject(zoneSizeMapping);
        oos.close();
        return zoneSizeMapping;
    }


}
