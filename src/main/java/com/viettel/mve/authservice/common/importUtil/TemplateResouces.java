package com.viettel.mve.authservice.common.importUtil;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

public class TemplateResouces {
    /**
     * getImportFile
     * @param string
     * @param req
     * @return
     * @throws IOException 
     */
    public static InputStream getImportFile(String path) throws IOException {
        return new ClassPathResource("public/template/import/" + path).getInputStream();
    }
}