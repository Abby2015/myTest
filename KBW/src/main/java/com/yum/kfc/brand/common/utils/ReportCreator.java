package com.yum.kfc.brand.common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Excel报表的公用处理类
 * @author luolix
 *
 */
public class ReportCreator {
	private HSSFWorkbook wb;

	private HSSFSheet sheet;

	private int initRowNum;

	private int repeatRowNum;

	private HSSFRow row = null;

	private HSSFCell cell = null;

	private HSSFCellStyle cellStyle = null;

	private HSSFCellStyle cellStyle1 = null;

	private HSSFCellStyle cellStyle2 = null;

	private HSSFCellStyle cellStyle3 = null;

	private HSSFCellStyle cellStyle4 = null;

	private HSSFCellStyle cellStyle5 = null;

	private HSSFCellStyle cellStyle6 = null;

	private HSSFCellStyle cellStyle7 = null;

	private HSSFCellStyle cellStyle8 = null;

	private HSSFCellStyle cellStyle9 = null;

//	private HSSFColor co = null;

	private List firstInsert = null;

	/*
	 * inputFileName -- 输入的文件名
	 */
	public ReportCreator(String inputFileName) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
				inputFileName));
		wb = new HSSFWorkbook(fs);
		sheet = wb.getSheetAt(0);
		// //样式1
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 10);// 设置字体大小
		font.setFontName("宋体");
		cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setWrapText(true);

		// cellStyle.set
		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);// 设置字体大小
		font2.setFontName("宋体");
		cellStyle2 = wb.createCellStyle();
		cellStyle2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		cellStyle2.setFont(font2);
		cellStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle2.setWrapText(true);
		// //样式2

		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 11);// 设置字体大小
		font1.setFontName("宋体");
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗

		cellStyle1 = wb.createCellStyle();
		cellStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// System.out.println("color"+HSSFColor.RED.getIndex());

		cellStyle1.setFont(font1);
		cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle1.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
		cellStyle1.setFillPattern(HSSFCellStyle.THIN_BACKWARD_DIAG);

		// /样式3
		HSSFFont font3 = wb.createFont();
		font3.setFontHeightInPoints((short) 10);// 设置字体大小
		font3.setFontName("宋体");
		font3.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
		cellStyle3 = wb.createCellStyle();
		cellStyle3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle3.setFont(font3);
		cellStyle3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle3.setWrapText(true);
		// /样式4
		HSSFFont font4 = wb.createFont();
		font4.setFontHeightInPoints((short) 10);// 设置字体大小
		font4.setFontName("宋体");
		font4.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
		cellStyle4 = wb.createCellStyle();
		cellStyle4.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle4.setFont(font4);
		cellStyle4.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle4.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle4.setWrapText(true);
		// /样式5
		HSSFFont font5 = wb.createFont();
		font5.setFontHeightInPoints((short) 10);// 设置字体大小
		font5.setFontName("宋体");
		font5.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
		cellStyle5 = wb.createCellStyle();
		cellStyle5.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle5.setFont(font5);
		cellStyle5.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		cellStyle5.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle5.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle5.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle5.setWrapText(true);
		// /样式6底部有
		HSSFFont font6 = wb.createFont();
		font6.setFontHeightInPoints((short) 10);// 设置字体大小
		font6.setFontName("宋体");
		font6.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
		cellStyle6 = wb.createCellStyle();
		cellStyle6.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle6.setFont(font6);
		cellStyle6.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle6.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle6.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle6.setWrapText(true);
		// 样式7----红色
		HSSFFont font7 = wb.createFont();
		font7.setFontHeightInPoints((short) 10);// 设置字体大小
		font7.setFontName("宋体");
		font7.setColor(HSSFFont.COLOR_RED);
		// font7.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗

		cellStyle7 = wb.createCellStyle();
		cellStyle7.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle7.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		cellStyle7.setFont(font7);
		cellStyle7.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle7.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle7.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle7.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// cellStyle7.setFillForegroundColor(HSSFColor.RED.index);
		// cellStyle7.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 样式8---黄色
		HSSFFont font8 = wb.createFont();
		font8.setFontHeightInPoints((short) 10);// 设置字体大小
		font8.setFontName("宋体");
		// 设置颜色--HSSFColor中的颜色比较多
		short color_yellow = HSSFColor.GOLD.index;
		font8.setColor(color_yellow);
		// font8.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗

		cellStyle8 = wb.createCellStyle();
		cellStyle8.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle8.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		cellStyle8.setFont(font8);
		cellStyle8.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle8.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle8.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle8.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// cellStyle8.setFillForegroundColor(HSSFColor.YELLOW.index);
		// cellStyle8.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 样式9---绿色
		HSSFFont font9 = wb.createFont();
		font9.setFontHeightInPoints((short) 10);// 设置字体大小
		font9.setFontName("宋体");
		// 设置颜色--HSSFColor中的颜色比较多
		short color_green = HSSFColor.SEA_GREEN.index;
		font9.setColor(color_green);

		// font9.setColor(HSSFFont.COLOR_GREEN); HSSFFont无COLOR_GREEN变量
		// font9.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗

		cellStyle9 = wb.createCellStyle();
		cellStyle9.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle9.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		cellStyle9.setFont(font9);
		cellStyle9.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle9.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle9.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle9.setBorderTop(HSSFCellStyle.BORDER_THIN);
		// cellStyle9.setFillForegroundColor(HSSFColor.GREEN.index);
		// cellStyle9.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	}

	/*
	 * inputFileName -- 输入的文件名 insertNum -- 需要插入的行的索引坐标
	 */
	public ReportCreator(String inputFileName, int insertNum)
			throws IOException {
		this(inputFileName);
//		System.out.println("PPP" + insertNum);
		initRowNum = insertNum;
		repeatRowNum = insertNum;

	}

	public HSSFSheet getSheet() {
		return sheet;
	}

	public HSSFWorkbook getWorkbook() {
		return wb;
	}

	public void write(OutputStream out) throws IOException {
		wb.write(out);
		out.close();
	}

	public void setCellStyle(HSSFCellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}

	@SuppressWarnings("rawtypes")
	public void addRepeatRows(ArrayList list, int insertIndex) {
		repeatRowNum = insertIndex;
		addRepeatRows(list);
	}

	@SuppressWarnings("rawtypes")
	public void addRepeatRows(ArrayList list) {
		ArrayList rowList = null;
		if (list == null || list.size() == 0)
			return;
		if (list.get(0) != null)
			firstInsert = (ArrayList) list.get(0);
		// lastInsert = (ArrayList)list.get(list.size()-1);
		String colStr = null;
		repeatRowNum = repeatRowNum - 1;
		for (int i = 0; i < list.size(); i++) {
			rowList = (ArrayList) list.get(i);
			// 列的数量
			int colNum = rowList.size();
			String endstr = (String) rowList.get(colNum - 1);
			// 插入的行数
			repeatRowNum = repeatRowNum + 1;
			row = sheet.createRow(repeatRowNum);
			if (endstr.equals("0")) {
				for (int j = 0; j < colNum - 1; j++) {
					colStr = (String) rowList.get(j);
					cell = row.createCell((short) j);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					try {
						double d = Double.parseDouble(colStr);
						cell.setCellValue(d);
					} catch (NumberFormatException ne) {
						cell.setCellValue(colStr);
					}
					if (cellStyle != null)
						cell.setCellStyle(cellStyle);
				}
			} else {
				for (int j = 0; j < colNum - 1; j++) {
					colStr = (String) rowList.get(j);
					cell = row.createCell((short) j);
					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					try {
						double d = Double.parseDouble(colStr);
						cell.setCellValue(d);
					} catch (NumberFormatException ne) {
						cell.setCellValue(colStr);
					}
					if (cellStyle1 != null)
						cell.setCellStyle(cellStyle1);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void addRepeatRows_General(ArrayList list) {
		ArrayList rowList = null;
		if (list == null || list.size() == 0)
			return;
		if (list.get(0) != null)
			firstInsert = (ArrayList) list.get(0);
		String colStr = null;
		repeatRowNum = repeatRowNum - 1;
		for (int i = 0; i < list.size(); i++) {
			rowList = (ArrayList) list.get(i);
			// 列的数量
			int colNum = rowList.size();
			String endstr = (String) rowList.get(colNum - 1);
			// 插入的行数
			repeatRowNum = repeatRowNum + 1;
			row = sheet.createRow(repeatRowNum);
			for (int j = 0; j < colNum; j++) {
				colStr = (String) rowList.get(j);
				cell = row.createCell((short) j);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				try {
//					double d = Double.parseDouble(colStr);
//					cell.setCellValue(d);
					cell.setCellValue(colStr);
				} catch (NumberFormatException ne) {
					cell.setCellValue(colStr);
				}
				if (cellStyle2 != null){
					cell.setCellStyle(cellStyle2);
				}
			} 
		}
	}
	
	public void addDtaqDataRows_General(ArrayList list) {
		ArrayList rowList = null;
		if (list == null || list.size() == 0)
			return;
		if (list.get(0) != null)
			firstInsert = (ArrayList) list.get(0);
		String colStr = null;
		repeatRowNum = repeatRowNum - 1;
		for (int i = 0; i < list.size(); i++) {
			rowList = (ArrayList) list.get(i);
			// 列的数量
			int colNum = rowList.size();
			String endstr = (String) rowList.get(colNum - 1);
			// 插入的行数
			repeatRowNum = repeatRowNum + 1;
			row = sheet.createRow(repeatRowNum);
			for (int j = 0; j < colNum; j++) {
				colStr = (String) rowList.get(j);
				cell = row.createCell((short) j);
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				try {
					double d = Double.parseDouble(colStr);
					cell.setCellValue(d);
				} catch (NumberFormatException ne) {
					cell.setCellValue(colStr);
				}
				if (cellStyle != null){
					cell.setCellStyle(cellStyle7);
				}
			} 
		}
	}


	public void addRepeatCell(int rowNum, short colNum, String value) {
		row = sheet.createRow(rowNum);
		cell = row.createCell(colNum);
		cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		try {
			double d = Double.parseDouble(value);
			cell.setCellValue(d);
		} catch (NumberFormatException ne) {
			cell.setCellValue(value);
		}
		if (cellStyle != null)
			cell.setCellStyle(cellStyle);
	}

	public void addSum() {
		HSSFRow row1 = sheet.createRow(initRowNum);
		HSSFRow row2 = sheet.createRow(repeatRowNum);
		HSSFRow row3 = sheet.createRow(repeatRowNum + 1);// 插入合计行
		HSSFCell insertcell = null;
		int initInsert = 0;
		boolean flag = true;
		String str = null;
		int notNumberIndex = 0;
		if (firstInsert == null || firstInsert.size() == 0)
			return;
		for (int i = 0; i < firstInsert.size(); i++) {
			str = (String) firstInsert.get(i);
			flag = true;
			try {
				double d = Double.parseDouble(str);
			} catch (NumberFormatException ne) {
				flag = false;
				notNumberIndex++;
			}

			if (flag == true) {
				insertcell = row3.createCell((short) i);
				CellReference cell1 = new CellReference(row1.getRowNum(), i);
				CellReference cell2 = new CellReference(row2.getRowNum(), i);
				insertcell.setCellFormula("SUM(" + cell1.toString() + ":"
						+ cell2.toString() + ")");
				if (cellStyle != null)
					insertcell.setCellStyle(cellStyle);
			} else if (notNumberIndex == 1) {
				HSSFCell sumcell = row3.createCell((short) i);
				sumcell.setEncoding(HSSFCell.ENCODING_UTF_16);
				sumcell.setCellValue("合  计");
				if (cellStyle != null)
					sumcell.setCellStyle(cellStyle);
			}
		}
		repeatRowNum++;
	}


	public void addRegion(int r1,int c1,String value,HSSFWorkbook wb,HSSFSheet sh){
		try{
			HSSFRow Rowbb = sh.getRow((short) r1);
			if(Rowbb == null){
				Rowbb = sh.createRow((short) r1);
			}
			sh.getMergedRegionAt(0);
			HSSFCell Cellaa = Rowbb.getCell((short)c1);
			if(Cellaa == null){
				Cellaa = Rowbb.createCell((short)c1);
			}
			Cellaa.setEncoding(HSSFCell.ENCODING_UTF_16);
		    Cellaa.setCellValue(value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void addCell(int r1,int c1,String value,HSSFWorkbook wb,HSSFSheet sh){
		HSSFRow Rowbb = sh.getRow((short) r1);
		HSSFCell Cellaa = Rowbb.getCell((short) c1);
		Cellaa.setEncoding(HSSFCell.ENCODING_UTF_16);
	    Cellaa.setCellValue(value);
	}
}