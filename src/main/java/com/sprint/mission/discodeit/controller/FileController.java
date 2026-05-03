package com.sprint.mission.discodeit.controller;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/files")
public class FileController {

  // Single file Download
  @GetMapping("/{fileName}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName)
      throws MalformedURLException {
    Path filePath = Paths.get("${file.upload-dir}").resolve(fileName).normalize();
    Resource resource = new UrlResource(filePath.toUri());

    String contentDisposition = "attachment; filename=\"" + resource.getFilename() + "\"";

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", contentDisposition)
        .body(resource);
  }

  // Multiple file Download (Simultaneous Download)
  @PostMapping("/download/zip")
  public ResponseEntity<Resource> downloadMultipleFiles(@RequestBody List<String> fileNames)
      throws IOException {

    // create zip file
    Path tempZip = Files.createTempFile("download-", ".zip");
    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(tempZip))) {
      for (String filename : fileNames) {
        Path filePath = Paths.get("${file.upload-dir}").resolve(filename).normalize();
        zos.putNextEntry(new ZipEntry(filename));
        Files.copy(filePath, zos);
        zos.closeEntry();
      }
    }
    Resource resource = new UrlResource(tempZip.toUri());
    String contentDisposition = "attachment; filename=\"files.zip\"";
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .body(resource);
  }
}




