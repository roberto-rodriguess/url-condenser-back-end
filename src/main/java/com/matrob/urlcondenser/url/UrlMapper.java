package com.matrob.urlcondenser.url;

import com.matrob.urlcondenser.url.dto.UrlListResponseDTO;
import com.matrob.urlcondenser.url.dto.UrlResponseDTO;
import com.matrob.urlcondenser.url.dto.UrlStatsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring")
public abstract class UrlMapper {

    @Value("${app.base-url:http://localhost:8080}")
    @SuppressWarnings("unused")
    protected String baseUrl;

    @Mapping(target = "shortUrl", expression = "java(baseUrl + \"/\" + url.getShortCode())")
    public abstract UrlResponseDTO toResponseDTO(Url url);

    public abstract UrlStatsDTO toStatsDTO(Url url);

    @Mapping(target = "shortUrl", expression = "java(baseUrl + \"/\" + url.getShortCode())")
    @Mapping(target = "criador", source = "usuario.login")
    public abstract UrlListResponseDTO toListResponseDTO(Url url);

}
