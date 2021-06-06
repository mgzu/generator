package com.baomidou.mybatisplus.generator.config.extra;

import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 扩展文件生成
 *
 * @author mgzu
 * @since 2021-06-06
 */
public interface ExtraGenerator {

    ExtraInfo extraGenerator(@NotNull String basePath, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap);

}
