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

    private Long id;

    private String name;

    private String code;

    private String about;

    private String category;

}
