package iiis.systems.os.blockdb;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;


import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
Problem:
1. JSONObject Order;
2. Datadir;

 */



class otherTransaction {

    @JSONField(name = "TransactionID")
    private int transactionID;

    @JSONField(name = "Type")
    private String type;

    @JSONField(name = "UserID")
    private String userID;

    @JSONField(name = "Value")
    private int value;

    private int balance;

    private String dataDir;

    private String uniqueTid = "";

    public otherTransaction(int transactionID, String type, String userID, int value, int balance, String dataDir) {
        this.transactionID = transactionID;
        this.type = type;
        this.userID = userID;
        this.value = value;
        this.balance = balance;
        this.dataDir = dataDir;
        this.uniqueTid = Util.getRandomPassword(16) + transactionID ;
    }

    public String getTid() { return this.uniqueTid ; }
    public void outPut(int N) {
        JSONObject json = new JSONObject(new LinkedHashMap());
        json.put("TransactionID", uniqueTid);
        json.put("Type", type);
        json.put("UserID", userID);
        json.put("Value", value);
        int n = (transactionID - 1) / N + 1;
        BufferedWriter writer = null;
        String path = dataDir + n + ".json";
        File file = new File(path);
        //如果文件不存在，则新建一个
        if(!file.exists()){
            try {
                file.createNewFile();
                JSONObject JSONBlock = new JSONObject(new LinkedHashMap());
                JSONBlock.put("BlockID", n);
                JSONBlock.put("PrevHash", "00000000");
                JSONArray transactions = new JSONArray();
                JSONBlock.put("Transactions", transactions);
                JSONBlock.put("Nonce", "00000000");
                JSONObject balances = new JSONObject();
                JSONBlock.put("Balances", balances);
                String blockStr = JSONBlock.toString();
                JsonUtils.writeJsonFile(blockStr, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            org.json.JSONObject jsonFile = Util.readJsonFile(path);
            jsonFile.accumulate("Transactions", json);
            org.json.JSONObject balances = (org.json.JSONObject) jsonFile.get("Balances");
            balances.put(userID, balance);
            String outputJSONStr = jsonFile.toString();
            JsonUtils.writeJsonFile(outputJSONStr, path);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

class transferTransaction {

    @JSONField(name = "TransactionID")
    private int transactionID;

    @JSONField(name = "Type")
    private String type;

    @JSONField(name = "FromID")
    private String fromID;

    @JSONField(name = "ToID")
    private String  toID;

    @JSONField(name = "Value")
    private int value;

    private int fromBalance;
    private int toBalance;
    private String dataDir;

    private String uniqueTid = "";

    public transferTransaction(int transactionID, String type, String fromID, String toID, int value, int fromBalance, int toBalance, String dataDir) {
        this.transactionID = transactionID;
        this.type = type;
        this.fromID = fromID;
        this.toID = toID;
        this.value = value;
        this.fromBalance = fromBalance;
        this.toBalance = toBalance;
        this.dataDir = dataDir;
        this.uniqueTid = Util.getRandomPassword(16) + transactionID ;
    }


    public String getTid() { return this.uniqueTid ; }
    public void outPut(int N) {
        JSONObject json = new JSONObject(new LinkedHashMap());
        json.put("TransactionID", uniqueTid);
        json.put("Type", type);
        json.put("FromID", fromID);
        json.put("ToID", toID);
        json.put("Value", value);
        BufferedWriter writer = null;
        int n = transactionID / N + 1;
        String path = dataDir + n + ".json";
        File file = new File(path);
        //如果文件不存在，则新建一个
        if(!file.exists()){
            try {
                file.createNewFile();
                JSONObject JSONBlock = new JSONObject(new LinkedHashMap());
                JSONBlock.put("BlockID", n);
                JSONBlock.put("PrevHash", "00000000");
                JSONArray transactions = new JSONArray();
                JSONBlock.put("Transactions", transactions);
                JSONBlock.put("Nonce", "00000000");
                JSONObject balances = new JSONObject();
                JSONBlock.put("Balances", balances);
                String blockStr = JSONBlock.toString();
                JsonUtils.writeJsonFile(blockStr, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            org.json.JSONObject jsonFile = Util.readJsonFile(path);
            jsonFile.accumulate("Transactions", json);
            org.json.JSONObject balances = (org.json.JSONObject) jsonFile.get("Balances");
            balances.put(fromID, fromBalance);
            balances.put(toID, toBalance);
            String outputJSONStr = jsonFile.toString();
            JsonUtils.writeJsonFile(outputJSONStr, path);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


public class DatabaseEngine {
    private static DatabaseEngine instance = null;

    public Lock lock = new ReentrantLock();

    public static int N = 50;

    public static DatabaseEngine getInstance() {
        return instance;
    }

    public static void setup(String dataDir) {
        instance = new DatabaseEngine(dataDir);
    }

    private HashMap<String, Integer> balances = new HashMap<>();
    private int logLength = 0;
    private String dataDir;
    public int transactionID = 0;

    public void restart() {
        int n = 1;
        String path = dataDir + n + ".json";
        File file = new File(path);
        while (file.exists()){
            try{
                if (!Util.isJsonFile(path)) {
                    System.out.println("Jsonfile " + path + " is missing or in wrong format") ;
                    file.delete() ;
                    break ;
                }
                org.json.JSONObject jsonFile = Util.readJsonFile(path);
                org.json.JSONObject balance = (org.json.JSONObject)jsonFile.get("Balances");
                Iterator<String> userBalance = balance.keys();
                while(userBalance.hasNext()) {
                    String userID = userBalance.next();
                    balances.put(userID, (Integer) balance.get(userID));
                }
                org.json.JSONArray transactions = (org.json.JSONArray)jsonFile.get("Transactions");
                transactionID = transactionID + transactions.length();
                n++;
                path = dataDir + n + ".json";
                file = new File(path);
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

    DatabaseEngine(String dataDir) {
        this.dataDir = "./" + dataDir;
        this.restart();
     /*   for (int i = 1; i < 109; i ++) {
            deposit("test" + i, i) ;
        }*/
    }

    private int getOrZero(String userId) {
        if (balances.containsKey(userId)) {
            return balances.get(userId);
        } else {
            return 0;
        }
    }

    public int get(String userId) {
        logLength++;
        return getOrZero(userId);
    }

    public boolean put(String userId, int value) {
        lock.lock();
        logLength++;
        balances.put(userId, value);
        transactionID = transactionID + 1;
        otherTransaction transaction = new otherTransaction(transactionID, "put", userId, value, value, dataDir);
        transaction.outPut(N);
        lock.unlock();
        return true;
    }

    public boolean deposit(String userId, int value) {
        lock.lock();

        logLength++;
        int balance = getOrZero(userId);
        balances.put(userId, balance + value);
        transactionID = transactionID + 1;
        otherTransaction transaction = new otherTransaction(transactionID, "deposit", userId, value, balance + value, dataDir);
        transaction.outPut(N);
        lock.unlock();

        return true;
    }

    public boolean withdraw(String userId, int value) {
        lock.lock();

        logLength++;
        int balance = getOrZero(userId);

        otherTransaction transaction = new otherTransaction(transactionID, "withdraw", userId, value, balance - value, dataDir);

        if (value > balance) {
            System.out.println("Transaction " + transaction.getTid() + " failed with userId = " + userId + " and amount = " + value);
            lock.unlock();
            return false ;
        }
        balances.put(userId, balance - value);
        transactionID = transactionID + 1;
        transaction.outPut(N);
        lock.unlock();
        return true;
    }

    public boolean transfer(String fromId, String toId, int value) {
        lock.lock();

        logLength++;
        int fromBalance = getOrZero(fromId);
        int toBalance = getOrZero(toId);

        transferTransaction transaction = new transferTransaction(transactionID, "transfer", fromId, toId, value, fromBalance - value, toBalance + value, dataDir);

        if (value > fromBalance) {

            System.out.println("Transaction " + transaction.getTid() + " failed with fromuser Id = " + fromId + " and amount = " + value);
            lock.unlock();
            return false ;
        }

        balances.put(fromId, fromBalance - value);
        balances.put(toId, toBalance + value);
        transactionID = transactionID + 1;
        transaction.outPut(N);
        lock.unlock();

        return true;
    }

    public int getLogLength() {
        return transactionID - (transactionID - 1) / 50 * 50 ;
    }
}
