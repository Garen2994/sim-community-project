package com.garen.community;

import java.io.IOException;

public class WKTest {
    
    public static void main(String[] args) {
        String mac_cmd = "/usr/local/bin/wkhtmltoimage www.baidu.com /Users/garen_hou/Desktop/test.png";
        try {
            Process p = Runtime.getRuntime().exec(mac_cmd);
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

