package com.viettel.mve.authservice.common.importUtil;

import java.awt.Color;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jxcell.CellException;
import com.jxcell.CellFormat;
import com.jxcell.View;

public class DynamicExport {

    /**
     * Format cho header
     */
    public static final int HEADER_FORMAT = 0;
    /**
     * Format tao border
     */
    public static final int BORDER_FORMAT = 1;
    /**
     * Tieu de
     */
    public static final int TITLE = 2;
    /**
     * Format group cap 1
     */
    public static final int GROUP_LEVEL1_FORMAT = 3;
    /**
     * Format group cap 2
     */
    public static final int GROUP_LEVEL2_FORMAT = 4;
    /**
     * Format group cap 3
     */
    public static final int GROUP_LEVEL3_FORMAT = 5;
    /**
     * Format group cap 4
     */
    public static final int GROUP_LEVEL4_FORMAT = 6;
    /**
     * Format group cap 5
     */
    public static final int GROUP_LEVEL5_FORMAT = 7;
    /**
     * Format group cap 6
     */
    public static final int GROUP_LEVEL6_FORMAT = 8;
    /* Format number .*/
    /**
     *
     */
    public static final int ACCOUNTING_FORMAT = 9;
    /* Format number .*/
    /**
     *
     */
    public static final int NUMBER_FORMAT = 10;
    /* Format number double.*/
    /**
     *
     */
    public static final int NUMBER_FORMAT_D = 11;
    /* Format tao border voi vien Den.*/
    /**
     *
     */
    public static final int BLACK_BORDER_FORMAT = 12;
    // SonPN added on 2012-05-14
    /**
     *
     */
    public static final int BOLD_WHITE = 20;
    /**
     *
     */
    public static final int BOLD_WHITE_CENTER = 21;
    /**
     *
     */
    public static final int NOMARL_WHITE_CENTER = 22;
    //khanhnq16 2012-05-14
    /**
     *
     */
    public static final int NORMAL_ITALIC = 23;
    /**
     *
     */
    public static final int GROUP_DATA_FORMAT = 24;
    /**
     *
     */
    public static final int PERCENT_FORMAT = 25;
    /**
     *
     */
    public static final int CENTER_FORMAT = 26;
    /**
     *
     */
    public static final int BOLD_CENTER_FORMAT = 27;
    /**
     *
     */
    public static final int BOLD_FORMAT = 28;
    // Align text to middle of cell
    /**
     *
     */
    public static final int CENTER_VERTICAL_FORMAT = 29;
    // Shrink to fit
    /**
     *
     */
    public static final int SHRINK_TO_FIT = 30;
    // Header orange
    /**
     *
     */
    public static final int HEADER_ORANGE = 31;
    // Header yellow
    /**
     *
     */
    public static final int HEADER_YELLOW = 32;
    /**
     *
     */
    public static final int BORDER_FORMAT_NON_WRAP = 33;
    // Left alignment
    /**
     *
     */
    public static final int ALIGN_LEFT = 34;
    // Cell color
    /**
     *
     */
    public static final int CELL_COLOR_YELLOW = 35;


    // Left alignment
    /**
     *
     */
    public static final int ALIGN_RIGHT = 36;
    // No wrap text
    /**
     *
     */
    public static final int NO_WRAP_TEXT = 37;
    /**
     *
     */
    public static final int BLACK_BORDER_NO_HORIZONTAL_NONE_WRAP = 38;
    /**
     *
     */
    public static final int BLACK_BORDER_NO_HORIZONTAL = 39;
    /**
     *
     */
    public static final int BORDER_FORMAT_NO_ROW_HEIGHT = 40;
    /**
     *
     */
    public static final int MERGE_CELL = 41;
    //#095 Start
    public static final int COLOR_RED = 42;
    public static final int NO_WORK_PROCESS = 43;
    public static final int PAST_LOCK = 44;
    public static final int ORG_LOCK = 45;
    public static final int CELL_COLOR_GREEN = 46;
    //#095 End
    /**
     * Doi tuong de tuong tac voi file Excel
     */
    private final View view;

    /**
     * Dong du lieu cuoi cung, moi nhat, hien tai
     */
    private int lastRow;
    private boolean isXLSX;
//    public static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);


    public DynamicExport(InputStream templateFile, int startDataRow, boolean isXLSX)
            throws Exception {
        this.lastRow = startDataRow - 1;
        view = new View();
        this.isXLSX = isXLSX;
        if (isXLSX) {
            view.readXLSX(templateFile);
        } else {
            view.read(templateFile);
        }
    }

