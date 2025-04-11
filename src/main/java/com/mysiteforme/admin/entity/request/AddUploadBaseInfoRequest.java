package com.mysiteforme.admin.entity.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.mysiteforme.admin.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddUploadBaseInfoRequest extends UploadBaseInfoRequest{

}
