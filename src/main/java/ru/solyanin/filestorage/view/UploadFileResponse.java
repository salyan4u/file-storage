package ru.solyanin.filestorage.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponse {
    private UUID id;
    private String name;
    private String fileDownloadUri;
    private String type;
    private Long size;
}
