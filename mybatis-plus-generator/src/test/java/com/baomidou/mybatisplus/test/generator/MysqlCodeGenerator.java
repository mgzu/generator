package com.baomidou.mybatisplus.test.generator;

import com.baomidou.mybatisplus.generator.SimpleAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.extra.ExtraGenerator;
import com.baomidou.mybatisplus.generator.config.extra.ExtraInfo;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

/**
 * @author mgzu
 * @since 2021-06-06
 */
@Slf4j
public class MysqlCodeGenerator {

    public static void main(String[] args) {
        new SimpleAutoGenerator() {
            @Override
            public IConfigBuilder<DataSourceConfig> dataSourceConfigBuilder() {
                String url = "jdbc:mysql://127.0.0.1:3306/official?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2b8";
                String username = "root";
                String password = "123456";

                return new DataSourceConfig.Builder(url, username, password);
            }

            @Override
            public PackageConfig.Builder packageConfigBuilder() {
                return new PackageConfig.Builder().parent("com.abc").moduleName("dict");
            }

            @Override
            public IConfigBuilder<StrategyConfig> strategyConfigBuilder() {
                return new StrategyConfig.Builder().addTablePrefix("t_").addInclude("t_dict")
                    ;
            }

            @Override
            public TemplateConfig.Builder templateConfigBuilder() {
                return new TemplateConfig.Builder().mapperExtra(new ExtraGenerator() {
                    @Override
                    public ExtraInfo extraGenerator(@NotNull String basePath, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
                        String templatePath = "/templates/mapperImpl.java";
                        String outputPath = String.format("%s%s%s%s%s%s", basePath, File.separator, "impl" , File.separator , tableInfo.getMapperName() , "Impl");
                        return new ExtraInfo(templatePath, outputPath);
                    }
                });
            }

            @Override
            public GlobalConfig.Builder globalConfigBuilder() {
                return new GlobalConfig.Builder().author("132").outputDir("./").fileOverride()
                    ;
            }
        }.execute();
    }
}