    /**
     * Ghi ra file Excel.
     *
     * @param exportFile File Excel xuat ra
     * @param req
     * @return
     * @throws Exception Exception
     */
    public String exportFile(String exportPath, String exportFile) throws Exception {
        String prefixOutPutFile = new SimpleDateFormat("yyyyMMddHHmmss_").format(new Date());
        exportFile = prefixOutPutFile + exportFile;
        String fullPathFile;
        if (isXLSX) {
            fullPathFile = exportPath + exportFile + ".xlsx";
            view.writeXLSX(fullPathFile);
        } else {
            fullPathFile = exportPath + exportFile + ".xls";
            view.write(fullPathFile);
        }
        return fullPathFile;
    }


    /**
     * Lay dong hien tai, dong cuoi cung.
     *
     * @return Dong cuoi cung
     */
    public int getLastRow() {
        return lastRow;
    }
 

    /**
     * Format cell
     *
     * @param r1     Top
     * @param c1     Left
     * @param r2     Bottom
     * @param c2     Right
     * @param format
     * @throws com.jxcell.CellException
     */
    public void setCellFormat(int r1, int c1, int r2, int c2, CellFormat format)
            throws CellException {
        view.setCellFormat(format, r1, c1, r2, c2);
    }



    /**
     * @param r1
     * @param c1
     * @param r2
     * @param c2
     * @param formatType
     * @throws CellException
     */
    public void setCellFormat(int r1, int c1, int r2, int c2, int formatType)
            throws CellException {
        if (r1 <= r2 && c1 <= c2) {
            CellFormat format = view.getCellFormat(r1, c1, r2, c2);
            if (formatType == HEADER_FORMAT) {
                //<editor-fold defaultstate="collapsed" desc="Header cua bang">
                short border = CellFormat.BorderThin;
                format.setLeftBorder(border);
                format.setRightBorder(border);
                format.setTopBorder(border);
                format.setBottomBorder(border);
                format.setHorizontalInsideBorder(border);
                format.setVerticalInsideBorder(border);

                Color borderColor = Color.GREEN.darker();
                format.setLeftBorderColor(borderColor);
                format.setRightBorderColor(borderColor);
                format.setTopBorderColor(borderColor);
                format.setBottomBorderColor(borderColor);

                format.setFontBold(true);
                format.setPattern((short) 1);
                format.setPatternFG(new Color(254, 252, 172));
                format.setWordWrap(true);

                format.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
                //</editor-fold>
            } else if (formatType == BORDER_FORMAT) {
                //<editor-fold defaultstate="collapsed" desc="Border cho du lieu binh thuong">
                short border = CellFormat.BorderThin;
                format.setLeftBorder(border);
                format.setRightBorder(border);
                format.setTopBorder(border);
                format.setBottomBorder(border);
                format.setHorizontalInsideBorder(border);
                format.setVerticalInsideBorder(border);

                Color borderColor = Color.GREEN.darker();
                format.setLeftBorderColor(borderColor);
                format.setRightBorderColor(borderColor);
                format.setTopBorderColor(borderColor);
                format.setBottomBorderColor(borderColor);

                //format.setFontBold(false);
                view.setRowHeightAuto(r1, c1, r2, c2, true);
                //view.setRowHeight(r1, r2, 200, true, true);
                format.setWordWrap(true);
                //</editor-fold>
            } else if (formatType == BORDER_FORMAT_NON_WRAP) {
                //<editor-fold defaultstate="collapsed" desc="Border cho du lieu binh thuong">
                short border = CellFormat.BorderThin;
                format.setLeftBorder(border);
                format.setRightBorder(border);
                format.setTopBorder(border);
                format.setBottomBorder(border);
                format.setHorizontalInsideBorder(border);
                format.setVerticalInsideBorder(border);

                Color borderColor = Color.GREEN.darker();
                format.setLeftBorderColor(borderColor);
                format.setRightBorderColor(borderColor);
                format.setTopBorderColor(borderColor);
                format.setBottomBorderColor(borderColor);

                //format.setFontBold(false);
                format.setPattern((short) 1);
                //view.setRowHeightAuto(r1, c1, r2, c2, true);
                view.setRowHeight(r1, r2, 200, true, true);
                format.setWordWrap(false);
                //</editor-fold>
            } else if (formatType == BLACK_BORDER_FORMAT) {
                //<editor-fold defaultstate="collapsed" desc="Border cho du lieu binh thuong">
                short border = CellFormat.BorderThin;
                format.setLeftBorder(border);
                format.setRightBorder(border);
                format.setTopBorder(border);
                format.setBottomBorder(border);
                format.setHorizontalInsideBorder(border);
                format.setVerticalInsideBorder(border);

                Color borderColor = Color.BLACK.darker();
                format.setLeftBorderColor(borderColor);
                format.setRightBorderColor(borderColor);
                format.setTopBorderColor(borderColor);
                format.setBottomBorderColor(borderColor);

                //format.setFontBold(false);
                format.setWordWrap(true);
                //</editor-fold>
            } else if (formatType == TITLE) {
                format.setFontBold(true);
                format.setPattern((short) 1);
                format.setPatternFG(Color.GREEN);
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
            } else if (formatType == GROUP_LEVEL1_FORMAT) {
                format.setFontBold(true);
                format.setPattern((short) 1);
                format.setPatternFG(Color.ORANGE);
            } else if (formatType == GROUP_LEVEL2_FORMAT) {
                format.setFontBold(true);
                format.setFontItalic(true);
                format.setPattern((short) 1);
                format.setPatternFG(new Color(135, 206, 250));
            } else if (formatType == GROUP_LEVEL3_FORMAT) {
                format.setFontBold(true);
                format.setFontItalic(true);
                format.setPattern((short) 1);
                format.setPatternFG(new Color(10, 175, 255));
            } else if (formatType == GROUP_LEVEL4_FORMAT) {
                format.setFontBold(true);
                format.setFontItalic(true);
                format.setPattern((short) 1);
                format.setPatternFG(new Color(209, 232, 170));
            } else if (formatType == GROUP_LEVEL5_FORMAT) {
                format.setFontBold(true);
                format.setFontItalic(true);
                format.setPattern((short) 1);
                format.setPatternFG(new Color(250, 250, 210));
            } else if (formatType == GROUP_LEVEL6_FORMAT) {
                format.setFontBold(true);
                format.setFontItalic(true);
                format.setPattern((short) 1);
                format.setPatternFG(new Color(209, 238, 238));
            } else if (formatType == ACCOUNTING_FORMAT) {
                String numberFormat = "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)";
                format.setCustomFormat(numberFormat);
            } else if (formatType == NUMBER_FORMAT) {
                String numberFormat = "#,##0";
                format.setCustomFormat(numberFormat);
            } else if (formatType == NUMBER_FORMAT_D) {
                String numberFormat = "#,##0.00";
                format.setCustomFormat(numberFormat);
            } else if (formatType == BOLD_WHITE) { // SonPN
                format.setFontBold(true);
                format.setPattern((short) 1);
                format.setPatternFG(Color.WHITE);
                //format.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);
            } else if (formatType == CENTER_FORMAT) {
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
            } else if (formatType == BOLD_CENTER_FORMAT) {
                format.setFontBold(true);
                format.setWordWrap(true);
                format.setVerticalAlignment((short) 1);
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
            } else if (formatType == BOLD_FORMAT) {
                format.setFontBold(true);
            } else if (formatType == BOLD_WHITE_CENTER) { // SonPN
                format.setFontBold(true);
                format.setPattern((short) 1);
                //            format.setPatternFG(Color.WHITE);
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
            } else if (formatType == NOMARL_WHITE_CENTER) { // SonPN
                format.setFontBold(false);
                format.setPattern((short) 1);
                format.setPatternFG(Color.WHITE);
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
            } else if (formatType == NORMAL_ITALIC) {
                format.setFontItalic(true);
            } else if (formatType == GROUP_DATA_FORMAT) {
                format.setPattern((short) 1);
                format.setPatternFG(Color.YELLOW);
            } else if (formatType == PERCENT_FORMAT) {
                format.setFontBold(true);
                //            format.setFontItalic(true);
                format.setPattern((short) 1);
                //            format.setPatternFG(new Color(209, 232, 170));
                String percentFormat = "#,##" + "%";
                format.setCustomFormat(percentFormat);
            } else if (formatType == CENTER_VERTICAL_FORMAT) {
                format.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);
            } else if (formatType == SHRINK_TO_FIT) {
                format.setShrinkToFit(true);
            } else if (formatType == HEADER_ORANGE) {
                format.setFontBold(true);
                format.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
                format.setPattern((short) 1);
                format.setPatternFG(Color.ORANGE);
            } else if (formatType == HEADER_YELLOW) {
                format.setFontBold(true);
                format.setVerticalAlignment(CellFormat.VerticalAlignmentCenter);
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentCenter);
                format.setPattern((short) 1);
                format.setPatternFG(Color.YELLOW);
            } else if (formatType == ALIGN_LEFT) {
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentLeft);
            } else if (formatType == ALIGN_RIGHT) {
                format.setHorizontalAlignment(CellFormat.HorizontalAlignmentRight);
            } else if (formatType == CELL_COLOR_YELLOW) {
                format.setPattern((short) 1);
                format.setPatternFG(Color.YELLOW);
            } else if (formatType == CELL_COLOR_GREEN) {
                format.setPattern((short) 1);
                format.setPatternFG(Color.GREEN);
            } else if (formatType == NO_WRAP_TEXT) {
                format.setWordWrap(false);
            } else if (formatType == BLACK_BORDER_NO_HORIZONTAL_NONE_WRAP) {
                short border = CellFormat.BorderThin;
                format.setLeftBorder(border);
                format.setRightBorder(border);
                format.setTopBorder(border);
                format.setBottomBorder(border);
                format.setVerticalInsideBorder(border);
                Color borderColor = Color.BLACK.darker();
                format.setLeftBorderColor(borderColor);
                format.setRightBorderColor(borderColor);
                format.setTopBorderColor(borderColor);
                format.setBottomBorderColor(borderColor);
                format.setWordWrap(false);
            } else if (formatType == BLACK_BORDER_NO_HORIZONTAL) {
                short border = CellFormat.BorderThin;
                format.setLeftBorder(border);
                format.setRightBorder(border);
                format.setTopBorder(border);
                format.setBottomBorder(border);
                format.setVerticalInsideBorder(border);
                Color borderColor = Color.BLACK.darker();
                format.setLeftBorderColor(borderColor);
                format.setRightBorderColor(borderColor);
                format.setTopBorderColor(borderColor);
                format.setBottomBorderColor(borderColor);
                format.setWordWrap(true);
            } else if (formatType == BORDER_FORMAT_NO_ROW_HEIGHT) {
                short border = CellFormat.BorderThin;
                format.setLeftBorder(border);
                format.setRightBorder(border);
                format.setTopBorder(border);
                format.setBottomBorder(border);
                format.setHorizontalInsideBorder(border);
                format.setVerticalInsideBorder(border);
                Color borderColor = Color.GREEN.darker();
                format.setLeftBorderColor(borderColor);
                format.setRightBorderColor(borderColor);
                format.setTopBorderColor(borderColor);
                format.setBottomBorderColor(borderColor);
                format.setWordWrap(true);
                //            view.setRowHeightAuto(r1, c1, r2, c2, true);
                //#095 Start
            } else if (formatType == MERGE_CELL) {
                format.setMergeCells(true);
            } else if (formatType == COLOR_RED) {
                format.setFontColor(Color.RED);
            } else if (formatType == NO_WORK_PROCESS) {
                format.setPattern((short) 1);
                format.setPatternFG(Color.PINK);
            } else if (formatType == PAST_LOCK) {
                format.setPattern((short) 1);
                format.setPatternFG(Color.GRAY);
            } else if (formatType == ORG_LOCK) {
                format.setPattern((short) 1);
                format.setPatternFG(Color.lightGray);
            }
            //#095 End
            view.setCellFormat(format, r1, c1, r2, c2);
        }
        //#042 End
    }

    /**
     * Thiet lap gia tri cho cell o dong hien tai.
     *
     * @param text   Gia tri
     * @param column Cot
     * @throws CellException CellException
     */
    public void setEntry(String text, int column)
            throws CellException {
        if (text != null && !text.isEmpty()) {
            view.setTextAsValue(lastRow, column, text);
        }
    }

    /**
     * Thiet lap gia tri cho cell o dong row.
     *
     * @param text   Gia tri
     * @param column Cot
     * @param row    Dong
     * @throws CellException CellException
     */
    public void setEntry(String text, int column, int row)
            throws CellException {
        if (text != null && !text.isEmpty()) {
            view.setTextAsValue(row, column, text);
        }
    }

    /**
     * Thiet lap gia tri cho cell o dong row.
     *
     * @param text   Gia tri
     * @param column Cot
     * @param row    Dong
     * @throws CellException CellException
     */
    public void setText(String text, int column, int row)
            throws CellException {
        if (text != null && !text.isEmpty()) {
            view.setText(row, column, text);
        }
    }

    /**
     * Thiet lap cong thuc cho cell.
     *
     * @param text   Gia tri
     * @param column Cot
     * @param row    Dong
     * @throws CellException CellException
     */
    public void setFormula(String text, int column, int row)
            throws CellException {
        view.setFormula(row, column, text);
        //view.recalc();
    }

    /**
     * @param sheetIndex
     * @throws Exception
     */
    public void setActiveSheet(int sheetIndex) throws Exception {
        view.setSheet(sheetIndex);
        view.setSheetSelected(sheetIndex, true);
    }

    /**
     * clearCell
     *
     * @param row
     * @param col
     * @throws CellException
     */
    public void clearCell(int row, int col) throws CellException {
        view.setText(row, col, "");
    }

    /**
     * getCellText
     *
     * @param row
     * @param col
     * @return
     * @throws CellException
     */
    public String getCellText(int row, int col) throws CellException {
        return view.getEntry(row, col);
    }

}
