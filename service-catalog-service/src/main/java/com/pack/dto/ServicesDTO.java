package com.pack.dto;

import com.pack.entity.SubService;
import lombok.Data;

import java.util.List;

@Data
public class ServicesDTO {

    private String name;

    private String about;

    private List<SubService> subServices;

    private String category;

}
