package com.ants.common.utils;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**  
 * @ClassName: ReadExcelUtils  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author 马高伟
 * @date 2018年4月8日  
 *    
 */
public class ReadExcelUtils {
	private Workbook wb;
	private Sheet sheet;
	private Row row;

	public ReadExcelUtils(String filepath) {
		if (filepath == null) {
			return;
		}
		String ext = filepath.substring(filepath.lastIndexOf("."));
		try {
			InputStream is = new FileInputStream(filepath);
			if (".xls".equals(ext)) {
				wb = new HSSFWorkbook(is);
			} else if (".xlsx".equals(ext)) {
				wb = new XSSFWorkbook(is);
			} else {
				wb = null;
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * 读取Excel表格表头的内容
	 * 
	 * @param InputStream
	 * @return String 表头内容的数组
	 * @author zengwendong
	 */
	public String[] readExcelTitle() throws Exception {
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		sheet = wb.getSheetAt(0);
		row = sheet.getRow(0);
		// 标题总列数
		int colNum = row.getPhysicalNumberOfCells();
		System.out.println("colNum:" + colNum);
		String[] title = new String[colNum];
		for (int i = 0; i < colNum; i++) {
			// title[i] = getStringCellValue(row.getCell((short) i));
			title[i] = row.getCell(i).getCellFormula();
		}
		return title;
	}

	/**
	 * 读取Excel数据内容
	 * 
	 * @param InputStream
	 * @return Map 包含单元格数据内容的Map对象
	 */
	public Map<Integer, Map<Integer, Object>> readExcelContent()
			throws Exception {
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		Map<Integer, Map<Integer, Object>> content = new HashMap<Integer, Map<Integer, Object>>();

		sheet = wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		int colNum = row.getPhysicalNumberOfCells();
		// 正文内容应该从第二行开始,第一行为表头的标题
		for (int i = 1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			int j = 0;
			Map<Integer, Object> cellValue = new HashMap<Integer, Object>();
			while (j < colNum) {
				Object obj = getCellFormatValue(row.getCell(j));
				cellValue.put(j, obj);
				j++;
			}
			content.put(i, cellValue);
		}
		return content;
	}

	/**
	 * 
	 * 根据Cell类型设置数据
	 * 
	 * @param cell
	 * @return
	 */
	private Object getCellFormatValue(Cell cell) {
		Object cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
			case Cell.CELL_TYPE_FORMULA: {
				// 判断当前的cell是否为Date
				if (DateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式
					// data格式是带时分秒的：2013-7-10 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();
					// data格式是不带带时分秒的：2013-7-10
					Date date = cell.getDateCellValue();
					cellvalue = date;
				} else {// 如果是纯数字

					// 取得当前Cell的数值
					cellvalue = String.valueOf(cell.getNumericCellValue());
				}
				break;
			}
			case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			default:// 默认的Cell值
				cellvalue = "";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;
	}

	public static void main(String[] args) {
		try {
			String filepath = "F:\\111111111111111111111.xls";
			ReadExcelUtils excelReader = new ReadExcelUtils(filepath);

			// 对读取Excel表格内容测试
			Map<Integer, Map<Integer, Object>> map = excelReader
					.readExcelContent();
			//System.out.println("获得Excel表格的内容:");
			System.out.println(map.size());
			for (int i = 1; i <= map.size(); i++) {
				String str = (String) map.get(i).get(0);
				 Pattern pattern = Pattern.compile("[^0-9]");
			        Matcher matcher = pattern.matcher(str);
			        String subId = matcher.replaceAll("");
			        String subName = str.replaceAll("\\d+","");
			        String subLevel="";
			        String motherId="";
			        switch(subId.length()){
			        	case 2:{subLevel = "0";motherId="0";break;}
			        	case 4:{subLevel = "1";motherId=subId.substring(0,2);break;}
			        	case 6:{subLevel = "2";motherId=subId.substring(0,4);;break;}
			        }
			        String subType = subId.substring(0,2);
			        if(subType.substring(0,1).equals("0")){
			        	subType = subType.substring(1,2);
			        }
			       
			        //System.out.println(subName.replaceAll("\\s*", ""));
			      //System.out.println(subId+"--"+subType+"--"+subName.trim()+"--"+motherId+"--"+subLevel);
			        ConnectionMysql(subId, subType, subName, motherId, subLevel);
			}
		} catch (FileNotFoundException e) {
			System.out.println("未找到指定路径的文件!");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void ConnectionMysql(String subId, String subType,String subName,String motherId,String subLevel){
		String driver = "com.mysql.jdbc.Driver";    
        String URL = "jdbc:mysql://localhost:3306/graduation";    
        Connection con = null;  
        ResultSet rs = null;  
        PreparedStatement  st = null;  
        String sql = "insert into t_sub_info (sub_id,sub_type,sub_name,mother_id,sub_level) values('"+subId+"','"+subType+"','"+subName.substring(3)+"','"+motherId+"','"+subLevel+"');";  
       //System.out.println(sql);
        try    
        {    
            Class.forName(driver);    
        }    
        catch(java.lang.ClassNotFoundException e)    
        {    
            System.out.println("Cant't load Driver");    
        }    
        try       
        {                                                                                   
            con=DriverManager.getConnection(URL,"root","123456");    
            st = con.prepareStatement(sql);  
            st.executeUpdate();  
            st.close();  
            con.close();  
        }     
        catch(Exception e)    
        {    
            System.out.println("Connect fail:" + e.getMessage());    
        }    
    }    
	
}
