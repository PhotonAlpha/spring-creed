package com.ethan.upload.service;

import com.ethan.upload.dto.FileUploadDTO;
import com.ethan.upload.dto.FileUploadRequestDTO;

import java.io.IOException;

public interface FileService {

  FileUploadDTO upload(FileUploadRequestDTO fileUploadRequestDTO)throws IOException;

  FileUploadDTO sliceUpload(FileUploadRequestDTO fileUploadRequestDTO);

  FileUploadDTO checkFileMd5(FileUploadRequestDTO fileUploadRequestDTO)throws IOException;

}
