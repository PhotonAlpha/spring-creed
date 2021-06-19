package com.ethan.upload.strategy;


import com.ethan.upload.dto.FileUploadDTO;
import com.ethan.upload.dto.FileUploadRequestDTO;

public interface SliceUploadStrategy {

  FileUploadDTO sliceUpload(FileUploadRequestDTO param);
}
