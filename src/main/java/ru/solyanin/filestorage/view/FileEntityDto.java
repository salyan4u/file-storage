package ru.solyanin.filestorage.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class FileEntityDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;

    private String path;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long size;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String type;
}