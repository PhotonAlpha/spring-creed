package com.ethan.upload.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import com.ethan.common.exception.BizException;
import com.ethan.common.util.DateUtil;
import com.ethan.common.util.RedisUtil;
import com.ethan.common.util.YmlUtil;
import com.ethan.upload.concurrent.FileCallable;
import com.ethan.upload.constant.FileConstant;
import com.ethan.upload.dto.FileUploadDTO;
import com.ethan.upload.dto.FileUploadRequestDTO;
import com.ethan.upload.enu.FileCheckMd5Status;
import com.ethan.upload.service.FileService;
import com.ethan.upload.strategy.enu.UploadModeEnum;
import com.ethan.upload.util.FileMD5Util;
import com.ethan.upload.util.FilePathUtil;
import com.ethan.upload.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private FilePathUtil filePathUtil;


  private AtomicInteger atomicInteger = new AtomicInteger(0);


  private final ExecutorService executorService = Executors.newFixedThreadPool(
      Integer.valueOf(YmlUtil.getValue("upload.thread.maxSize").toString()),(r)->{
        String threadName = "uploadPool-"+atomicInteger.getAndIncrement();
        Thread thread = new Thread(r);
        thread.setName(threadName);
        return thread;
      });

  private final CompletionService<FileUploadDTO> completionService = new ExecutorCompletionService<>(executorService,
      new LinkedBlockingDeque<>(Integer.valueOf(YmlUtil.getValue("upload.queue.maxSize").toString())));


  @Override
  public FileUploadDTO upload(FileUploadRequestDTO param) throws IOException {

    if (Objects.isNull(param.getFile())) {
      throw new BizException("file can not be empty", 404);
    }
    param.setPath(FileUtil.withoutHeadAndTailDiagonal(param.getPath()));
    String md5 = FileMD5Util.getFileMD5(param.getFile());
    param.setMd5(md5);

    String filePath = filePathUtil.getPath(param);
    File targetFile = new File(filePath);
    if (!targetFile.exists()) {
      targetFile.mkdirs();
    }
    String path = filePath + FileConstant.FILE_SEPARATORCHAR + param.getFile().getOriginalFilename();
    FileOutputStream out = new FileOutputStream(path);
    out.write(param.getFile().getBytes());
    out.flush();
    FileUtil.close(out);

    redisUtil.hset(FileConstant.FILE_UPLOAD_STATUS, md5, "true");

    return FileUploadDTO.builder().path(path).mtime(DateUtil.getCurrentTimeStamp()).uploadComplete(true).build();
  }

  @Override
  public FileUploadDTO sliceUpload(FileUploadRequestDTO fileUploadRequestDTO){

    try {
      completionService.submit(new FileCallable(UploadModeEnum.RANDOM_ACCESS,fileUploadRequestDTO));

      FileUploadDTO fileUploadDTO = completionService.take().get();
      return fileUploadDTO;
    } catch (InterruptedException e) {
      log.error(e.getMessage(),e);
      throw new BizException(e.getMessage(),406);
    } catch (ExecutionException e) {
      log.error(e.getMessage(),e);
      throw new BizException(e.getMessage(),406);
    }
  }

  @Override
  public FileUploadDTO checkFileMd5(FileUploadRequestDTO param) throws IOException{
    Object uploadProgressObj = redisUtil.hget(FileConstant.FILE_UPLOAD_STATUS, param.getMd5());
    if (uploadProgressObj == null) {
      FileUploadDTO fileMd5DTO = FileUploadDTO.builder()
          .code(FileCheckMd5Status.FILE_NO_UPLOAD.getValue()).build();
      return fileMd5DTO;
    }
    String processingStr = uploadProgressObj.toString();
    boolean processing = Boolean.parseBoolean(processingStr);
    String value = String.valueOf(redisUtil.get(FileConstant.FILE_MD5_KEY + param.getMd5()));
    return fillFileUploadDTO(param, processing, value);
  }

  /**
   * 填充返回文件内容信息
   */
  private FileUploadDTO fillFileUploadDTO(FileUploadRequestDTO param, boolean processing,
      String value) throws IOException {

    if (processing) {
      param.setPath(FileUtil.withoutHeadAndTailDiagonal(param.getPath()));
      String path = filePathUtil.getPath(param);
      return FileUploadDTO.builder().code(FileCheckMd5Status.FILE_UPLOADED.getValue())
          .path(path).build();
    } else {
      File confFile = new File(value);
      byte[] completeList = FileUtils.readFileToByteArray(confFile);
      List<Integer> missChunkList = new LinkedList<>();
      for (int i = 0; i < completeList.length; i++) {
        if (completeList[i] != Byte.MAX_VALUE) {
          missChunkList.add(i);
        }
      }
      return FileUploadDTO.builder().code(FileCheckMd5Status.FILE_UPLOAD_SOME.getValue())
          .missChunks(missChunkList).build();
    }
  }


}
