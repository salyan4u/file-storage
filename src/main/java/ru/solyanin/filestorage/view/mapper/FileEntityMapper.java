package ru.solyanin.filestorage.view.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ru.solyanin.filestorage.model.FileEntity;
import ru.solyanin.filestorage.view.FileEntityDto;


@Mapper(componentModel = "spring")
public interface FileEntityMapper {
    FileEntityMapper INSTANCE = Mappers.getMapper(FileEntityMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "path", target = "path"),
            @Mapping(source = "size", target = "size"),
            @Mapping(source = "type", target = "type")
    })
    FileEntityDto toDto(FileEntity region);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "path", target = "path"),
            @Mapping(source = "size", target = "size"),
            @Mapping(source = "type", target = "type")
    })
    FileEntity toEntity(FileEntityDto regionDto);
}