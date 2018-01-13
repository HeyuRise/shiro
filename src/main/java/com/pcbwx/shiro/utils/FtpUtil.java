package com.pcbwx.shiro.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
    
public class FtpUtil {
	
	private static final Logger logger = LogManager.getLogger(FtpUtil.class);
	
    private static FTPClient ftpClient;    
    public static final int BINARY_FILE_TYPE = FTP.BINARY_FILE_TYPE;    
    public static final int ASCII_FILE_TYPE = FTP.ASCII_FILE_TYPE;        
        
    public static void connectServer(String server, int port, String user,    
            String password, String path) throws SocketException, IOException {    
        ftpClient = new FTPClient();    
        ftpClient.connect(server, port);    
        logger.info("Connected to " + server + ".");    
        logger.info(ftpClient.getReplyCode());    
        ftpClient.login(user, password);    
        // Path is the sub-path of the FTP path    
        if (path.length() != 0) {    
            ftpClient.changeWorkingDirectory(path);    
        }    
    }    
    //FTP.BINARY_FILE_TYPE | FTP.ASCII_FILE_TYPE    
    // Set transform type    
    public void setFileType(int fileType) throws IOException {    
        ftpClient.setFileType(fileType);    
    }    
    
    public static void closeServer() throws IOException {    
        if (ftpClient.isConnected()) {    
            ftpClient.disconnect();    
        }    
    }    
    //=======================================================================    
    //==         About directory       =====    
    // The following method using relative path better.    
    //=======================================================================    
        
    public static boolean changeDirectory(String path) throws IOException {    
        return ftpClient.changeWorkingDirectory(path);    
    }    
    public static boolean createDirectory(String pathName) throws IOException {    
        return ftpClient.makeDirectory(pathName);    
    }    
    public static boolean removeDirectory(String path) throws IOException {    
        return ftpClient.removeDirectory(path);    
    }    
        
    // delete all subDirectory and files.    
    public static boolean removeDirectory(String path, boolean isAll)    
            throws IOException {    
            
        if (!isAll) {    
            return removeDirectory(path);    
        }    
    
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);    
        if (ftpFileArr == null || ftpFileArr.length == 0) {    
            return removeDirectory(path);    
        }    
        //     
        for (FTPFile ftpFile : ftpFileArr) {    
            String name = ftpFile.getName();    
            if (ftpFile.isDirectory()) {    
System.out.println("* [sD]Delete subPath ["+path + "/" + name+"]");                 
                removeDirectory(path + "/" + name, true);    
            } else if (ftpFile.isFile()) {    
System.out.println("* [sF]Delete file ["+path + "/" + name+"]");                            
                deleteFile(path + "/" + name);    
            } else if (ftpFile.isSymbolicLink()) {    
    
            } else if (ftpFile.isUnknown()) {    
    
            }    
        }    
        return ftpClient.removeDirectory(path);    
    }    
        
    // Check the path is exist; exist return true, else false.    
    public static boolean existDirectory(String path) throws IOException {    
        boolean flag = false;    
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);    
        for (FTPFile ftpFile : ftpFileArr) {    
            if (ftpFile.isDirectory()    
                    && ftpFile.getName().equalsIgnoreCase(path)) {    
                flag = true;    
                break;    
            }    
        }    
        return flag;    
    }    
    
    //=======================================================================    
    //==         About file        =====    
    // Download and Upload file using    
    // ftpUtil.setFileType(FtpUtil.BINARY_FILE_TYPE) better!    
    //=======================================================================    
    
    // #1. list & delete operation    
    // Not contains directory    
    public static List<String> getFileList(String path) throws IOException {    
        // listFiles return contains directory and file, it's FTPFile instance    
        // listNames() contains directory, so using following to filer directory.    
        //String[] fileNameArr = ftpClient.listNames(path);    
        FTPFile[] ftpFiles= ftpClient.listFiles(path);    
            
        List<String> retList = new ArrayList<String>();    
        if (ftpFiles == null || ftpFiles.length == 0) {    
            return retList;    
        }    
        for (FTPFile ftpFile : ftpFiles) {    
            if (ftpFile.isFile()) {    
                retList.add(ftpFile.getName());    
            }    
        }    
        return retList;    
    }    
    
    public static boolean deleteFile(String pathName) throws IOException {    
        return ftpClient.deleteFile(pathName);    
    }    
    
    // #2. upload to ftp server    
    // InputStream <------> byte[]  simple and See API    
    
    public static boolean uploadFile(String fileName, String newName)    
            throws IOException {    
        boolean flag = false;    
        InputStream iStream = null;    
        try {    
            iStream = new FileInputStream(fileName);    
            flag = ftpClient.storeFile(newName, iStream);    
        } catch (IOException e) {    
            flag = false;    
            return flag;    
        } finally {    
            if (iStream != null) {    
                iStream.close();    
            }    
        }    
        return flag;    
    }    
    
    public static boolean uploadFile(String fileName) throws IOException {    
        return uploadFile(fileName, fileName);    
    }    
    
    public static boolean uploadFile(InputStream iStream, String newName)    
            throws IOException {    
        boolean flag = false;    
        try {    
            // can execute [OutputStream storeFileStream(String remote)]    
            // Above method return's value is the local file stream.    
            flag = ftpClient.storeFile(newName, iStream);    
        } catch (IOException e) {    
            flag = false;    
            return flag;    
        } finally {    
            if (iStream != null) {    
                iStream.close();    
            }    
        }    
        return flag;    
    }    
    
    // #3. Down load     
    
    public static boolean download(String remoteFileName, String localFileName)    
            throws IOException {    
        boolean flag = false;    
        File outfile = new File(localFileName);    
        OutputStream oStream = null;    
        try {    
            oStream = new FileOutputStream(outfile);    
            flag = ftpClient.retrieveFile(remoteFileName, oStream);    
        } catch (IOException e) {    
            flag = false;    
            return flag;    
        } finally {    
            oStream.close();    
        }    
        return flag;    
    }    
        
    public static InputStream downFile(String sourceFileName) throws IOException {    
        return ftpClient.retrieveFileStream(sourceFileName);    
    }
}
