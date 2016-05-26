package UI;
/**
 * Project Name:Try
 * File Name:GenerateValueFiles.java
 * Package Name:
 * Date:2016-2-22下午4:12:09
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 *
 */

/**
 * ClassName:GenerateValueFiles <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016-2-22 下午4:12:09 <br/>
 * @author   SunXL
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * 
 * ClassName: GenerateValueFiles_1920 <br/>
 * Function: px到dp多种格式多文件转换 <br/>
 * Reason: 安卓屏幕适配,鉴于使用1920x1080px的标准,且虚拟键盘导致的需要重新计算参数,而故生成px到dp的转换,原博链接:http://blog.csdn.net/lmj623565791/article/details/45460089 <br/>
 * use: 基础宽度basew=1080,baseh=1920,即以1920x1080为模板,
 * 		故需要"px/3的dp"时可直接使用640X360文件夹下的y文件,--2016-2-24时初次使用,修改已保存
 * 		核心修改为cellw与cellh,--2016-5-26时需要以原有基础x1.44,故可将cell * 1.44f
 * kernel: cellw = 需求屏幕宽度 / 基础宽度
 * 		 
 * tips: 默认的最后一条为basew/baseh为输出,需要时可将for循环条件+1
 * 
 * date: 2016-2-24 下午7:51:38 
 * 		 2016-5-26  下午3:26:13	
 * <br/>
 *
 * @author SunXL
 * @version 
 * @since JDK 1.6
 */
public class GenerateValueFiles_1920 {

    private int baseW;
    private int baseH;

    private String dirStr = "./res";

    /**
     * 生成的格式,后替换"{0}"与"{1}"为对应值,如果需要xy分开,则可W,H都改
     */
    private final static String WTemplate = "<dimen name=\"x{0}\">{1}dp</dimen>\n";
    private final static String HTemplate = "<dimen name=\"px_{0}\">{1}dp</dimen>\n";

    /**
     * {0}-HEIGHT
     */
    private final static String VALUE_TEMPLATE = "values-{0}x{1}";

    //需求屏幕宽高数组
    private static final String SUPPORT_DIMESION = "320,480;480,800;360,640;480,854;540,960;600,1024;720,1184;720,1196;720,1280;768,1024;800,1280;1080,1812;1080,1920;1440,2560;";

    private String supportStr = SUPPORT_DIMESION;

    public GenerateValueFiles_1920(int baseX, int baseY, String supportStr) {
        this.baseW = baseX;
        this.baseH = baseY;

        if (!this.supportStr.contains(baseX + "," + baseY)) {
            this.supportStr += baseX + "," + baseY + ";";
        }

        this.supportStr += validateInput(supportStr);

        System.out.println(supportStr);

        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdir();

        }
        System.out.println(dir.getAbsoluteFile());

    }

    /**
     * @param supportStr
     *            w,h_...w,h;
     * @return
     */
    private String validateInput(String supportStr) {
        StringBuffer sb = new StringBuffer();
        String[] vals = supportStr.split("_");
        int w = -1;
        int h = -1;
        String[] wh;
        for (String val : vals) {
            try {
                if (val == null || val.trim().length() == 0)
                    continue;

                wh = val.split(",");
                w = Integer.parseInt(wh[0]);
                h = Integer.parseInt(wh[1]);
            } catch (Exception e) {
                System.out.println("skip invalidate params : w,h = " + val);
                continue;
            }
            sb.append(w + "," + h + ";");
        }

        return sb.toString();
    }

    public void generate() {
        String[] vals = supportStr.split(";");
        for (String val : vals) {
            String[] wh = val.split(",");
            generateXmlFile(Integer.parseInt(wh[0]), Integer.parseInt(wh[1]));
        }

    }

    private void generateXmlFile(int w, int h) {

        StringBuffer sbForWidth = new StringBuffer();
        sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForWidth.append("<resources>");
        
        float cellw = w * 1.0f / baseW;//计算宽度的核心
        
        System.out.println("width : " + w + "," + baseW + "," + cellw);
        for (int i = 1; i < baseW; i++) {
        	//width替换的值
            sbForWidth.append(WTemplate.replace("{0}", i + "").replace("{1}",
                    change(cellw * i) + ""));
        }
        sbForWidth.append(WTemplate.replace("{0}", baseW + "").replace("{1}",
                w + ""));
        sbForWidth.append("</resources>");

        StringBuffer sbForHeight = new StringBuffer();
        sbForHeight.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sbForHeight.append("<resources>");
        
        float cellh = h *1.0f/ baseH;//计算高度的核心
        
        System.out.println("height : "+ h + "," + baseH + "," + cellh);
        for (int i = 1; i < baseH; i++) {
        	//height替换的值
            sbForHeight.append(HTemplate.replace("{0}", i + "").replace("{1}",
                    change(cellh * i) + ""));
        }
        sbForHeight.append(HTemplate.replace("{0}", baseH + "").replace("{1}",
                h + ""));
        sbForHeight.append("</resources>");

        File fileDir = new File(dirStr + File.separator
                + VALUE_TEMPLATE.replace("{0}", h + "")//
                        .replace("{1}", w + ""));
        fileDir.mkdir();

        File layxFile = new File(fileDir.getAbsolutePath(), "lay_x.xml");
        File layyFile = new File(fileDir.getAbsolutePath(), "lay_y.xml");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
            pw.print(sbForWidth.toString());
            pw.close();
            pw = new PrintWriter(new FileOutputStream(layyFile));
            pw.print(sbForHeight.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * change:整数转成浮点数 <br/>
     *
     * @author SunXL
     * @param a
     * @return
     * @since JDK 1.6
     */
    public static float change(float a) {
        int temp = (int) (a * 100);
        return temp / 100f;
    }

    public static void main(String[] args) {
        int baseW = 1080;
        int baseH = 1920;
        String addition = "";
        try {
            if (args.length >= 3) {
                baseW = Integer.parseInt(args[0]);
                baseH = Integer.parseInt(args[1]);
                addition = args[2];
            } else if (args.length >= 2) {
                baseW = Integer.parseInt(args[0]);
                baseH = Integer.parseInt(args[1]);
            } else if (args.length >= 1) {
                addition = args[0];
            }
        } catch (NumberFormatException e) {

            System.err
                    .println("right input params : java -jar xxx.jar width height w,h_w,h_..._w,h;");
            e.printStackTrace();
            System.exit(-1);
        }

        new GenerateValueFiles_1920(baseW, baseH, addition).generate();
    }

}

