package hit.TechNews.Huffman;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class decompressor {
    private huffmannode root;
    /**
     * 哈夫曼树根节点
     */
    private File file;
    /**
     * 输出文件
     */
    private int[] count =new int [256];
    private  String[] string =new String[256];
    private  DataInputStream input;
    private  DataOutputStream output;


    /**
     * 析构函数
     * @param file
     * @throws IOException
     */
    public decompressor(File pfile,File file)throws IOException{
        getByteCode();
        this.file=file;
        this.input=new DataInputStream(new BufferedInputStream(new FileInputStream(pfile),1024));
    }
    public void decompress()throws IOException{
        getCount();
        root = new encoder(count).getRoot();
        while (input.available() > 0) {
            File ofile=new File(file.getPath() + "\\" + input.readUTF());
            File temp = new File(file.getParent());
            if (!temp.exists())
                temp.mkdirs();

            decompress(file);
    }
    }
    private void decompress(File file)throws IOException{
        output=new DataOutputStream((new BufferedOutputStream(new FileOutputStream(file),1024)));
        int size=input.readInt();
        for(int i=0;i<size;i++){
            writeFile();
        }
        output.flush();
        output.close();
    }

    /**
     *
     * @throws IOException
     */
    private void writeFile()throws IOException{
        int size=input.readInt();
        byte addition=input.readByte();
        byte[] btfile=new byte[size];
        input.read(btfile);
        StringBuffer buffer =new StringBuffer("");
        for(int i=0;i<btfile.length;i++)
        {
            buffer.append(string[btfile[i]+128]);
        }
        String result=buffer.toString();
        buffer=null;
        result=result.substring(0,result.length()-addition);
        int start=0;
        String substring="";
        for(int i=1;i<=result.length();i++){
            substring=result.substring(start,i);
            int j=-1;
            if((j=getByte(substring))!=-1)
            {
                output.writeByte((byte)(j-128));
                start=i;
            }
        }
    }

    /**
     * 从编码获取byte
     * @param str
     * @return
     */
    private int getByte(String str){
        huffmannode i=root;
        for(int j=0;j<str.length();j++){
            if(str.charAt(j)=='0'&&i.getLeft()!=null)
            {
                i=i.getLeft();
            }
            else if (str.charAt(j)=='1'&&i.getRight()!=null)
            {
                i=i.getRight();
            }
            else
            {
                ;
            }
        }
        if(i.getRight()==null&&i.getLeft()==null)
        {
            return i.getBt()+128;
        }
        return -1;
    }

    /**
     * 字符串转化编码
     */
    private void getByteCode(){
        for (int j = 0; j < 256; j++) {
            String result = "";
            int k = j - 128;
            int value = (k < 0) ? (k + 256) : k;
            for (int i = 0; i < 8; i++) {
                int temp = (int) Math.pow(2, 8 - i - 1);
                result += value / temp;
                value %= temp;
            }
            string[j] = result;
        }

    }

    /**
     * 从压缩文件中获取每个文件编码
     * @throws IOException
     */
    private void getCount() throws IOException{
        int size=input.readInt();
        for(int i=0;i<size;i++){
            byte bt=input.readByte();
            int t=input.readInt();
            count[bt+128]=t;
        }
    }

}
