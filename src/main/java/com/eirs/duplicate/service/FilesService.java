package com.eirs.duplicate.service;

import java.io.File;

public interface FilesService {

    File[] getFiles(String filePattern);

    void moveFile(File file);

    public void moveFileToCompleted(File file);

}

