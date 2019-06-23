package hit.TechNews.Huffman;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
public class compressor {
    private File pFile;                                        /*源文件*/
    private encoder encode;                                     /* 编码*/
    private int []count=new int[256];                       /*存储byte在文件中次数*/
    private String[]code=new String[256];                  /*每个byte的索引比它的值大128*/
    private DataInputStream input;
    private DataOutputStream output;
    /*
        输入输出流
     */

    /*析构函数*/
    public compressor(File pFile, File file)throws IOException{
        this.pFile=pFile;
        this.encode=new encoder(pFile);
        this.code=encode.getCode();
        this.count=encode.getCount();
        this.output=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file),1024));
    }

    /**
     * 哈夫曼编码写入压缩文件
     * @throws IOException
     */
    private  void writeCode() throws  IOException{
        int size=0;
        for(int i=0;i<256;i++)
        {
            if(count[i]!=0)
                size++;
        }
        output.writeInt(size);
        for(int i=0;i<256;i++){
            if(count[i]!=0){
                output.writeByte((byte)(i-128));
                output.writeInt(count[i]);
            }
        }
    }

    /**
     * 压缩文件，若为目录则压缩该目录下全部文件
     * @param file
     * @throws IOException
     */
    public void compress(File file)throws IOException{
        if(file.isDirectory()){
            File[]filelist=file.listFiles();
            for(int i=0;i<filelist.length;i++)
            {
                compress(filelist[i]);
            }
        }else{
            input=new DataInputStream(new BufferedInputStream(new FileInputStream(file),1024));
            writeFileInfo(file);
            writefile(file);
        }
    }
    public void compress()throws IOException{
        writeCode();
        compress(pFile);
        output.flush();
        output.close();

    }

    /**
     * 写入原文件路径
     * @param file
     * @throws IOException
     */
    private void writeFileInfo(File file) throws IOException {
        String relativePath = file.getPath().replace(pFile.getParent(), "");
        output.writeUTF(relativePath);
    }

    /**
     * 字符串0,1转换byte
     * @param str
     * @return
     */
    private byte stringToByte(String str){
        if(str.length()!=8)
        {return 0;}
        int i;
        i = (str.charAt(0) - '0') * 128 + (str.charAt(1) - '0') * 64 + (str.charAt(2) - '0') * 32 + (str.charAt(3) - '0') * 16 + (str.charAt(4) - '0') * 8 + (str.charAt(5) - '0') * 4 + (str.charAt(6) - '0') * 2 + (str.charAt(7) - '0');
        return (byte) i;

    }

    /**
     * 字符串转换byte数组
     * @param str
     * @return
     */
    private  byte[] stringToBytes(String str){
        int re=str.length()%8;
        if(re!=0) {
            StringBuffer buffer = new StringBuffer(str);
            for (int i = 0; i < 8 - re; i++) {
                buffer.append(0);
            }
            /*补足8位*/
            str = buffer.toString();
            buffer = null;
        }
        byte []bs=new byte[str.length()/8];
        for(int i=0;i<bs.length;i++){
            String substr= str.substring(i*8,(i+1)*8);
            bs[i]=stringToByte(substr);
        }
        return bs;
    }

    /**
     * 压缩数据写入
     * @param file
     * @throws IOException
     */
    public void writefile(File file)throws IOException{
        int size=input.available();
        output.writeInt((size%3145728==0)?(size/3145728):(size/3145728+1));
        while((size=input.available())>0){
            int length=(size>=3145728)?3145728:size;
            byte[]btfile=new byte[length];
            input.read(btfile);
            StringBuffer buffer=new StringBuffer("");
            for(int i=0;i<btfile.length;i++){
                buffer.append(code[btfile[i]+128]);
            }
            String result=buffer.toString();
            buffer=null;

            int re=result.length()%8;
            byte[] bs=stringToBytes(result);
            result=null;
            output.writeInt(bs.length);
            output.writeByte((byte)((re==0)?0:(8-re)));
            for(int i=0;i<bs.length;i++)
            {
                output.writeByte(bs[i]);
            }
        }
        input.close();
    }



}


