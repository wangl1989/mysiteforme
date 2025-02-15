package com.mysiteforme.admin.entity.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PermissionPageVO  {

    private Long id;

    private String pageUrl;

}
