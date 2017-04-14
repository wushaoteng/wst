package com.alibaba.middleware.race;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaoteng.wst on 2017/4/6.
 */
public class TopKN implements KNLimit {
    private static Logger logger = LoggerFactory.getLogger(TopKN.class);
    private static final int ZONE_NUM = 64;
    private static final String TEAM_COED = "71263qsvb5";
    private static final String INDEX_FILE = MIDDLE_DIR + TEAM_COED + "/index.data";
    private Map<String, Long> fileSizeMapping = new HashMap<>();
    private Map<Integer, Long> zoneSizeMapping = new HashMap<>();

    public static void main(String[] args) throws IOException {

        long start = System.currentTimeMillis();
        if (args.length != 2) {
            throw new InvalidParameterException("Please give the right paramaters 'k' and 'n'.");
        } else {
            logger.warn("k=" + Long.valueOf(args[0]));
            logger.warn("n=" + Integer.valueOf(args[1]));
            System.out.println("k=" + Long.valueOf(args[0]));
            System.out.println("n=" + Integer.valueOf(args[1]));
        }

        TopKN demo = new TopKN();
        demo.processTopKN(Long.valueOf(args[0]), Integer.valueOf(args[1]));
        logger.warn("Total topKN cost:" + (System.currentTimeMillis() - start));
        System.out.println("Total topKN cost:" + (System.currentTimeMillis() - start));


    }


    @Override
    public void processTopKN(long k, int n) {
        try {
            List<Long> result = new ArrayList<>();
            init();
            long start = k;
            long count = n;
            int zone = 0;
            long totalCount = 0;
            for(long a:zoneSizeMapping.values()){
                totalCount += a;
            }
            System.out.println("Total count:" + totalCount);
            for(int i = 0; i < ZONE_NUM; i ++ ){
                if((start - zoneSizeMapping.get(i))>0) {
                    start -= zoneSizeMapping.get(i);
                    continue;
                }
                zone = i;
                break;
            }
            boolean searching = false;
            System.out.println("Start to search result ......");
            while (!searching) {
                List<Long> a = getFromZone(zone, start, count);
                if (a.size() < count) {
                    zone++;
                    start = 0;
                    count -= a.size();
                } else {
                    searching = true;
                }
                result.addAll(a);
            }
            System.out.println("Searching finished. Result:" +result);
            outputResults(result);
        }catch (Exception e) {
            logger.error("Exception!",e);
        }
    }

    private List<Long> getFromZone(int zone, long start, long count) {
        System.out.println();
        List<Long> allData = readZoneData(zone);
        SortUtil.quickSort(allData);
        List<Long> zoneList = new ArrayList<>();
        try {
            for (int i = 0; i < count; i++) {
                zoneList.add(allData.get((int) start + i));
            }
        }catch (Exception e) {
            logger.error("getFromZone exception!", e);
        }
        return zoneList;
    }

    private List<Long> readZoneData(int zone) {
        long startTime = System.currentTimeMillis();
        List<Long> result = new ArrayList<>();
        try {
            for (int i = 0; i < 10; i++) {
                String tmpFilePath = MIDDLE_DIR + TEAM_COED + "/" + FILE_PREFIX + i + "_" + zone + FILE_SUFFIX;
                File file = new File(tmpFilePath);
                if(!file.exists()) {
                    continue;
                }

                result.addAll(getRecordsListStream(tmpFilePath));
            }
        } catch (Exception e) {
            logger.error("readZoneData exception!",e);
        }
        logger.warn("ReadZoneData cost:" + (System.currentTimeMillis() - startTime));
        System.out.println("Zone ("+ zone+ ") size:" + result.size());
        System.out.println("ReadZoneData ("+ zone + ") cost:" + (System.currentTimeMillis() - startTime));
        return result;
    }

    public List<Long> getRecordsListStream(String path) {
        long startTime = System.currentTimeMillis();
        List<Long> recordsList = new ArrayList<>();
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
            try {
                long line = 0;
                while ((line = dis.readLong()) != -1) {
                    recordsList.add(line);
                }

            } catch (EOFException e) {
                logger.error("getRecordsList EOF", e);
            } finally {
                dis.close();
            }
        }catch (Exception e) {
            logger.error("getRecordsList exception", e);
        }
        System.out.println("ReadTmpFile " + path + " cost:" + (System.currentTimeMillis() - startTime));
        return recordsList;
    }

    public static String[] readToString(String filePath)
    {
        File file = new File(filePath);
        Long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
        try
        {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String s = new String(filecontent);

        String[] fileContentArr = s.split("\n");

        return fileContentArr;// 返回文件内容,默认编码
    }

    public List<Long> getRecordsList(String path) {
        List<Long> recordsList = new ArrayList<>();

        try {
            File file = new File(path);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                recordsList.add(Long.valueOf(line));
            }

            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            logger.error("getRecordsList exception",e);
        }

        return recordsList;
    }

    private void init() {
        if (hasInited()) {
            System.out.println("Don't need to build index.");
            return;
        } else {
            fileSpilt();
        }
    }
    private boolean hasInited() {
        long startTime = System.currentTimeMillis();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INDEX_FILE));
            zoneSizeMapping = (Map<Integer, Long>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            System.out.println("CheckInit exception:" + e.getMessage());
            System.out.println("ReadIndex fail cost:" + (System.currentTimeMillis() - startTime));
            logger.error("hasInited exception",e);
            return false;
        }
        System.out.println("ReadIndex success cost:" + (System.currentTimeMillis() - startTime));
        return true;
    }
    private void fileSpilt() {
        System.out.println("Start to build index ......");
        long startTime = System.currentTimeMillis();
        try {
            zoneSizeMapping = FileUtils.fileSplit2();
        } catch (Exception e) {
            logger.error("fileSpilt exception",e);
            System.out.println(e.getMessage());
        }
        System.out.println("Build index finished.");
        System.out.println("Build index total cost:" + (System.currentTimeMillis() - startTime));
    }
    private void outputResults(List<Long> resultList) {
        try {

            FileWriter fileWriter = new FileWriter(
                KNLimit.RESULT_DIR + TEAM_COED + "/" + KNLimit.RESULT_NAME);
            for (Long value : resultList) {
                fileWriter.append(String.valueOf(value) + "\n");
            }
            logger.warn("ResultList:" + fileWriter.toString());
            fileWriter.close();

        } catch (IOException e) {
            logger.error("outputResults exception",e);
        }
    }
}
