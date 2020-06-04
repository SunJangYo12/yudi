package com.laudy.group;

import android.util.Log;
import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static String readFileBash(String path) {
        return executer("cat "+path);
    }
    public static String newFile(String newf) {
        return executer("touch "+newf);
    }
    public static String newFolder(String path) {
        return executer("mkdir "+path);
    }
    public static String removeFile(String rfile) {
        return executer("rm "+rfile);
    }
    public static String removeFolder(String path) {
        return executer("rm -R "+path);
    }
    public static String moveFile(String here, String dest) {
        return executer("mv "+here+" "+dest);
    }
    public static String copyFile(String file, String dest) {
        return executer("cp "+file+" "+dest);
    }

    public static String copy(String in, String out) 
    {
        File sourceFile = new File(in);
        File destFile = new File(out);

        if (!sourceFile.exists() || !destFile.exists()) {
            Log.i("mikusan", in+"  "+out);
            return "Source or destination file doesn't exist";
        }
        try (FileChannel srcChannel = new FileInputStream(sourceFile).getChannel(); FileChannel sinkChanel = new FileOutputStream(destFile).getChannel()) {
            srcChannel.transferTo(0, srcChannel.size(), sinkChanel);
            return "Copy sukses";
        } catch (IOException e) {
            return ""+e;
        }
    }

    public static String readFile(String path) {
        StringBuilder result = new StringBuilder();
        try {
            FileReader fis = new FileReader(path);
            char buffer[] = new char[1100];
            int read;

            do {
                read = fis.read(buffer);
                    
                if (read >= 0)
                {
                    result.append(buffer, 0, read);
                }
            } while (read >= 0);

            
        } catch (FileNotFoundException e) {
            return "File not found (TODO)";
        } catch (IOException ioe) {
            return "IOException (TODO)";
        }
        return result.toString();
    }

    public static void saveCode(String code, String charset, String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), charset);
        osw.append(code).flush();
        osw.close();
    }

    public static String executer(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line+"\n");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;

    }
}
