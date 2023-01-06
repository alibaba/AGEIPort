package com.alibaba.ageiport.processor.core.test;

import com.alibaba.ageiport.processor.core.AgeiPort;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


@Slf4j
public class TestHelper {

    private AgeiPort ageiPort;

    public TestHelper(AgeiPort ageiPort) {
        this.ageiPort = ageiPort;
    }

    public String file(String fileName) {
        return "." + File.separator + "files" + File.separator + "import-xlsx" + File.separator + fileName;
    }


}
