package com.example.thirdparty.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sally
 * @date 2022-07-22 2:07 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String urlHttp;

    private String urlPath;
}