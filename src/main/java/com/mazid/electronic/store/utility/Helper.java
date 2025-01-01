package com.mazid.electronic.store.utility;

import com.mazid.electronic.store.dataTransferObjects.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class Helper {
    public static <U,V> PageableResponse<V> getPageableResponse(Page<U> page,Class<V> type){
        //List<U> entity=page.getContent();
        //List<V> dtoList= entity.stream().map(object -> new ModelMapper().map(object,type)).toList();
        List<U> entity = page.getContent();
        List<V> dtoList = entity.stream()
                .map(object -> {
                    ModelMapper mapper = new ModelMapper();
                    V dto = mapper.map(object, type);
                    if (dto instanceof UserDto) {
                        ((UserDto) dto).setPassword(null);
                    }
                    return dto;
                })
                .toList();
        PageableResponse<V> response= new PageableResponse<>();
        response.setContent(dtoList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;

    }
}
