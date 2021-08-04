package com.viettel.mve.authservice.common.importUtil;

import java.util.List;

public class ImportValidator {

    public String validate(ImportConfigBean columnConfig, String content, int row, int col) {
        return null;
    }

    public String validate(
            ImportConfigBean columnConfig,
            String content,
            int row,
            int col,
            List<ImportErrorBean> errorList
    ) {
        return null;
    }

    public String validateNull(
            ImportConfigBean columnConfig,
            String content,
            int row,
            int col,
            List<ImportErrorBean> errorList
    ) {
        return null;
    }

}