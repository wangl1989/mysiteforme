package com.mysiteforme.admin.entity.DTO;

import com.mysiteforme.admin.entity.Dict;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DictDTO extends Dict {

    private String oldType;

    private String newType;

}
