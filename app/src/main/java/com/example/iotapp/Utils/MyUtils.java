package com.example.iotapp.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class MyUtils {
    public static String GetJsonFromUrl(URL url) throws IOException {
        String inline = "";
        Scanner scanner = new Scanner(url.openStream());

        //Write all the JSON data into a string using a scanner
        while (scanner.hasNext()) {
            inline += scanner.nextLine();
        }

        //Close the scanner
        scanner.close();

        return inline;
    }
}
