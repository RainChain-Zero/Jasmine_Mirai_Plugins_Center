package meme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;

/**
 * @author 慕北_Innocent
 * @version 1.0
 * @date 2022/04/23 11:17
 */
public class JudgeFavor {
    //用户配置文件
    public static final String UserConfPath = "F:\\Bot\\mirai_console_diceplugin\\Dice2632573315\\user\\UserConf";

    //读取整个文件
    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    //判断好感是否达到3000
    public boolean judgeFavor(Long QQ,int favorLimit) {
        String jsonStr = readToString(UserConfPath+QQ+"\\favorConf.json");
        if (jsonStr == null) {
            return false;
        }
        try {
            Gson gs = new GsonBuilder().setPrettyPrinting().create();
            JsonElement favorElement = gs.fromJson(jsonStr, JsonObject.class).get("好感度");
            if (favorElement != null) {
                int favor = favorElement.getAsInt();
                return favor >= favorLimit;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
