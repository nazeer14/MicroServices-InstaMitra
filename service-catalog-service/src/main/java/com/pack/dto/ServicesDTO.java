package com.pack.dto;

import com.pack.entity.SubService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicesDTO {

    private String name;

    private String about;

    private List<SubService> subServices;

    private String category;


}
